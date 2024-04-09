package dev.alexandreoliveira.microservices.accountsapi.unit.controllers;

import dev.alexandreoliveira.microservices.accountsapi.controllers.AccountsController;
import dev.alexandreoliveira.microservices.accountsapi.controllers.data.accounts.AccountControllerCreateRequest;
import dev.alexandreoliveira.microservices.accountsapi.database.entities.enums.AccountTypeEnum;
import dev.alexandreoliveira.microservices.accountsapi.database.repositories.AccountRepository;
import dev.alexandreoliveira.microservices.accountsapi.database.repositories.UserRepository;
import dev.alexandreoliveira.microservices.accountsapi.dtos.AccountDTO;
import dev.alexandreoliveira.microservices.accountsapi.services.AccountService;
import dev.alexandreoliveira.microservices.accountsapi.services.exceptions.ServiceException;
import dev.alexandreoliveira.microservices.accountsapi.unit.UnitTest;
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

@WebMvcTest(AccountsController.class)
class AccountsControllerTest extends UnitTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    AccountService mockAccountService;

    @MockBean
    UserRepository mockUserRepository;

    @MockBean
    AccountRepository mockAccountRepository;

    @BeforeEach
    void beforeEach() {
        Mockito.clearInvocations(mockAccountService, mockUserRepository, mockAccountRepository);
        Mockito.reset(mockAccountService, mockUserRepository, mockAccountRepository);
    }

    @ParameterizedTest
    @MethodSource("shouldExpectedErrorWhenDataIsWrongSource")
    @Order(1)
    void shouldExpectedErrorWhenDataIsWrong(String data) throws Exception {
        var requestData = data.getBytes(StandardCharsets.UTF_8);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post("/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestData);

        mockMvc
                .perform(request)
                .andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors").isNotEmpty());
    }

    static Stream<String> shouldExpectedErrorWhenDataIsWrongSource() {
        return Stream.of(
                "{}",
                "{\"userId\":\"\", \"accountType\": \"\"}",
                "{\"userId\":\"\", \"accountType\": \"INVALID_TYPE\"}"
        );
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

        var fakeAccount = new AccountControllerCreateRequest(
                0L,
                AccountTypeEnum.PF.name()
        );

        Mockito.doThrow(new ServiceException("User not found")).when(mockAccountService).createAccount(fakeAccount);

        mockMvc
                .perform(request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors").isNotEmpty());
    }

    @Test
    @Order(3)
    void mustBeOkWhenDataIsCorrect() throws Exception {
        var fakeAccount = new AccountDTO();
        fakeAccount.setId(1L);
        fakeAccount.setAccountType(AccountTypeEnum.PF.name());
        fakeAccount.setUserId(1L);
        fakeAccount.setAccountNumber("0001000203");

        var expectedData = new AccountControllerCreateRequest(
                1L,
                AccountTypeEnum.PF.name()
        );

        Mockito.doReturn(fakeAccount).when(mockAccountService).createAccount(expectedData);

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
                .andExpect(MockMvcResultMatchers.header().exists("location"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.userId", Matchers.equalTo(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.accountNumber", Matchers.is(Matchers.not(Matchers.emptyString()))));
    }
}
