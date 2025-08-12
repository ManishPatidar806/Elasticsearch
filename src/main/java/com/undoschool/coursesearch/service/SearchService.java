package com.undoschool.coursesearch.service;

import com.undoschool.coursesearch.document.CourseDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MultiMatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.WildcardQuery;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchService {
    private final ElasticsearchOperations operations;

    public SearchService(ElasticsearchOperations operations) {
        this.operations = operations;
    }

    public Page<CourseDocument> search(
            String q, Integer minAge, Integer maxAge, String category, String type,
            Double minPrice, Double maxPrice, Instant startDate,
            String sort, int page, int size, boolean fuzzy
    ) {
        List<Query> mustQueries = new ArrayList<>();
        List<Query> filterQueries = new ArrayList<>();

        // Text search query
        if (q != null && !q.isBlank()) {
            if (fuzzy) {
                mustQueries.add(MultiMatchQuery.of(m -> m
                        .fields("title", "description")
                        .query(q)
                        .fuzziness("AUTO")
                )._toQuery());
            } else {
                mustQueries.add(MultiMatchQuery.of(m -> m
                        .fields("title", "description")
                        .query(q)
                )._toQuery());
            }
        }

        // Filter queries
        if (minAge != null) {
            filterQueries.add(RangeQuery.of(r -> r
                    .number(n -> n.field("minAge").gte(minAge.doubleValue()))
            )._toQuery());
        }
        if (maxAge != null) {
            filterQueries.add(RangeQuery.of(r -> r
                    .number(n -> n.field("maxAge").lte(maxAge.doubleValue()))
            )._toQuery());
        }
        if (minPrice != null) {
            filterQueries.add(RangeQuery.of(r -> r
                    .number(n -> n.field("price").gte(minPrice))
            )._toQuery());
        }
        if (maxPrice != null) {
            filterQueries.add(RangeQuery.of(r -> r
                    .number(n -> n.field("price").lte(maxPrice))
            )._toQuery());
        }
        if (category != null) {
            filterQueries.add(TermQuery.of(t -> t
                    .field("category")
                    .value(category)
            )._toQuery());
        }
        if (type != null) {
            filterQueries.add(TermQuery.of(t -> t
                    .field("type")
                    .value(type)
            )._toQuery());
        }
        if (startDate != null) {
            filterQueries.add(RangeQuery.of(r -> r
                    .date(d -> d.field("nextSessionDate").gte(startDate.toString()))
            )._toQuery());
        }

        // Build bool query
        BoolQuery.Builder boolBuilder = new BoolQuery.Builder();
        if (!mustQueries.isEmpty()) {
            boolBuilder.must(mustQueries);
        }
        if (!filterQueries.isEmpty()) {
            boolBuilder.filter(filterQueries);
        }

        // Create sort
        Sort sortOrder;
        switch (sort) {
            case "priceAsc":
                sortOrder = Sort.by("price").ascending();
                break;
            case "priceDesc":
                sortOrder = Sort.by("price").descending();
                break;
            default:
                sortOrder = Sort.by("nextSessionDate").ascending();
        }

        // Create pageable
        Pageable pageable = PageRequest.of(page, size, sortOrder);

        // Build native query
        NativeQuery searchQuery = NativeQuery.builder()
                .withQuery(boolBuilder.build()._toQuery())
                .withPageable(pageable)
                .build();

        SearchHits<CourseDocument> hits = operations.search(searchQuery, CourseDocument.class);
        
        return new PageImpl<>(
                hits.stream().map(h -> h.getContent()).collect(Collectors.toList()),
                pageable,
                hits.getTotalHits()
        );
    }

    public List<String> getSuggestions(String query, int limit) {

        Query matchPhraseQuery = Query.of(q -> q
                .matchPhrasePrefix(m -> m
                        .field("title")
                        .query(query)
                )
        );

        NativeQuery searchQuery = NativeQuery.builder()
                .withQuery(matchPhraseQuery)
                .withMaxResults(limit)
                .build();

        SearchHits<CourseDocument> hits = operations.search(searchQuery, CourseDocument.class);
        
        return hits.stream()
                .map(hit -> hit.getContent().getTitle())
                .distinct()
                .collect(Collectors.toList());
    }
}
