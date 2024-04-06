package dev.alexandreoliveira.microservices.accountsapi.integrated.controllers;

import dev.alexandreoliveira.microservices.accountsapi.database.entities.UserEntity;
import dev.alexandreoliveira.microservices.accountsapi.database.repositories.UserRepository;
import dev.alexandreoliveira.microservices.accountsapi.integrated.IntegratedTest;
import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.nio.charset.StandardCharsets;

@AutoConfigureMockMvc
class AccountControllerTest extends IntegratedTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @Test
    @Order(1)
    void shouldExpectedErrorWhenDataIsWrong() throws Exception {
        var requestData = "{}".getBytes(StandardCharsets.UTF_8);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post("/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestData);

        mockMvc
                .perform(request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.is(Matchers.not(Matchers.emptyArray()))));
    }

    @Test
    @Order(2)
    void shouldExpectedErrorWhenUserNotFound() throws Exception {
        var requestData = """
                {
                    "userId": 0,
                    "accountType": "PF"
                }
                """.getBytes(StandardCharsets.UTF_8);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post("/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestData);

        mockMvc
                .perform(request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.is(Matchers.not(Matchers.emptyArray()))))
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.contains("User not found")));
    }

    @Test
    @Order(3)
    void shouldExpectACorrectData() throws Exception {
        var user = new UserEntity();
        user.setName("User");
        user.setEmail("user@email.com");
        user.setMobileNumber("31911112222");

        UserEntity savedUserEntity = userRepository.save(user);

        Assertions.assertThat(savedUserEntity).isNotNull();
        Assertions.assertThat(savedUserEntity.getId()).isPositive().isEqualTo(1L);

        var requestData = """
                {
                    "userId": 1,
                    "accountType": "PF"
                }
                """.getBytes(StandardCharsets.UTF_8);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post("/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestData);

        mockMvc
                .perform(request)
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.accountNumber").isNotEmpty());
    }
}
