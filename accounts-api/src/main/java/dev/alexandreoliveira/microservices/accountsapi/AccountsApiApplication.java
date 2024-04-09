package dev.alexandreoliveira.microservices.accountsapi;

import dev.alexandreoliveira.microservices.accountsapi.database.entities.AccountEntity;
import dev.alexandreoliveira.microservices.accountsapi.database.entities.UserEntity;
import dev.alexandreoliveira.microservices.accountsapi.database.entities.enums.AccountTypeEnum;
import dev.alexandreoliveira.microservices.accountsapi.database.repositories.AccountRepository;
import dev.alexandreoliveira.microservices.accountsapi.database.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
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
			account.setUser(savedUserEntity);

			accountRepository.save(account);
		};
	}
}
