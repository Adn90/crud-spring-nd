package com.adn.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(length = 10, nullable = false)
    private String name;

    @Column(length = 11, nullable = false)
    private String youtubeUrl;

    // not needed, Course already has a @JoinColumn(name = "course_id").
    // the effect would be the same
    // private String course_id;

}
