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