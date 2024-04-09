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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

@AutoConfigureMockMvc
class UsersControllerTest extends IntegratedTest {

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
                .andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors").isNotEmpty());
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
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").isNumber());
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
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").isNumber());
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
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").isNumber())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.accounts").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.accounts[*].accountNumber").isNotEmpty());
    }



    @ParameterizedTest
    @MethodSource("shouldExpectedAnExceptionWhenTryToCreateUsersWithSameEmailOrMobileNumberSource")
    @Order(5)
    void shouldExpectedAnExceptionWhenTryToCreateUsersWithSameEmailOrMobileNumber(byte[] data) throws Exception {
        var user = new UserEntity();
        user.setName("Name");
        user.setEmail("alexandre@email.com");
        user.setMobileNumber("31933334444");

        UserEntity savedUser = userRepository.save(user);

        Assertions.assertThat(savedUser).isNotNull();
        Assertions.assertThat(savedUser.getId()).isPositive().isEqualTo(1L);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(data);

        mockMvc
                .perform(request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors", Matchers.contains("This email / mobileNumber exists")));
    }

    static Stream<byte[]> shouldExpectedAnExceptionWhenTryToCreateUsersWithSameEmailOrMobileNumberSource() {
        var requestData_email = """
                {
                	"name": "Alexandre Salvador de Oliveira",
                	"email": "alexandre@email.com",
                	"mobileNumber": "31933334422"
                }
                """.getBytes(StandardCharsets.UTF_8);

        var requestData_mobile_number = """
                {
                	"name": "Alexandre Salvador de Oliveira",
                	"email": "not-same-email@email.com",
                	"mobileNumber": "31933334444"
                }
                """.getBytes(StandardCharsets.UTF_8);

        return Stream.of(
                requestData_email,
                requestData_mobile_number
        );
    }
}
