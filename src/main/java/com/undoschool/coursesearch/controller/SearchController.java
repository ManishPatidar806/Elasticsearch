package com.undoschool.coursesearch.controller;

import com.undoschool.coursesearch.document.CourseDocument;
import com.undoschool.coursesearch.service.SearchService;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/search")
    public Map<String, Object> search(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Integer minAge,
            @RequestParam(required = false) Integer maxAge,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDate,
            @RequestParam(defaultValue = "upcoming") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "false") boolean fuzzy
    ) {
        Page<CourseDocument> results = searchService.search(
                q, minAge, maxAge, category, type,
                minPrice, maxPrice, startDate, sort, page, size, fuzzy
        );

        return Map.of(
                "total", results.getTotalElements(),
                "courses", results.getContent()
        );
    }

    @GetMapping("/search/suggest")
    public Map<String, Object> suggest(
            @RequestParam String q,
            @RequestParam(defaultValue = "10") int limit
    ) {
        List<String> suggestions = searchService.getSuggestions(q, limit);

        return Map.of("suggestions", suggestions);
    }

    @GetMapping("/health")
    public Map<String, Object> health() {
        try {
            long totalCourses = searchService.search(
                    null, null, null, null, null,
                    null, null, null, "upcoming", 0, 1, false
            ).getTotalElements();

            return Map.of(
                    "status", "UP",
                    "elasticsearch", "connected",
                    "totalCourses", totalCourses,
                    "timestamp", java.time.Instant.now()
            );
        } catch (Exception e) {
            return Map.of(
                    "status", "DOWN",
                    "elasticsearch", "disconnected",
                    "error", e.getMessage(),
                    "timestamp", java.time.Instant.now()
            );
        }
    }
}