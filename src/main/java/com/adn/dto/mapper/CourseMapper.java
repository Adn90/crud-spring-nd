package com.adn.dto.mapper;

import com.adn.dto.CourseDTO;
import com.adn.dto.LessonDTO;
import com.adn.enums.Category;
import com.adn.model.Course;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CourseMapper {
    public CourseDTO toDTO(Course course) {
        if (course == null) return null;
        List<LessonDTO> lessonDTOS =  course.getLessons()
                .stream()
                .map(lesson -> new LessonDTO(lesson.getId(), lesson.getName(), lesson.getYoutubeUrl()))
                .collect(Collectors.toList());

        return new CourseDTO(course.getId(),
                course.getName(),
                course.getCategory().getValue(),
                lessonDTOS
        );
    }

    public Course toEntity(CourseDTO courseDTO) {
        if (courseDTO == null) return null;
        Course course = new Course();
        if (courseDTO.id() != null) {
            course.setId(courseDTO.id());
        }
        course.setName(courseDTO.name());
        // TODO: use a Mapper for category
        course.setCategory(convertCategoryValue(courseDTO.category()));
        return course;
    }

    public Category convertCategoryValue(String value) {
        if (value == null) { return null; }
        return switch (value) {
            case "Front-end" -> Category.FRONT_END;
            case "Back-end" -> Category.BACK_END;
            case "Games" -> Category.GAMES;
            default -> throw new IllegalArgumentException("Categoria inválida: " + value);
        };

    }
}
