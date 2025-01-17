package com.example.apache_lucene.controller;

import com.example.apache_lucene.services.LuceneService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SearchController {

    private final LuceneService luceneService;

    public SearchController(LuceneService luceneService) {
        this.luceneService = luceneService;
    }

    @GetMapping("/search")
    public List<String> search(@RequestParam String query) {
        return luceneService.search(query);
    }

    @GetMapping("/search/content")
    public List<String> searchContent(@RequestParam String query) {
        return luceneService.searchContent(query);
    }
}
