package wikisearch.wiki_search.controller;

import org.springframework.web.bind.annotation.*;

import wikisearch.wiki_search.dto.WikiArticleDto;
import wikisearch.wiki_search.service.WikiService;
import wikisearch.wiki_search.cache.SimpleCache;
import wikisearch.wiki_search.entity.WikiArticle;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@RestController
@RequestMapping("/api/articles")
public class WikiArticleCrudController {
    private final WikiService wikiService;
    private final SimpleCache cache;

    @Autowired
    public WikiArticleCrudController(WikiService wikiService, SimpleCache cache) {
        this.wikiService = wikiService;
        this.cache = cache;
    }

    @GetMapping
    public List<WikiArticleDto> getAll() {
        return wikiService.getAllArticles();
    }

    @GetMapping("/{id}")
    public WikiArticleDto getById(@PathVariable Long id) {
        return wikiService.getArticleById(id);
    }

    @PostMapping
    public WikiArticleDto create(@RequestBody WikiArticle article) {
        return wikiService.createArticle(article);
    }

    @PutMapping("/{id}")
    public WikiArticleDto update(@PathVariable Long id, @RequestBody WikiArticle article) {
        return wikiService.updateArticle(id, article);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        wikiService.deleteArticle(id);
    }

    @GetMapping("/by-term")
    public List<WikiArticleDto> getByTerm(@RequestParam String term) {
        @SuppressWarnings("unchecked")
        List<WikiArticleDto> cached = (List<WikiArticleDto>) cache.get("term:" + term);
        if (cached != null) {
            return cached;
        }
        List<WikiArticleDto> result = wikiService.findByTermAndSaveHistory(term);
        cache.put("term:" + term, result);
        return result;
    }
}
