package com.adn.repository;

import com.adn.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository // special type of @component - spring identify component, as one tha manages database
public interface CourseRepository extends JpaRepository<Course, Long> { // entity, id type
}
