package dev.alexandreoliveira.microservices.accountsapi.unit.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.alexandreoliveira.microservices.accountsapi.controllers.UserController;
import dev.alexandreoliveira.microservices.accountsapi.database.repositories.AccountRepository;
import dev.alexandreoliveira.microservices.accountsapi.database.repositories.UserRepository;
import dev.alexandreoliveira.microservices.accountsapi.dtos.UserDTO;
import dev.alexandreoliveira.microservices.accountsapi.services.UserService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

@WebMvcTest(UserController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService mockUserService;

    @MockBean
    UserRepository mockUserRepository;

    @MockBean
    AccountRepository mockAccountRepository;

    @BeforeEach
    void beforeEach() {
        Mockito.clearInvocations(mockUserService, mockUserRepository, mockAccountRepository);
        Mockito.reset(mockUserService, mockUserRepository, mockAccountRepository);
    }

    @ParameterizedTest
    @MethodSource("shouldExpectedErrorToSendWrongDataSource")
    @Order(1)
    void shouldExpectedErrorToSendWrongData(String data) throws Exception {
        var requestData = data.getBytes(StandardCharsets.UTF_8);

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

    static Stream<String> shouldExpectedErrorToSendWrongDataSource() {
        return Stream.of(
                "{}",
                "{\"name\": \"\", \"email\": \"\", \"mobileNumber\": \"\"}"
        );
    }

    @Test
    @Order(2)
    void mustBeOkWhenDataIsCorrect() throws Exception {
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

        var savedUser = new UserDTO();
        savedUser.setId(1L);
        savedUser.setName("Alexandre Salvador de Oliveira");
        savedUser.setEmail("alexandre@email.com");
        savedUser.setMobileNumber("31933334444");

        UserDTO requestDto = new ObjectMapper().readValue(requestData, UserDTO.class);

        Mockito.doReturn(savedUser).when(mockUserService).createUser(requestDto);

        mockMvc
                .perform(request)
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.header().exists("location"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber());
    }

    @Test
    @Order(3)
    void shoudExpectedCorrectUserWithAccount() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get("/users/1");

        var fakeServiceResponse = """
                {
                	"id": 1,
                	"name": "Alexandre Salvador de Oliveira",
                	"email": "alexandre@email.com",
                	"mobileNumber": "31911112222",
                	"accounts": [
                		{
                			"id": 1,
                			"accountNumber": "0001110001",
                			"accountType": "PF"
                		}
                	]
                }
                """;

        var fakeData = new ObjectMapper().readValue(fakeServiceResponse, UserDTO.class);

        Mockito.doReturn(fakeData).when(mockUserService).find(1L);

        mockMvc
                .perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber())
                .andExpect(MockMvcResultMatchers.jsonPath("$.accounts").isArray());
    }
}
