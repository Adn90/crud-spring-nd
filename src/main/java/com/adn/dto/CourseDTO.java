package com.adn.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

import java.util.List;

public record CourseDTO(
        @JsonProperty("_id") Long id,
        @NotBlank @NotNull @Length(min = 5, max = 10) String name,
        @NotNull @Length(max = 10) @Pattern(regexp = "Back-end|Front-end|Games") String category,
        // ignore status. Will not be shown to user
        List<LessonDTO> lessons
) {}
