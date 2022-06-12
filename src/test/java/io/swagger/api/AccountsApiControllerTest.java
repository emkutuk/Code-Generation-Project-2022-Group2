package io.swagger.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.model.*;
import io.swagger.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Collections;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AccountsApiControllerTest
{
    @Autowired
    private MockMvc mvc;

    @MockBean
    private AccountService accountService;

    private User user;
    private Account account;
    private String customerToken;
    private String employeeToken;
    ObjectMapper mapper;

    @BeforeEach
    public void setup() throws Exception
    {
        account = new Account("testIban", AccountType.SAVING, 15.0);
        mapper = new ObjectMapper();

        user = new User("Hein", "Eken", "31685032148", "customer", "customer", new ArrayList<>(), io.swagger.security.Role.ROLE_CUSTOMER, AccountStatus.ACTIVE);
        LoginDto customer = new LoginDto("customer", "customer");
        LoginDto employee = new LoginDto("employee", "employee");

        customerToken = this.mvc.perform(post("/Login").contentType(MediaType.APPLICATION_JSON_VALUE).content(mapper.writeValueAsString(customer))).andReturn().getResponse().getContentAsString();
        employeeToken = this.mvc.perform(post("/Login").contentType(MediaType.APPLICATION_JSON_VALUE).content(mapper.writeValueAsString(employee))).andReturn().getResponse().getContentAsString();

    }

    @Test
    public void addANewAccount() throws Exception
    {
        given(accountService.addANewAccount(account)).willReturn(account);

        this.mvc.perform(post("/Accounts")
                .header("authorization", "Bearer " + employeeToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(account)))
                .andExpect(status().isCreated());
    }

    @Test
    public void addANewAccountReturnsBadRequestWhenFails() throws Exception
    {
        given(accountService.addANewAccount(account)).willReturn(null);

        this.mvc.perform(post("/Accounts")
                .header("authorization", "Bearer " + employeeToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(account)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void changeAccountType() throws Exception
    {
        Account updatedAccount = account;
        updatedAccount.setAccountType(AccountType.CURRENT);
        given(accountService.changeAccountType(account.getIban(), "current")).willReturn(updatedAccount);

        this.mvc.perform(post("/Accounts/"+account.getIban()+"/current")
                .header("authorization", "Bearer " + employeeToken))
                .andExpect(status().isOk());
    }

    @Test
    public void changeAccountTypeReturnsNullFromService() throws Exception
    {
        Account updatedAccount = account;
        updatedAccount.setAccountType(AccountType.CURRENT);
        given(accountService.changeAccountType(account.getIban(), "current")).willReturn(null);

        this.mvc.perform(post("/Accounts/"+account.getIban()+"/current")
                .header("authorization", "Bearer " + employeeToken))
                .andExpect(status().isNotFound());
    }

    @Test
    public void changeAccountTypeReturns() throws Exception
    {
        Account updatedAccount = account;
        updatedAccount.setAccountType(AccountType.CURRENT);
        given(accountService.changeAccountType(account.getIban(), "current")).willReturn(updatedAccount);

        this.mvc.perform(post("/Accounts/"+account.getIban()+"/current")
                .header("authorization", "Bearer " + employeeToken))
                .andExpect(status().isOk());
    }

    @Test
    public void changeAccountStatusWithEmployee() throws Exception
    {
        this.mvc.perform(post("/Accounts/"+account.getIban()+"/Status/closed")
                .header("authorization", "Bearer " + employeeToken))
                .andExpect(status().isOk());
    }

    @Test
    public void testChangeAnotherAccountsStatusWithCustomer() throws Exception
    {
        this.mvc.perform(post("/Accounts/"+account.getIban()+"/Status/disabled")
                .header("authorization", "Bearer " + customerToken))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getAccountBalanceByIban() throws Exception
    {
        given(accountService.getAccountBalanceByIban(account.getIban())).willReturn(account.getBalance());
        this.mvc.perform(get("/Accounts/"+account.getIban()+"/Balance")
                .header("authorization", "Bearer " + employeeToken))
                .andExpect(status().isOk());
    }

    @Test
    public void getAccountByIban() throws Exception
    {
        given(accountService.getAccountByIban(account.getIban())).willReturn(account);
        this.mvc.perform(get("/Accounts/"+account.getIban()).header("authorization", "Bearer " + employeeToken))
                .andExpect(status().isOk());
    }

    @Test
    public void getAllAccounts() throws Exception
    {
        given(accountService.getAllAccounts()).willReturn(Collections.singletonList(account));
        this.mvc.perform(get("/Accounts?offset=0&max=10")
                .header("authorization", "Bearer " + employeeToken))
                .andExpect(status().isOk())
                .andExpect((jsonPath("$", hasSize(1))));
    }

    @Test
    public void updateAccountByIban() throws Exception
    {
        Account newAccount = account;
        newAccount.setAccountType(AccountType.CURRENT);

        this.mvc.perform(put("/Accounts/"+account.getIban())
                .header("authorization", "Bearer " + employeeToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(newAccount)))
                .andExpect(status().isOk());
    }

    @Test
    public void updateAccountByIbanAsACustomerReturnsUnAuthorized() throws Exception
    {
        Account newAccount = account;
        newAccount.setAccountType(AccountType.CURRENT);

        this.mvc.perform(put("/Accounts/"+account.getIban())
                .header("authorization", "Bearer " + customerToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(newAccount)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void getAccountByIbanReturnsNotFoundWhenThereIsNoAccount() throws Exception
    {
        given(accountService.getAccountByIban(account.getIban())).willReturn(null);

        this.mvc.perform(get("/Accounts/"+account.getIban())
                .header("authorization", "Bearer " + employeeToken))
                .andExpect(status().isNotFound());
    }

    @Test
    public void changeAccountStatusReturnsUnauthorized() throws Exception
    {
        given(accountService.changeAccountStatus(account.getIban(), "disabled")).willReturn(2);

        this.mvc.perform(post("/Accounts/"+account.getIban()+"/Status/disabled")
                .header("authorization", "Bearer " + employeeToken))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void getAccountBalanceByIbanReturnsUnauthorizedIfAccountIsNotCustomers() throws Exception
    {
        this.mvc.perform(get("/Accounts/"+account.getIban()+"/Balance")
                .header("authorization", "Bearer " + customerToken))
                .andExpect(status().isUnauthorized());
    }
}