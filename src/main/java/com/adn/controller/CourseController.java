package com.adn.controller;

import com.adn.dto.CourseDTO;
import com.adn.model.Course;

import com.adn.service.CourseService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Validated // validates all java bean (jakarta) and hibernate. Except the model @Validate in create. That comes from Model class
@RestController // java servelt (endpoint; url -- rest - get, post, etc)
@RequestMapping("api/courses")
//@AllArgsConstructor // create a constructor with all atts
// @Component // spring controls the life cycle of the instance - @RestController is a special component
public class CourseController {

    private final CourseService courseService;

    // correct way to inject dependency, via controller instead of a set or attribute
    // app will understand that is required to the instance has the repository (in this case)
    // for a set method or via attribute, is necessary to use @autowired annotation
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping() // @RequestMapping(method = RequestMethod.GET) same as @GetMapping()
    public @ResponseBody List<CourseDTO> list() {
        return courseService.list();
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public CourseDTO create(@RequestBody @Valid @NotNull CourseDTO course) {
        return courseService.create(course);
    }

    @GetMapping("/{id}") // uri, param part of url
    // @PathVariable annotation to extract the templated part of the URI, represented by the variable {id}.
    // https://www.baeldung.com/spring-pathvariable
    // JPA demands an Optional return, for unregistered ids cases
    // instead of java Optional<Course>, we can use ResponseEntity<Course> of Spring
    // can use the jakarta validations in controllers as well. Id not null (Long is object) and only positive
    public CourseDTO getCourseById(@PathVariable @NotNull @Positive Long id) {
        return courseService.findById(id);
    }

    @PutMapping("/{id}")
    public CourseDTO update(@PathVariable @NotNull @Positive Long id, @RequestBody @Valid @NotNull CourseDTO course) {
        return courseService.update(id, course);
    }

    @DeleteMapping("/{id}")
    // A Hard delete or Physical delete - remove from database literally
    // most of the time, it's just a kind of disable, not delete data
    // just set an att like status: Active or inactive
    // instead of courseRepository.deleteById(id), would be dataFound.setStatus...
    @ResponseStatus(code = HttpStatus.NO_CONTENT) // keeping return no content, after exception handling
    public void delete(@PathVariable @NotNull @Positive Long id) {
        courseService.delete(id);
    }

}
