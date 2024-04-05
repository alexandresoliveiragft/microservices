package dev.alexandreoliveira.microservices.accountsapi.integrated;

import dev.alexandreoliveira.microservices.accountsapi.AccountsApiApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = {AccountsApiApplication.class}
)
@TestPropertySource(locations = "classpath:application-integrated.yml")
public class IntegratedTest {
}
