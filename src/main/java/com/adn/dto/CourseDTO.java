package com.adn.dto;

import com.adn.enums.Category;
import com.adn.enums.validation.ValueOfEnum;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

import java.util.List;

public record CourseDTO(
        @JsonProperty("_id") Long id,
        @NotNull @NotBlank @Length(min = 5, max = 100) String name,
        @NotNull @Length(max = 10) @ValueOfEnum(enumClass = Category.class) String category,
        // ignore status. Will not be shown to user
        @NotNull @NotEmpty @Valid List<LessonDTO> lessons
) {}
