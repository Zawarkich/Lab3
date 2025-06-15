package wikisearch.wiki_search.controller;

import org.springframework.web.bind.annotation.*;

import jakarta.transaction.Transactional;
import wikisearch.wiki_search.entity.WikiArticle;
import wikisearch.wiki_search.repository.WikiArticleRepository;
import wikisearch.wiki_search.cache.SimpleCache;
import org.springframework.beans.factory.annotation.Autowired;
import wikisearch.wiki_search.dto.WikiArticleDto;

import java.util.List;
import java.util.stream.Collectors;

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
    public List<WikiArticleDto> getAll() {
        return articleRepo.findAll().stream()
            .map(a -> new WikiArticleDto(a.getId(), a.getTitle(), a.getContent()))
            .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public WikiArticleDto getById(@PathVariable Long id) {
        return articleRepo.findById(id)
            .map(a -> new WikiArticleDto(a.getId(), a.getTitle(), a.getContent()))
            .orElse(null);
    }

    @PostMapping
    public WikiArticleDto create(@RequestBody WikiArticle article) {
        WikiArticle saved = articleRepo.save(article);
        return new WikiArticleDto(saved.getId(), saved.getTitle(), saved.getContent());
    }

    @PutMapping("/{id}")
    public WikiArticleDto update(@PathVariable Long id, @RequestBody WikiArticle article) {
        article.setId(id);
        WikiArticle saved = articleRepo.save(article);
        return new WikiArticleDto(saved.getId(), saved.getTitle(), saved.getContent());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        articleRepo.deleteById(id);
    }

    @Transactional
    @GetMapping("/by-term")
    public List<WikiArticleDto> getByTerm(@RequestParam String term) {
        @SuppressWarnings("unchecked")
        List<WikiArticleDto> cached = (List<WikiArticleDto>) cache.get("term:" + term);
        if (cached != null) {
            return cached;
        }
        List<WikiArticleDto> result = articleRepo.findByTerm(term)
            .stream()
            .map(a -> new WikiArticleDto(a.getId(), a.getTitle(), a.getContent()))
            .collect(Collectors.toList());
        cache.put("term:" + term, result);
        return result;
    }
}
