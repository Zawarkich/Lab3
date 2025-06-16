package wikisearch.wiki_search.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wikisearch.wiki_search.entity.SearchHistory;
import wikisearch.wiki_search.entity.WikiArticle;
import wikisearch.wiki_search.repository.SearchHistoryRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class SearchHistoryService {
    private final SearchHistoryRepository historyRepo;

    public SearchHistoryService(SearchHistoryRepository historyRepo) {
        this.historyRepo = historyRepo;
    }

    public List<SearchHistory> getAllHistories() {
        return historyRepo.findAll();
    }

    public SearchHistory getBySearchTerm(String term) {
        return historyRepo.findBySearchTerm(term);
    }

    @Transactional
    public void saveSearchHistoryWithArticles(String term, List<WikiArticle> articles) {
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
