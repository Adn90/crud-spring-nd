package com.adn.controller;

import com.adn.model.Course;
import com.adn.repository.CourseRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    /*
    * @Valid when receive request, @Valid will check if the json
    * cast to course, pass in the validations in the model class
    * */
    public ResponseEntity<Course> create(@RequestBody @Valid Course course) {
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

    @GetMapping("/{id}") // uri, param part of url
    // @PathVariable annotation to extract the templated part of the URI, represented by the variable {id}.
    // https://www.baeldung.com/spring-pathvariable
    // JPA demands an Optional return, for unregistered ids cases
    // instead of java Optional<Course>, we can use ResponseEntity<Course> of Spring
    // can use the jakarta validations in controllers as well. Id not null (Long is object) and only positive
    public ResponseEntity<Course> getCourseById(@PathVariable @NotNull @Positive Long id) {
        return courseRepository.findById(id)
                .map(data -> ResponseEntity.ok().body(data))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Course> update(@PathVariable @NotNull @Positive Long id, @RequestBody @Valid Course course) {
        return courseRepository.findById(id)
                .map(dataFound -> {
                    dataFound.setName(course.getName());
                    dataFound.setCategory(course.getCategory());
                    // dataFound has id, because of that, hibernate JPA will execute an update instead of create
                    Course updated = courseRepository.save(dataFound);
                    return  ResponseEntity.ok().body(updated);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    // A Hard delete or Physical delete - remove from database literally
    // most of the time, it's just a kind of disable, not delete data
    // just set an att like status: Active or inactive
    // instead of courseRepository.deleteById(id), would be dataFound.setStatus...

    // need to cast the build(); returns ResponseEntity<Object>
    // if instead of using Void, just use ResponseEntity<Object>
    // can use also wildCard <?>
    public ResponseEntity<Void> delete(@PathVariable @NotNull @Positive Long id) {
        return courseRepository.findById(id)
                .map(dataFound -> {
                    courseRepository.deleteById(id);
                    return  ResponseEntity.noContent().<Void>build(); // usually, delete operations returns nothing
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /*
    * soft delete
    * Does not show register based in an attribute
    * Two-ways implementation:
    * delete will be an update of status, and find will only return activate status
    * Other way is using hibernate
    * */

}
