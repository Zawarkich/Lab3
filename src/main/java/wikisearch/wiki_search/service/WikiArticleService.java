package wikisearch.wiki_search.service;

import org.springframework.stereotype.Service;
import wikisearch.wiki_search.entity.WikiArticle;
import wikisearch.wiki_search.repository.WikiArticleRepository;
import wikisearch.wiki_search.dto.WikiArticleDto;
import wikisearch.wiki_search.cache.SimpleCache;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WikiArticleService {
    private final WikiArticleRepository articleRepo;
    private final SimpleCache cache;

    public WikiArticleService(WikiArticleRepository articleRepo, SimpleCache cache) {
        this.articleRepo = articleRepo;
        this.cache = cache;
    }

    public List<WikiArticleDto> getAllArticles() {
        @SuppressWarnings("unchecked")
        List<WikiArticleDto> cached = (List<WikiArticleDto>) cache.get("all_articles");
        if (cached != null) {
            return cached;
        }
        List<WikiArticleDto> result = articleRepo.findAll().stream()
            .map(a -> new WikiArticleDto(a.getId(), a.getTitle(), a.getContent()))
            .collect(Collectors.toList());
        cache.put("all_articles", result);
        return result;
    }

    public WikiArticleDto getArticleById(Long id) {
        String key = "article:" + id;
        WikiArticleDto cached = (WikiArticleDto) cache.get(key);
        if (cached != null) {
            return cached;
        }
        WikiArticleDto result = articleRepo.findById(id)
            .map(a -> new WikiArticleDto(a.getId(), a.getTitle(), a.getContent()))
            .orElse(null);
        if (result != null) {
            cache.put(key, result);
        }
        return result;
    }

    public WikiArticleDto createArticle(WikiArticle article) {
        WikiArticle saved = articleRepo.save(article);
        WikiArticleDto dto = new WikiArticleDto(saved.getId(), saved.getTitle(), saved.getContent());
        cache.clear(); 
        return dto;
    }

    public WikiArticleDto updateArticle(Long id, WikiArticle article) {
        article.setId(id);
        WikiArticle saved = articleRepo.save(article);
        WikiArticleDto dto = new WikiArticleDto(saved.getId(), saved.getTitle(), saved.getContent());
        cache.clear(); 
        return dto;
    }

    public void deleteArticle(Long id) {
        articleRepo.deleteById(id);
        cache.clear(); 
    }

    public List<WikiArticleDto> findByTerm(String term) {
        @SuppressWarnings("unchecked")
        List<WikiArticleDto> cached = (List<WikiArticleDto>) cache.get("term:" + term);
        if (cached != null) {
            return cached;
        }
        List<WikiArticleDto> result = articleRepo.findByTerm(term).stream()
            .map(a -> new WikiArticleDto(a.getId(), a.getTitle(), a.getContent()))
            .collect(Collectors.toList());
        cache.put("term:" + term, result);
        return result;
    }
}
