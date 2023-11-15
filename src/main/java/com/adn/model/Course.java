package com.adn.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import lombok.Data;

@Data // generate set, get, toString, equals, hash etc
@Entity // jpa
//@Table(name = "Courses") in case of legacy code, where database was created before
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    // front-end uses _id instead of back-end id. when object-json conversion occur, json now has a _id att
    @JsonProperty("_id")
    private Long id;

    //@Column(name = "nome") in case where legacy database, you can make the association.
    @Column(length = 200, nullable = false)
    private  String name;

    @Column(length = 20, nullable = false)
    private  String category;
}
