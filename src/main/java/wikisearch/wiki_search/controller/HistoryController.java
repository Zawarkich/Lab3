package wikisearch.wiki_search.controller;

import org.springframework.web.bind.annotation.*;
import wikisearch.wiki_search.entity.SearchHistory;
import wikisearch.wiki_search.repository.SearchHistoryRepository;

import java.util.List;

@RestController
@RequestMapping("/api/history")
public class HistoryController {
    private final SearchHistoryRepository historyRepo;

    public HistoryController(SearchHistoryRepository historyRepo) {
        this.historyRepo = historyRepo;
    }

    @PostMapping
    public SearchHistory create(@RequestBody SearchHistory history) {
        return historyRepo.save(history);
    }

    @GetMapping
    public List<SearchHistory> getAll() {
        return historyRepo.findAll();
    }
}