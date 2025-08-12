package com.undoschool.coursesearch;

import com.undoschool.coursesearch.document.CourseDocument;
import com.undoschool.coursesearch.repository.CourseRepository;
import com.undoschool.coursesearch.service.SearchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CourseSearchApplicationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private SearchService searchService;

    @Test
    void contextLoads() {

        assertThat(courseRepository).isNotNull();
        assertThat(searchService).isNotNull();
    }

    @Test
    void testDataIsLoaded() {

        long count = courseRepository.count();
        assertThat(count).isGreaterThan(0);
        System.out.println("Total courses loaded: " + count);
    }

    @Test
    void testSearchEndpoint() {

        String url = "http://localhost:" + port + "/api/search?q=music&size=5";
        @SuppressWarnings("rawtypes")
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.containsKey("total")).isTrue();
        assertThat(body.containsKey("courses")).isTrue();
        
        List<?> courses = (List<?>) body.get("courses");
        assertThat(courses.size()).isLessThanOrEqualTo(5);
        
        System.out.println("Search test passed. Found " + body.get("total") + " total matches");
    }

    @Test
    void testSearchWithFilters() {

        String url = "http://localhost:" + port + "/api/search?category=Math&sort=priceAsc&size=3";
        @SuppressWarnings("rawtypes")
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = response.getBody();
        assertThat(body).isNotNull();
        
        List<?> courses = (List<?>) body.get("courses");
        assertThat(courses.size()).isLessThanOrEqualTo(3);
        
        System.out.println("Filter test passed. Found " + body.get("total") + " Math courses");
    }

    @Test
    void testSuggestEndpoint() {

        String url = "http://localhost:" + port + "/api/search/suggest?q=Course&limit=5";
        @SuppressWarnings("rawtypes")
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.containsKey("suggestions")).isTrue();
        
        List<?> suggestions = (List<?>) body.get("suggestions");
        assertThat(suggestions.size()).isLessThanOrEqualTo(5);
        
        System.out.println("Suggestions test passed. Found " + suggestions.size() + " suggestions");
    }

    @Test
    void testFuzzySearch() {

        String url = "http://localhost:" + port + "/api/search?q=Musci&fuzzy=true&size=3";
        @SuppressWarnings("rawtypes")
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = response.getBody();
        assertThat(body).isNotNull();
        

        Integer total = (Integer) body.get("total");
        System.out.println("Fuzzy search test found " + total + " results for typo 'Musci'");
    }

    @Test
    void testSearchServiceDirectly() {

        Page<CourseDocument> results = searchService.search(
                "music", null, null, null, null,
                null, null, null, "upcoming", 0, 5, false
        );
        
        assertThat(results).isNotNull();
        assertThat(results.getTotalElements()).isGreaterThan(0);
        assertThat(results.getContent().size()).isLessThanOrEqualTo(5);
        
        System.out.println("Direct service test passed. Found " + results.getTotalElements() + " results");
    }

    @Test
    void testSuggestionServiceDirectly() {

        List<String> suggestions = searchService.getSuggestions("Course", 5);
        
        assertThat(suggestions).isNotNull();
        assertThat(suggestions.size()).isLessThanOrEqualTo(5);
        
        System.out.println("Direct suggestion test passed. Found " + suggestions.size() + " suggestions");
    }
}
