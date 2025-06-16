package wikisearch.wiki_search.controller;

import org.springframework.web.bind.annotation.*;

import wikisearch.wiki_search.dto.WikiArticleDto;
import wikisearch.wiki_search.service.WikiArticleService;
import wikisearch.wiki_search.service.SearchHistoryService;
import wikisearch.wiki_search.entity.WikiArticle;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@RestController
@RequestMapping("/api/articles")
public class WikiArticleCrudController {
    private final WikiArticleService articleService;
    private final SearchHistoryService historyService;

    @Autowired
    public WikiArticleCrudController(WikiArticleService articleService, SearchHistoryService historyService) {
        this.articleService = articleService;
        this.historyService = historyService;
    }

    @GetMapping
    public List<WikiArticleDto> getAll() {
        return articleService.getAllArticles();
    }

    @GetMapping("/{id}")
    public WikiArticleDto getById(@PathVariable Long id) {
        return articleService.getArticleById(id);
    }

    @PostMapping
    public WikiArticleDto create(@RequestBody WikiArticle article) {
        return articleService.createArticle(article);
    }

    @PutMapping("/{id}")
    public WikiArticleDto update(@PathVariable Long id, @RequestBody WikiArticle article) {
        return articleService.updateArticle(id, article);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        articleService.deleteArticle(id);
    }

    @GetMapping("/by-term")
    public List<WikiArticleDto> getByTerm(@RequestParam String term) {
        List<WikiArticleDto> result = articleService.findByTerm(term);
        List<WikiArticle> articles = result.stream()
            .map(dto -> new WikiArticle(dto.getTitle(), dto.getContent()))
            .collect(java.util.stream.Collectors.toList());
        historyService.saveSearchHistoryWithArticles(term, articles);
        return result;
    }
}
