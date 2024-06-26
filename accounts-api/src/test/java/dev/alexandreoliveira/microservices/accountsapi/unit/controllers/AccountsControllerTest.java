package dev.alexandreoliveira.microservices.accountsapi.unit.controllers;

import dev.alexandreoliveira.microservices.accountsapi.controllers.AccountsController;
import dev.alexandreoliveira.microservices.accountsapi.controllers.data.accounts.AccountControllerCreateRequest;
import dev.alexandreoliveira.microservices.accountsapi.database.entities.enums.AccountTypeEnum;
import dev.alexandreoliveira.microservices.accountsapi.database.repositories.AccountsRepository;
import dev.alexandreoliveira.microservices.accountsapi.database.repositories.UsersRepository;
import dev.alexandreoliveira.microservices.accountsapi.dtos.AccountDto;
import dev.alexandreoliveira.microservices.accountsapi.dtos.AccountDtoRepresentationModelAssembler;
import dev.alexandreoliveira.microservices.accountsapi.exceptions.ServiceException;
import dev.alexandreoliveira.microservices.accountsapi.services.AccountService;
import dev.alexandreoliveira.microservices.accountsapi.unit.UnitTest;
import org.hamcrest.Matchers;
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

@WebMvcTest(AccountsController.class)
class AccountsControllerTest extends UnitTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    AccountService mockAccountService;

    @MockBean
    UsersRepository mockUsersRepository;

    @MockBean
    AccountsRepository mockAccountsRepository;

    @MockBean
    AccountDtoRepresentationModelAssembler assembler;

    @BeforeEach
    void beforeEach() {
        Mockito.clearInvocations(mockAccountService, mockUsersRepository, mockAccountsRepository);
        Mockito.reset(mockAccountService, mockUsersRepository, mockAccountsRepository);
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
        UUID userId = UUID.randomUUID();

        var requestData = """
                {
                    "userId": ":userId",
                    "accountType": "PF"
                }
                """
                .replaceAll(":userId", userId.toString())
                .getBytes(StandardCharsets.UTF_8);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post("/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestData);

        var fakeAccount = new AccountControllerCreateRequest(
                userId,
                AccountTypeEnum.PF.name()
        );

        Mockito.doThrow(new ServiceException("User not found")).when(mockAccountService).create(fakeAccount);

        mockMvc
                .perform(request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors").isNotEmpty());
    }

    @Test
    @Order(3)
    void mustBeOkWhenDataIsCorrect() throws Exception {
        UUID accountId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        var fakeAccount = new AccountDto();
        fakeAccount.setId(accountId);
        fakeAccount.setAccountType(AccountTypeEnum.PF.name());
        fakeAccount.setUserId(userId);
        fakeAccount.setAccountNumber("0001000203");

        var expectedData = new AccountControllerCreateRequest(
                userId,
                AccountTypeEnum.PF.name()
        );

        Mockito.doReturn(fakeAccount).when(mockAccountService).create(expectedData);
        Mockito.when(assembler.toModel(Mockito.any(AccountDto.class))).thenCallRealMethod();

        var requestData = """
                {
                    "userId": ":userId",
                    "accountType": "PF"
                }
                """
                .replaceAll(":userId", userId.toString())
                .getBytes(StandardCharsets.UTF_8);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post("/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestData);

        mockMvc
                .perform(request)
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.header().exists("location"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.userId", Matchers.equalTo(userId.toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.accountNumber", Matchers.is(Matchers.not(Matchers.emptyString()))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.links", Matchers.is(Matchers.not(Matchers.empty()))));
    }
}
