package io.swagger.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.model.*;
import io.swagger.security.Role;
import io.swagger.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TransactionsApiControllerTest {

  @Autowired ObjectMapper mapper;
  @Autowired private MockMvc mvc;
  @MockBean private TransactionService transactionService;
  private User user;
  private String customerToken;
  private String employeeToken;
  private List<Transaction> expectedTransactionList;

  // Test Users, Accounts & Transactions
  private Account accountToCurrent, accountFromCurrent;
  private User customerTo, customerFrom, employee;

  // Performed by employee by default
  private RegularTransaction transactionCurrentToCurrent;

  @BeforeEach
  public void setup() throws Exception {
    mapper = new ObjectMapper();
    mapper.findAndRegisterModules();

    user =
        new User(
            "Hein",
            "Eken",
            "31685032148",
            "customer",
            "customer",
            new ArrayList<>(),
            io.swagger.security.Role.ROLE_CUSTOMER,
            AccountStatus.ACTIVE);
    LoginDto customer = new LoginDto("customer", "customer");
    LoginDto employee = new LoginDto("employee", "employee");

    accountSetup();
    userSetup();
    transactionSetup();

    customerToken =
        this.mvc
            .perform(
                post("/Login")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(mapper.writeValueAsString(customer)))
            .andReturn()
            .getResponse()
            .getContentAsString();
    employeeToken =
        this.mvc
            .perform(
                post("/Login")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(mapper.writeValueAsString(employee)))
            .andReturn()
            .getResponse()
            .getContentAsString();
  }

  private void accountSetup() {
    accountToCurrent = new Account("accountToCurrent", AccountType.CURRENT, 1000d);
    accountFromCurrent = new Account("accountFromCurrent", AccountType.CURRENT, 1000d);
  }

  private void userSetup() {
    customerTo =
        new User(
            "Customer",
            "To",
            "12345",
            "customerTo",
            "customerTo",
            new ArrayList<Account>() {
              {
                add(accountToCurrent);
              }
            },
            Role.ROLE_CUSTOMER,
            AccountStatus.ACTIVE);
    customerFrom =
        new User(
            "Customer",
            "From",
            "12345",
            "customerFrom",
            "customerFrom",
            new ArrayList<Account>() {
              {
                add(accountFromCurrent);
              }
            },
            Role.ROLE_CUSTOMER,
            AccountStatus.ACTIVE);
    employee =
        new User(
            "Employee",
            "Employee",
            "12345",
            "Employee",
            "Employee",
            null,
            Role.ROLE_EMPLOYEE,
            AccountStatus.ACTIVE);
  }

  private void transactionSetup() {
    transactionCurrentToCurrent =
        new RegularTransaction(
            accountToCurrent.getIban(),
            accountFromCurrent.getIban(),
            200d,
            employee.getId(),
            LocalDateTime.now());

    expectedTransactionList =
        new ArrayList<Transaction>() {
          {
            add(transactionCurrentToCurrent);
          }
        };
  }

  @Test
  @DisplayName("Create transaction should create new transaction and return created transaction")
  void createTransaction() throws Exception {
    given(transactionService.createTransaction(transactionCurrentToCurrent, user))
        .willReturn(transactionCurrentToCurrent);

    // Can't test getting user without breaking security
    this.mvc
        .perform(
            post("/Transactions")
                .header("authorization", "Bearer " + employeeToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(transactionCurrentToCurrent)))
        .andExpect(status().isCreated());
  }

  @Test
  @DisplayName("Create transaction with invalid format returns Bad request")
  void createTransactionInvalidFormat() throws Exception {
    given(transactionService.createTransaction(transactionCurrentToCurrent, user))
        .willReturn(transactionCurrentToCurrent);

    this.mvc
        .perform(
            post("/Transactions")
                .header("authorization", "Bearer " + employeeToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("ahhh"))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Get all transactions of current user or all users")
  void getTransactionsByUser() throws Exception {
    this.mvc
        .perform(get("/Transactions/").header("authorization", "Bearer " + employeeToken))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("Delete Transaction should delete transaction with specified id")
  void deleteTransactionById() throws Exception {
    this.mvc
        .perform(
            delete("/Transactions/" + transactionCurrentToCurrent.getTransactionId())
                .header("authorization", "Bearer " + employeeToken))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("Deposit money should create Deposit and return it with status OK")
  void depositMoney() throws Exception {
    this.mvc
        .perform(
            post("/Transactions/Deposit")
                .header("authorization", "Bearer " + employeeToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(""))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("Edit Transaction should update transaction and return OK")
  void editTransactionById() throws Exception {
    this.mvc
        .perform(
            put("/Transactions/" + transactionCurrentToCurrent.getTransactionId())
                .header("authorization", "Bearer " + employeeToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(transactionCurrentToCurrent.toString())))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("Get Transaction by IBAN should return list of transactions related to IBAN")
  void getTransactionByIBAN() throws Exception {
    this.mvc
        .perform(
            get("/Transactions/" + accountFromCurrent.getIban())
                .header("authorization", "Bearer " + employeeToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("Get transaction by id should return transaction with specified id")
  void getTransactionById() throws Exception {
    this.mvc
        .perform(
            get("/Transactions/" + transactionCurrentToCurrent.getTransactionId())
                .header("authorization", "Bearer " + employeeToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("Withdraw money returns OK")
  void withdrawMoney() throws Exception {
    this.mvc
        .perform(
            get("/Transactions/Withdraw")
                .header("authorization", "Bearer " + employeeToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isOk());
  }
}
