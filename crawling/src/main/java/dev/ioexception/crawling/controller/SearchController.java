package dev.ioexception.crawling.controller;

import dev.ioexception.crawling.dto.SearchedLectureResponse;
import dev.ioexception.crawling.service.search.ElasticSearchServiceImpl;
import dev.ioexception.crawling.service.search.MariaDBSearchServiceImpl;
import dev.ioexception.crawling.service.search.SearchService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SearchController {
    private final SearchService searchService;

    public SearchController(ElasticSearchServiceImpl service) {
        this.searchService = service;
    }

    @GetMapping("/search")
    public ResponseEntity<List<SearchedLectureResponse>> search(@RequestParam String q) {
        List<SearchedLectureResponse> result = searchService.search(q);

        return ResponseEntity.ok(result);
    }
}
