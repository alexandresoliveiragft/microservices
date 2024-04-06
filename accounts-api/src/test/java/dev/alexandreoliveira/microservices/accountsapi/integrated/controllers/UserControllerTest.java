package dev.alexandreoliveira.microservices.accountsapi.integrated.controllers;

import dev.alexandreoliveira.microservices.accountsapi.database.entities.AccountEntity;
import dev.alexandreoliveira.microservices.accountsapi.database.entities.UserEntity;
import dev.alexandreoliveira.microservices.accountsapi.database.entities.enums.AccountTypeEnum;
import dev.alexandreoliveira.microservices.accountsapi.database.repositories.AccountRepository;
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
class UserControllerTest extends IntegratedTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    AccountRepository accountRepository;

    @Test
    @Order(1)
    void shouldExpectedErrorWhenDataIsWrong() throws Exception {
        var requestData = "{}".getBytes(StandardCharsets.UTF_8);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post("/users")
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
    void shouldExpectAValidUser() throws Exception {
        var requestData = """
                {
                	"name": "Alexandre Salvador de Oliveira",
                	"email": "alexandre@email.com",
                	"mobileNumber": "31933334444"
                }
                """.getBytes(StandardCharsets.UTF_8);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestData);

        mockMvc
                .perform(request)
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.header().exists("location"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber());
    }

    @Test
    @Order(3)
    void shouldExpectedToReturnACorrectUser() throws Exception {
        var user = new UserEntity();
        user.setName("Name");
        user.setEmail("user@email.com");
        user.setMobileNumber("31911112222");

        UserEntity savedUser = userRepository.save(user);

        Assertions.assertThat(savedUser).isNotNull();
        Assertions.assertThat(savedUser.getId()).isPositive().isEqualTo(1L);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get("/users/1");

        mockMvc
                .perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber());
    }

    @Test
    @Order(4)
    void shouldExpectedToReturnACorrectUserWithAccount() throws Exception {
        var user = new UserEntity();
        user.setName("Name");
        user.setEmail("user@email.com");
        user.setMobileNumber("31911112222");

        UserEntity savedUser = userRepository.save(user);

        Assertions.assertThat(savedUser).isNotNull();
        Assertions.assertThat(savedUser.getId()).isPositive().isEqualTo(1L);

        var account = new AccountEntity();
        account.setAccountType(AccountTypeEnum.PF);
        account.setUser(savedUser);

        AccountEntity savedAccount = accountRepository.save(account);

        Assertions.assertThat(savedAccount).isNotNull();
        Assertions.assertThat(savedAccount.getId()).isPositive().isEqualTo(1L);
        Assertions.assertThat(savedAccount.getAccountNumber()).isNotBlank();

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get("/users/1");

        mockMvc
                .perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber())
                .andExpect(MockMvcResultMatchers.jsonPath("$.accounts").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.accounts[*].accountNumber").isNotEmpty());
    }

}
