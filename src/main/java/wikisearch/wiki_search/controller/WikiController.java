package wikisearch.wiki_search.controller;

import wikisearch.wiki_search.dto.WikiArticleDto;
import wikisearch.wiki_search.service.WikiService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class WikiController {
    private final WikiService wikiService;

    public WikiController(WikiService wikiService) {
        this.wikiService = wikiService;
    }

    @GetMapping("/search")
    public WikiArticleDto search(@RequestParam String term) {
        return wikiService.searchAndSaveFromWiki(term);
    }
}