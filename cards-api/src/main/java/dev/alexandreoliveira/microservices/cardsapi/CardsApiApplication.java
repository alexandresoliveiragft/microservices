package dev.alexandreoliveira.microservices.cardsapi;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Predicate;
import dev.alexandreoliveira.microservices.cardsapi.database.entities.CardEntity;
import dev.alexandreoliveira.microservices.cardsapi.database.entities.enums.CardTypeEnum;
import dev.alexandreoliveira.microservices.cardsapi.database.repositories.CardsRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SpringBootApplication
public class CardsApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(CardsApiApplication.class, args);
	}

	@Bean
	@Profile("dev")
	CommandLineRunner commandLineRunner(ApplicationContext applicationContext) {
		return args -> {
			CardsRepository cardsRepository = applicationContext.getBean(CardsRepository.class);

			RestClient restClient = RestClient.builder()
					.baseUrl("http://localhost:8080")
					.build();

			MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
			queryParams.add("email", "alexandre@email.com");

			var body = restClient
					.get()
					.uri(uriBuilder ->
						uriBuilder.path("/users")
								.queryParams(queryParams)
								.build()
					)
					.accept(MediaType.APPLICATION_JSON)
					.retrieve()
					.toEntity(String.class);

			Predicate expensivePredicate = context -> {
                String value = context.item(Map.class).get("email").toString();
                return value.equals("alexandre@email.com");
            };

			DocumentContext documentContext = JsonPath.parse(body.getBody());
			Map<String, Object> data = documentContext.read("$['content'][0]", expensivePredicate);

			if (!data.containsKey("id")) {
				return;
			}

			UUID externalUserId = UUID.fromString(data.get("id").toString());

			ExampleMatcher exampleMatcher = ExampleMatcher
					.matchingAll()
					.withStringMatcher(ExampleMatcher.StringMatcher.EXACT)
					.withIgnoreNullValues();

			var cardEntityExample = new CardEntity();
			cardEntityExample.setExternalId(externalUserId);

			List<CardEntity> cards = cardsRepository.findAll(Example.of(cardEntityExample, exampleMatcher));

			if (CollectionUtils.isEmpty(cards)) {
				var card = new CardEntity();
				card.setExternalId(externalUserId);
				card.setCardNumber("0000111122223333");
				card.setCardType(CardTypeEnum.CREDT);
				card.setSecureCode("000");
				card.setLimitValue(BigDecimal.valueOf(999_999_999_999.99));
				card.setValidDate(LocalDateTime.of(3000, 12, 31, 23, 59, 59));

				cardsRepository.save(card);
			}
		};
	}
}
