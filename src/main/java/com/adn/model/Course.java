package com.adn.model;

import jakarta.persistence.*;

import lombok.Data;

@Data // generate set, get, toString, equals, hash etc
@Entity // jpa
//@Table(name = "Courses") in case of legacy code, where database was created before
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    //@Column(name = "nome") in case where legacy database, you can make the association.
    @Column(length = 200, nullable = false)
    private  String name;

    @Column(length = 20, nullable = false)
    private  String category;
}
