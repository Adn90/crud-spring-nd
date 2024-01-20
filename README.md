# DTO (Data Transfer Object)

> An object that carries data between processes in order to reduce the number of method


https://martinfowler.com/eaaCatalog/dataTransferObject.html

- avoid show the actual columns of database
- security risk
- less server requests
- two ways to create a DTO:

> and DTO object class

```java

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class CourseDTO {

    private Long id;

    @NotBlank  // java bean validation
    @NotNull // java bean validation
    @Length(min = 5, max = 100) // hibernate validation
    private String name;

    @NotNull
    @Pattern(regexp = "Back-end|Front-end|Games")
    @Length(min = 1)
    private String category;

    @NotNull
    @Pattern(regexp = "Active|Inactive")
    @Length(min = 1)
    private String status = "Active";
}
```

Working this way, you would need t refactor the code, and change the Course returns do CourseDTO, like in controller:

```java
public class CourseController {
    @PutMapping("/{id}")
    public CourseDTO update(@PathVariable @NotNull @Positive Long id, @RequestBody @Valid Course course) {
        return courseService.update(id, course);
    }
}
```

with java 17 and spring 6, with spring boot 3, use Record instead

https://www.guiadojava.com.br/2021/04/java-records.html
https://www.baeldung.com/java-record-keyword
https://www.baeldung.com/java-record-vs-final-class



```agsl
basically, constructor with att, and not have set; 
immutable classes; 
does not have get, instead use the att name

cannot use JPA/hibernate (be an Entity):
    JPA entity must have empty constructor
    have setter methods
    If you want to be an Entity, must use jdbc and write querys
```

Must consider a Request DTO(without id) and Response DTO(with id)


# Mapper

map entity -> dto, dto -> entity

https://www.baeldung.com/java-performance-mapping-frameworks


# Persisting ENUMS JPA

````java
public class ENUMJAP {
    @NotNull
    // borefe make converter
    @Enumerated(EnumType.STRING) // save the enum in string format in the database 
    @Convert(converter = CategoryConverter.class) // correct way to save in database
    private Category category; 
}

````

https://www.baeldung.com/jpa-persisting-enums-in-jpa


# ONE-to-Many

https://vladmihalcea.com/the-best-way-to-map-a-onetomany-association-with-jpa-and-hibernate/