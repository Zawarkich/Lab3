package wikisearch.wiki_search.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wikisearch.wiki_search.cache.SimpleCache;
import wikisearch.wiki_search.entity.SearchHistory;
import wikisearch.wiki_search.entity.WikiArticle;
import wikisearch.wiki_search.repository.SearchHistoryRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class SearchHistoryService {
    private final SearchHistoryRepository historyRepo;
    private final SimpleCache cache;

    public SearchHistoryService(SearchHistoryRepository historyRepo, SimpleCache cache) {
        this.historyRepo = historyRepo;
        this.cache = cache;
    }

    public List<SearchHistory> getAllHistories() {
        @SuppressWarnings("unchecked")
        List<SearchHistory> cached = (List<SearchHistory>) cache.get("all_histories");
        if (cached != null) {
            return cached;
        }
        List<SearchHistory> result = historyRepo.findAll();
        cache.put("all_histories", result);
        return result;
    }

    public SearchHistory getBySearchTerm(String term) {
        String key = "history:" + term;
        SearchHistory cached = (SearchHistory) cache.get(key);
        if (cached != null) {
            return cached;
        }
        SearchHistory result = historyRepo.findBySearchTerm(term);
        if (result != null) {
            cache.put(key, result);
        }
        return result;
    }

    @Transactional
    public void saveSearchHistoryWithArticles(String term, List<WikiArticle> articles) {
        cache.clear();
        SearchHistory history = historyRepo.findBySearchTerm(term);
        if (history == null) {
            history = new SearchHistory();
            history.setSearchTerm(term);
            history.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        }
        for (WikiArticle article : articles) {
            article.setHistory(history);
        }
        history.setArticles(articles);
        historyRepo.save(history);
    }
}
