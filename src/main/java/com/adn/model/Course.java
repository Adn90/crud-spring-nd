package com.adn.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data // generate set, get, toString, equals, hash etc
@Entity // jpa
//@Table(name = "Courses") in case of legacy code, where database was created before
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
    @Pattern(regexp = "Back-end|Front-end|Games")
    @Length(min = 1)
    @Column(length = 10, nullable = false)
    private  String category;
}
