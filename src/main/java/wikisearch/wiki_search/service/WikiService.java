package wikisearch.wiki_search.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wikisearch.wiki_search.entity.*;
import wikisearch.wiki_search.repository.*;
import wikisearch.wiki_search.dto.WikiArticleDto;
import org.springframework.web.client.RestTemplate;
import wikisearch.wiki_search.cache.SimpleCache;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WikiService {
    private final SearchHistoryRepository historyRepo;
    private final WikiArticleRepository articleRepo;
    private final SimpleCache cache;

    public WikiService(SearchHistoryRepository historyRepo, WikiArticleRepository articleRepo, SimpleCache cache) {
        this.historyRepo = historyRepo;
        this.articleRepo = articleRepo;
        this.cache = cache;
    }

    public List<WikiArticleDto> getAllArticles() {
        return articleRepo.findAll().stream()
            .map(a -> new WikiArticleDto(a.getId(), a.getTitle(), a.getContent()))
            .collect(Collectors.toList());
    }

    public WikiArticleDto getArticleById(Long id) {
        return articleRepo.findById(id)
            .map(a -> new WikiArticleDto(a.getId(), a.getTitle(), a.getContent()))
            .orElse(null);
    }

    public WikiArticleDto createArticle(WikiArticle article) {
        WikiArticle saved = articleRepo.save(article);
        return new WikiArticleDto(saved.getId(), saved.getTitle(), saved.getContent());
    }

    public WikiArticleDto updateArticle(Long id, WikiArticle article) {
        article.setId(id);
        WikiArticle saved = articleRepo.save(article);
        return new WikiArticleDto(saved.getId(), saved.getTitle(), saved.getContent());
    }

    public void deleteArticle(Long id) {
        articleRepo.deleteById(id);
    }

    @Transactional
    public List<WikiArticleDto> findByTermAndSaveHistory(String term) {
        @SuppressWarnings("unchecked")
        List<WikiArticleDto> cached = (List<WikiArticleDto>) cache.get("term:" + term);
        if (cached != null) {
            return cached;
        }
        List<WikiArticle> articles = articleRepo.findByTerm(term);
        if (!articles.isEmpty()) {
            SearchHistory history = historyRepo.findBySearchTerm(term);
            if (history == null) {
                history = new SearchHistory();
                history.setSearchTerm(term);
                history.setTimestamp(java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            }
            for (WikiArticle article : articles) {
                article.setHistory(history);
            }
            history.setArticles(articles);
            historyRepo.save(history);
        }
        List<WikiArticleDto> result = articles.stream()
            .map(a -> new WikiArticleDto(a.getId(), a.getTitle(), a.getContent()))
            .collect(java.util.stream.Collectors.toList());
        cache.put("term:" + term, result);
        return result;
    }

    public WikiArticleDto searchAndSaveFromWiki(String term) {
        String url = "https://en.wikipedia.org/w/api.php?action=query&format=json&prop=extracts&exintro=true&explaintext=true&titles=" + term;
        String response = new RestTemplate().getForObject(url, String.class);
        String extract;
        if (response != null && response.contains("extract")) {
            extract = response.split("\"extract\":\"")[1].split("\"")[0];
        } else {
            extract = "No results found";
        }
        WikiArticle article = new WikiArticle(term, extract);
        WikiArticle saved = articleRepo.save(article);
        return new WikiArticleDto(saved.getId(), saved.getTitle(), saved.getContent());
    }
}