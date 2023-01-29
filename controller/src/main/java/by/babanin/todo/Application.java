package by.babanin.todo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        System.setProperty("spring.config.additional-location", "classpath:api.properties, classpath:api-${spring.profiles.active}.properties");
        SpringApplication.run(Application.class, args);
    }
}
