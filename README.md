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


### N+1 query problem with JPA and Hibernate
https://vladmihalcea.com/n-plus-1-query-problem/
https://stackoverflow.com/questions/97197/what-is-the-n1-selects-problem-in-orm-object-relational-mapping
https://pt.stackoverflow.com/questions/307264/o-que-%C3%A9-o-problema-das-queries-n1

````java
public class Course {
    @OneToMany(
            cascade = CascadeType.ALL, // when parent entity is modified, verifies if changes a needed in child entity
            orphanRemoval = true,
            mappedBy = "course" // the owner of this relation is course. A way to make bidirectional relation to improve performance
    )
    // @JoinColumn(name = "course_id")

    private List<Lesson> lessons = new ArrayList<>();
}

public class Lesson {
    @ManyToOne(
            fetch = FetchType.LAZY, // this mapping is loading only when .getCourse is called
            optional = false // required field
    )
    private Course course;

}


````

````
LOG in CONSOLE when app is compiled

Hibernate: select c1_0.id,c1_0.category,c1_0.name,c1_0.status from course c1_0 where (c1_0.status = 'Active')
Hibernate: select next value for course_seq
Hibernate: select next value for lesson_seq
Hibernate: insert into course (category,name,status,id) values (?,?,?,?) *
Hibernate: insert into lesson (course_id,name,youtube_url,id) values (?,?,?,?) **
````

````java
public class CrudSpringApplication {

	public static void main(String[] args) {
		SpringApplication.run(CrudSpringApplication.class, args);
	}

	@Bean // spring handle life cycle
		// executed after app run
	CommandLineRunner initLocalDatabase(CourseRepository courseRepository) {
		return args -> {
			courseRepository.deleteAll();
			Course c = new Course();
			c.setName("Angular");
			c.setCategory(Category.FRONT_END);
            
            // * course is inserted, and thus its id (l.setCourse(c); reference)

			Lesson l = new Lesson();
			l.setName("Intro");
			l.setYoutubeUrl("Nb4uxLxdvxo");
            
            // ** course already have its id when is set in lesson          
			l.setCourse(c);
			c.getLessons().add(l);

			courseRepository.save(c);
		};
	}
}
````

## Performance

````
In this format, less database requests are made. When @Join
@OneToMany(
        cascade = CascadeType.ALL, // when parent entity is modified, verifies if changes a needed in child entity
        orphanRemoval = true,
        mappedBy = "course" // more info in the README.md
)
@JoinColumn(name = "course_id")

in this course/lesson relation, the app would made 3 calls instead of two. 
calling 1 course with 2 lessons, console:

Hibernate: select c1_0.id,c1_0.category,c1_0.name,c1_0.status from course c1_0 where (c1_0.status = 'Active')
Hibernate: select next value for course_seq
Hibernate: select next value for lesson_seq
Hibernate: select next value for lesson_seq
Hibernate: insert into course (category,name,status,id) values (?,?,?,?)
Hibernate: insert into lesson (course_id,name,youtube_url,id) values (?,?,?,?)
Hibernate: insert into lesson (course_id,name,youtube_url,id) values (?,?,?,?)
````
# List classes

````java
public record CourseDTO(
        @JsonProperty("_id") Long id,
        @NotBlank @NotNull @Length(min = 5, max = 10) String name,
        @NotNull @Length(max = 10) @Pattern(regexp = "Back-end|Front-end|Games") String category,
        // ignore status. Will not be shown to user
        List<Lesson> lessons // easy way. Raw from database. working in database
) {}
````

### circular Dependency

- course will populate by first select, then lesson will be populated, via another select in database
- lesson has a @ManyToOne(fetch = FetchType.LAZY, optional = false) to Course
- and course has a private List<Lesson> lessons = new ArrayList<>();
- the process repeats its self

> the lesson @ManyToOne, solves the n + 1 problem.
> to avoid circular dependency, just make att to set, and not get
> @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) make the att to a set only mode, as name suggests

````java
public class Course {
   // ...
    @NotNull
    @Column(length = 10, nullable = false)
    @Convert(converter = StatusConverter.class) // correct way to save in database
    private Status status = Status.ACTIVE;

    @OneToMany(
            cascade = CascadeType.ALL, // when parent entity is modified, verifies if changes a needed in child entity
            orphanRemoval = true,
            mappedBy = "course" // more info in the README.md
    )
    private List<Lesson> lessons = new ArrayList<>();
}

public class Lesson {
    // ...

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) // avoid circular dependency
    private Course course;

}

````


# Lesson update

> error about the disregard about collection came from database, and thus, make hibernate lost about new list

```java
class Course_and_mapper {
    @OneToMany(
        cascade = CascadeType.ALL, // when parent entity is modified, verifies if changes a needed in child entity
        orphanRemoval = true,
        mappedBy = "course" // more info in the README.md
    )
    private List<Lesson> lessons = new ArrayList<>(); // *1

    // mapper class
    public Course toEntity(CourseDTO courseDTO) {
        //...
        // create and update courses with lessons
        List<Lesson> lessons = courseDTO.lessons().stream().map(lessonDTO -> {
            var lesson = new Lesson();
            lesson.setId(lessonDTO.id());
            lesson.setName(lessonDTO.name());
            lesson.setYoutubeUrl(lessonDTO.youtubeUrl());
            lesson.setCourse(course);
            return lesson; // *2
        }).collect(Collectors.toList());
        course.setLessons(lessons);

        return course;
    }
}

class updateLesson {
    @SuppressWarnings("null")
    public CourseDTO update(@NotNull @Positive Long id, @Valid @NotNull CourseDTO courseDTO) {
        return courseRepository.findById(id)
            .map(dataFound -> {
                Course course = courseMapper.toEntity(courseDTO); // *3
                dataFound.setName(courseDTO.name());
                dataFound.setCategory(courseMapper.convertCategoryValue(courseDTO.category()));
                dataFound.setLessons(course.getLessons()); // *3
                // dataFound has id, because of that, hibernate JPA will execute an update instead of create
                return courseMapper.toDTO(courseRepository.save(dataFound));
            }).orElseThrow(() -> new RecordNotFoundException(id));
    }
}

```

> org.hibernate.HibernateException: A collection with cascade="all-delete-orphan" was no longer referenced by the owning entity instance

- JPA 'n hibernate will get the lesson list in the mapper class (dataFound), declared in course class
- then in the update service, the user one more time set a new lesson list
- in this case, hibernate will be lost it about the new lesson list, and thus throw the exception


```java

class updateLesson {
    @SuppressWarnings("null")
    public CourseDTO update(@NotNull @Positive Long id, @Valid @NotNull CourseDTO courseDTO) {
        return courseRepository.findById(id)
            .map(dataFound -> {
                Course course = courseMapper.toEntity(courseDTO);
                dataFound.setName(courseDTO.name());
                dataFound.setCategory(courseMapper.convertCategoryValue(courseDTO.category()));
                dataFound.getLessons().clear();
                course.getLessons().forEach(dataFound.getLessons()::add);
                return courseMapper.toDTO(courseRepository.save(dataFound));
            }).orElseThrow(() -> new RecordNotFoundException(id));
    }
}

```

-  dataFound.getLessons().clear(); will delete all lesson data from database
-   and will just add the updated versions of lesson
- this way the hibenate reference will be the same



# Validadte Enuns

- https://www.baeldung.com/javax-validations-enums
- Validating That a String Matches a Value of an Enum
- Generic for for enum validation

```java
class EnumValidationsAsString {
    @Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
    @Retention(RUNTIME)
    @Documented
    @Constraint(validatedBy = ValueOfEnumValidator.class)
    public @interface ValueOfEnum {
        Class<? extends Enum<?>> enumClass();
        String message() default "must be any of enum {enumClass}";
        Class<?>[] groups() default {};
        Class<? extends Payload>[] payload() default {};
    }
}

public class ValueOfEnumValidator implements ConstraintValidator<ValueOfEnum, CharSequence> {
    private List<String> acceptedValues;

    @Override
    public void initialize(ValueOfEnum annotation) {
        acceptedValues = Stream.of(annotation.enumClass().getEnumConstants())
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        return acceptedValues.contains(value.toString());
    }
}

@ValueOfEnum(enumClass = CustomerType.class)
private String customerTypeString;
```

### Subset of enums exemple

```java
class subSetEnum() {
    @Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
    @Retention(RUNTIME)
    @Documented
    @Constraint(validatedBy = CustomerTypeSubSetValidator.class)
    public @interface CustomerTypeSubset {
        CustomerType[] anyOf();
        String message() default "must be any of {anyOf}";
        Class<?>[] groups() default {};
        Class<? extends Payload>[] payload() default {};
    }
}

public class CustomerTypeSubSetValidator implements ConstraintValidator<CustomerTypeSubset, CustomerType> {
    private CustomerType[] subset;

    @Override
    public void initialize(CustomerTypeSubset constraint) {
        this.subset = constraint.anyOf();
    }

    @Override
    public boolean isValid(CustomerType value, ConstraintValidatorContext context) {
        return value == null || Arrays.asList(subset).contains(value);
    }
}


@CustomerTypeSubset(anyOf = {CustomerType.NEW, CustomerType.OLD})
private CustomerType customerType;
```


# Pagination

- JPA and Hibernate will abstract the pagination querys.