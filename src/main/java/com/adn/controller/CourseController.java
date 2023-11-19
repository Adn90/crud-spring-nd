package com.adn.controller;

import com.adn.model.Course;
import com.adn.repository.CourseRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping // @RequestMapping(method = RequestMethod.POST) same as @PostMapping()
    // payload coming in the body of the request - ng service
    // @RequestBody will look for the att of the RequestBody, and try to map with course
    public ResponseEntity<Course> create(@RequestBody Course course) {
        return ResponseEntity.status(HttpStatus.CREATED) // return correct http code
                .body(courseRepository.save(course));
    }

    // no mapping. Should be @PostMapping, but would crash app (2 postmaps, same url)
    @ResponseStatus(code = HttpStatus.CREATED)
    public Course create2(@RequestBody Course course) {
        // there is an annotation for ResponseEntity, but this way, you can't manipulate the header and response
        // there is no need to manipulate response and header in this case, so the code will be more clean in this case
        return courseRepository.save(course);
    }
}
