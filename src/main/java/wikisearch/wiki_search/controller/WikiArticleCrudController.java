package wikisearch.wiki_search.controller;

import org.springframework.web.bind.annotation.*;
import wikisearch.wiki_search.entity.WikiArticle;
import wikisearch.wiki_search.repository.WikiArticleRepository;
import wikisearch.wiki_search.cache.SimpleCache;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@RestController
@RequestMapping("/api/articles")
public class WikiArticleCrudController {
    private final WikiArticleRepository articleRepo;
    private final SimpleCache cache;

    @Autowired
    public WikiArticleCrudController(WikiArticleRepository articleRepo, SimpleCache cache) {
        this.articleRepo = articleRepo;
        this.cache = cache;
    }

    @GetMapping
    public List<WikiArticle> getAll() {
        return articleRepo.findAll();
    }

    @GetMapping("/{id}")
    public WikiArticle getById(@PathVariable Long id) {
        return articleRepo.findById(id).orElse(null);
    }

    @PostMapping
    public WikiArticle create(@RequestBody WikiArticle article) {
        return articleRepo.save(article);
    }

    @PutMapping("/{id}")
    public WikiArticle update(@PathVariable Long id, @RequestBody WikiArticle article) {
        article.setId(id);
        return articleRepo.save(article);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        articleRepo.deleteById(id);
    }

    @GetMapping("/by-history-term")
    public List<WikiArticle> getByHistoryTerm(@RequestParam String term) {
        @SuppressWarnings("unchecked")
        List<WikiArticle> cached = (List<WikiArticle>) cache.get(term);
        if (cached != null) {
            return cached;
        }
        List<WikiArticle> result = articleRepo.findBySearchHistoryTerm(term);
        cache.put(term, result);
        return result;
    }
}
