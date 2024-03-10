package com.adn;

import com.adn.enums.Category;
import com.adn.model.Course;
import com.adn.model.Lesson;
import com.adn.repository.CourseRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
public class CrudSpringApplication {

	public static void main(String[] args) {
		SpringApplication.run(CrudSpringApplication.class, args);
	}

	@Bean // spring handle life cycle
		// executed after app run
	@Profile("dev") // application-dev.properties
	CommandLineRunner initLocalDatabase(CourseRepository courseRepository) {
		
		return args -> {
			courseRepository.deleteAll();
			for (int i = 0; i < 20; i++) {				
				Course c = new Course();
				c.setName(String.format("Curso: %s", i + 1));
				c.setCategory(Category.BACK_END);

				Lesson l = new Lesson();
				l.setName("Intro");
				l.setYoutubeUrl("Nb4uxLxdvxo");
				l.setCourse(c);
				c.getLessons().add(l);

				Lesson l2 = new Lesson();
				l2.setName("Aula 2");
				l2.setYoutubeUrl("Nb4uxLxdvx1");
				l2.setCourse(c);
				c.getLessons().add(l2);

				courseRepository.save(c);
			}
		};
	}
}
