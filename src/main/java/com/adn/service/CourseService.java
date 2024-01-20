package com.adn.service;

import com.adn.dto.CourseDTO;
import com.adn.dto.mapper.CourseMapper;
import com.adn.exception.RecordNotFoundException;
import com.adn.repository.CourseRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Validated
public class CourseService {
    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;

    public CourseService(CourseRepository courseRepository, CourseMapper courseMapper) {
        this.courseRepository = courseRepository;
        this.courseMapper = courseMapper;
    }

    public List<CourseDTO> list() {
        return courseRepository.findAll()
                .stream()
                .map(courseMapper::toDTO) //.map(course -> courseMapper.toDTO(course))
                .collect(Collectors.toList()); // .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    // @PathVariable removed; should be in controller
    public CourseDTO findById(@NotNull @Positive Long id) {
        return courseRepository.findById(id).map(courseMapper::toDTO)
                .orElseThrow(() -> new RecordNotFoundException(id));
    }

    public CourseDTO create(@Valid @NotNull CourseDTO course) {
        return courseMapper.toDTO(courseRepository.save(courseMapper.toEntity(course)));
    }

    public CourseDTO update(@NotNull @Positive Long id, @Valid @NotNull CourseDTO course) {
        return courseRepository.findById(id)
                .map(dataFound -> {
                    dataFound.setName(course.name());
                    dataFound.setCategory(courseMapper.convertCategoryValue(course.category()));
                    // dataFound has id, because of that, hibernate JPA will execute an update instead of create
                    return courseMapper.toDTO(courseRepository.save(dataFound));
                }).orElseThrow(() -> new RecordNotFoundException(id));
    }

    public void delete(@NotNull @Positive Long id) {
        courseRepository.delete(courseRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException(id)));
    }
}
