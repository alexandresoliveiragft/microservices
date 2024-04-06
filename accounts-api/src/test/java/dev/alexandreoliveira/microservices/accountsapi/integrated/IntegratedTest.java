package dev.alexandreoliveira.microservices.accountsapi.integrated;

import dev.alexandreoliveira.microservices.accountsapi.AccountsApiApplication;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = {AccountsApiApplication.class}
)
@TestPropertySource(locations = "classpath:application-integrated.yml")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class IntegratedTest {
}
