package dev.alexandreoliveira.microservices.cardsapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication(
		exclude = {RedisRepositoriesAutoConfiguration.class}
)
@ConfigurationPropertiesScan("dev.alexandreoliveira.microservices.cardsapi.properties")
public class CardsApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(CardsApiApplication.class, args);
	}
}
