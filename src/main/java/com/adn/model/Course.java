package com.adn.model;

import com.adn.enums.Category;
import com.adn.enums.Status;
import com.adn.enums.converters.CategoryConverter;
import com.adn.enums.converters.StatusConverter;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Data;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.hibernate.validator.constraints.Length;

import java.util.ArrayList;
import java.util.List;

@Data // generate set, get, toString, equals, hash etc
@Entity // jpa
//@Table(name = "Courses") in case of legacy code, where database was created before
// hibernate will execute this query every time delete is invoked, turning delete method in soft delete
@SQLDelete(sql = "UPDATE Course SET status = 'Inactive' WHERE id = ?")
// hibernate will execute in every select a where clause >>in the query (a filter)
@Where(clause = "status = 'Active'")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    // front-end uses _id instead of back-end id. when object-json conversion occur, json now has a _id att
    @JsonProperty("_id")
    private Long id;

    @NotBlank  // java bean validation
    @NotNull // java bean validation
    @Length(min = 5, max = 100) // hibernate validation
    //@Column(name = "nome") in case where legacy database, you can make the association.
    @Column(length = 100, nullable = false)
    private  String name;

    @NotNull
    @Column(length = 10, nullable = false)
    @Convert(converter = CategoryConverter.class) // correct way to save in database
    private Category category;

    @NotNull
    @Column(length = 10, nullable = false)
    @Convert(converter = StatusConverter.class) // correct way to save in database
    private Status status = Status.ACTIVE;

    @OneToMany(
            cascade = CascadeType.ALL, // when parent entity is modified, verifies if changes a needed in child entity
            orphanRemoval = true
    )
    @JoinColumn(name = "course_id")
    private List<Lesson> lessons = new ArrayList<>();
}
