package com.adn.controller;

import com.adn.model.Course;
import com.adn.repository.CourseRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController // java servelt (endpoint; url -- rest - get, post, etc)
@RequestMapping("api/courses")
//@AllArgsConstructor // create a constructor with all atts
// @Component // spring controls the life cycle of the instance - @RestController is a special component
public class CourseController {

    private final CourseRepository courseRepository;

    // correct way to inject dependency, via controller instead of a set or attribute
    // app will understand that is required to the instance has the repository (in this case)
    // for a set method or via attribute, is necessary to use @autowired annotation
    public CourseController(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }



    // @RequestMapping(method = RequestMethod.GET) same as @GetMapping()
    @GetMapping()
    public List<Course> list() {
        return courseRepository.findAll();
    }
}
