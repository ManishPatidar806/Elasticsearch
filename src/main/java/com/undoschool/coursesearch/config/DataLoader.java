package com.undoschool.coursesearch.config;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.undoschool.coursesearch.document.CourseDocument;
import com.undoschool.coursesearch.repository.CourseRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

@Component
public class DataLoader {
    private final CourseRepository repo;
    private final ObjectMapper mapper;

    public DataLoader(CourseRepository repo, ObjectMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void loadData() throws Exception {
        if (repo.count() == 0) {
            InputStream is = new ClassPathResource("sample-courses.json").getInputStream();
            List<CourseDocument> courses = mapper.readValue(is, new TypeReference<>() {});
            courses.forEach(course -> course.setTitleSuggest(course.getTitle()));
            repo.saveAll(courses);
            System.out.println("Indexed " + courses.size() + " courses.");
        } else {
            System.out.println("Courses already indexed, skipping...");
        }
    }
}
