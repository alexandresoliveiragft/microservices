package dev.alexandreoliveira.microservices.accountsapi;

import dev.alexandreoliveira.microservices.accountsapi.database.entities.AccountEntity;
import dev.alexandreoliveira.microservices.accountsapi.database.entities.UserEntity;
import dev.alexandreoliveira.microservices.accountsapi.database.entities.enums.AccountTypeEnum;
import dev.alexandreoliveira.microservices.accountsapi.database.repositories.AccountsRepository;
import dev.alexandreoliveira.microservices.accountsapi.database.repositories.UsersRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.util.CollectionUtils;

import java.util.List;

@SpringBootApplication
public class AccountsApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(AccountsApiApplication.class, args);
	}

	@Bean
	@Profile("dev")
	CommandLineRunner commandLineRunner(ApplicationContext applicationContext) {
		return args -> {
			UsersRepository usersRepository = applicationContext.getBean(UsersRepository.class);
			AccountsRepository accountsRepository = applicationContext.getBean(AccountsRepository.class);

			ExampleMatcher exampleMatcher = ExampleMatcher
					.matchingAll()
					.withStringMatcher(ExampleMatcher.StringMatcher.EXACT)
					.withIgnoreNullValues();

			UserEntity exampleUserEntity = new UserEntity();
			exampleUserEntity.setEmail("alexandre@email.com");

			List<UserEntity> testBase = usersRepository.findAll(Example.of(exampleUserEntity, exampleMatcher));

			if (CollectionUtils.isEmpty(testBase)) {
				var user = new UserEntity();
				user.setName("Alexandre Salvador de Oliveira");
				user.setEmail("alexandre@email.com");
				user.setMobileNumber("31911112222");

				UserEntity savedUserEntity = usersRepository.save(user);

				var account = new AccountEntity();
				account.setAccountType(AccountTypeEnum.PF);
				account.setUser(savedUserEntity);

				accountsRepository.save(account);
			}
		};
	}
}
