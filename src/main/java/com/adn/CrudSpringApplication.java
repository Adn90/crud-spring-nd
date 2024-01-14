package com.adn;

import com.adn.enums.Category;
import com.adn.model.Course;
import com.adn.repository.CourseRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
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

			courseRepository.save(c);
		};
	}
}
