package dev.alexandreoliveira.microservices.accountsapi.unit.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.alexandreoliveira.microservices.accountsapi.controllers.UsersController;
import dev.alexandreoliveira.microservices.accountsapi.controllers.data.users.UserControllerCreateRequest;
import dev.alexandreoliveira.microservices.accountsapi.database.repositories.AccountRepository;
import dev.alexandreoliveira.microservices.accountsapi.database.repositories.UserRepository;
import dev.alexandreoliveira.microservices.accountsapi.dtos.UserDTO;
import dev.alexandreoliveira.microservices.accountsapi.services.UserService;
import dev.alexandreoliveira.microservices.accountsapi.services.exceptions.ServiceException;
import dev.alexandreoliveira.microservices.accountsapi.unit.UnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
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
import java.util.UUID;
import java.util.stream.Stream;

@WebMvcTest(UsersController.class)
class UsersControllerTest extends UnitTest {

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
                .andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors").isNotEmpty());
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
        savedUser.setId(UUID.randomUUID());
        savedUser.setName("Alexandre Salvador de Oliveira");
        savedUser.setEmail("alexandre@email.com");
        savedUser.setMobileNumber("31933334444");

        UserControllerCreateRequest requestDto = new ObjectMapper()
                .readValue(requestData, UserControllerCreateRequest.class);

        Mockito.doReturn(savedUser).when(mockUserService).createUser(requestDto);

        mockMvc
                .perform(request)
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.header().exists("location"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").isNotEmpty());
    }

    @Test
    @Order(3)
    void shoudExpectedCorrectUserWithAccount() throws Exception {
        UUID accountId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get("/users/" + userId);

        var fakeServiceResponse = """
                {
                	"id": ":accountId",
                	"name": "Alexandre Salvador de Oliveira",
                	"email": "alexandre@email.com",
                	"mobileNumber": "31911112222",
                	"accounts": [
                		{
                			"id": ":userId",
                			"accountNumber": "0001110001",
                			"accountType": "PF"
                		}
                	]
                }
                """
                .replaceAll(":accountId", accountId.toString())
                .replaceAll(":userId", userId.toString());

        var fakeData = new ObjectMapper().readValue(fakeServiceResponse, UserDTO.class);

        Mockito.doReturn(fakeData).when(mockUserService).find(Mockito.any(UUID.class));

        mockMvc
                .perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.id").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.accounts").isArray());
    }

    @Test
    @Order(4)
    void shouldExpectedAnExceptionWhenDataExists() throws Exception {
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

        UserControllerCreateRequest requestDto = new ObjectMapper()
                .readValue(requestData, UserControllerCreateRequest.class);

        Mockito.when(mockUserService.createUser(requestDto))
                .thenThrow(new ServiceException("This email / mobileNumber exists"));

        mockMvc
                .perform(request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors").isNotEmpty());
    }
}
