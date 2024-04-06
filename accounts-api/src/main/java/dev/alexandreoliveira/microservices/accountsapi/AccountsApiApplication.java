package dev.alexandreoliveira.microservices.accountsapi;

import dev.alexandreoliveira.microservices.accountsapi.database.entities.AccountEntity;
import dev.alexandreoliveira.microservices.accountsapi.database.entities.UserEntity;
import dev.alexandreoliveira.microservices.accountsapi.database.entities.enums.AccountTypeEnum;
import dev.alexandreoliveira.microservices.accountsapi.database.repositories.AccountRepository;
import dev.alexandreoliveira.microservices.accountsapi.database.repositories.UserRepository;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
@OpenAPIDefinition(
		info = @Info(
				title = "Accounts API microservice - Documentation",
				description = "AOsoluti API's",
				version = "v1 (0.0.1)",
				contact = @Contact(
						name = "Alexandre Salvador de Oliveira",
						email = "contact@alexandreoliveira.dev",
						url = "http://alexandreoliveira.dev"
				),
				license = @License(
						name = "Apache 2.0",
						url = "http://alexandreoliveira.dev"
				)
		),
		externalDocs = @ExternalDocumentation(
				description = "AOsoluti API's",
				url = "https://microservices-bank-accounts-api.alexandreoliveira.dev/swagger-ui/index.html"
		)
)
public class AccountsApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(AccountsApiApplication.class, args);
	}

	@Bean
	@Profile("dev")
	CommandLineRunner commandLineRunner(ApplicationContext applicationContext) {
		return args -> {
			UserRepository userRepository = applicationContext.getBean(UserRepository.class);
			AccountRepository accountRepository = applicationContext.getBean(AccountRepository.class);

			var user = new UserEntity();
			user.setName("Alexandre Salvador de Oliveira");
			user.setEmail("alexandre@email.com");
			user.setMobileNumber("31911112222");

			UserEntity savedUserEntity = userRepository.save(user);

			var account = new AccountEntity();
			account.setAccountType(AccountTypeEnum.PF);
			account.setAccountNumber("0001110001");
			account.setUser(savedUserEntity);

			accountRepository.save(account);
		};
	}
}
