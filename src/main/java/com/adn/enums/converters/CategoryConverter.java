package com.adn.enums.converters;

import com.adn.enums.Category;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.stream.Stream;

@Converter(autoApply = true) // jpa will always use the converter when necessary. Will call this auto
public class CategoryConverter implements AttributeConverter<Category, String> {
    // will save in the right way the category enum in database. Same way reading from database the string to enum
    @Override
    public String convertToDatabaseColumn(Category category) {
        if (category == null) { return null; }
        return category.getValue();
    }

    @Override
    public Category convertToEntityAttribute(String value) {
        if (value == null) { return null; }
        return Stream.of(Category.values())
                .filter(c -> c.getValue().equals(value))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
