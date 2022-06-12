package io.swagger.service;

import io.swagger.model.*;
import io.swagger.repo.TransactionRepo;
import io.swagger.security.Role;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class TransactionServiceTest {

  @InjectMocks private TransactionService transactionService;
  @Mock private UserService userService;
  @Mock private AccountService accountService;
  @Mock private TransactionRepo transactionRepo;

  private List<Transaction> expectedTransactionList;

  // Test Users, Accounts & Transactions
  private Account accountToCurrent, accountToSavings, accountFromCurrent, accountFromSavings;
  private User customerTo, customerFrom, employee;

  // Performed by employee by default
  private RegularTransaction transactionCurrentToCurrent,
      transactionCurrentToSavings,
      transactionSavingsToCurrent,
      transactionSavingsToSavings,
      transaction1,
      transaction2,
      transaction3;

  @BeforeEach
  void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    // Create test accounts
    accountSetup();
    // create fake users
    userSetup();
    // Transaction Templates
    transactionSetup();
    // Service Mocking
    serviceSetup();
  }

  private void serviceSetup() throws Exception {

    // Account Service
    when(accountService.getAccountByIban(accountToCurrent.getIban())).thenReturn(accountToCurrent);
    when(accountService.getAccountByIban(accountFromCurrent.getIban()))
        .thenReturn(accountFromCurrent);
    when(accountService.getAccountByIban(accountToSavings.getIban())).thenReturn(accountToSavings);
    when(accountService.getAccountByIban(accountFromSavings.getIban()))
        .thenReturn(accountFromSavings);

    // User service
    when(userService.getUserByIban(accountToCurrent)).thenReturn(customerTo);
    when(userService.getUserByIban(accountToSavings)).thenReturn(customerTo);
    when(userService.getUserByIban(accountFromCurrent)).thenReturn(customerFrom);
    when(userService.getUserByIban(accountFromSavings)).thenReturn(customerFrom);

    when(userService.getUserById(employee.getId())).thenReturn(employee);
    when(userService.getUserById(customerTo.getId())).thenReturn(customerTo);
    when(userService.getUserById(customerFrom.getId())).thenReturn(customerFrom);

    // Transaction Repo
    when(transactionRepo.save(transactionCurrentToCurrent)).thenReturn(transactionCurrentToCurrent);
    when(transactionRepo.save(transactionCurrentToSavings)).thenReturn(transactionCurrentToSavings);
    when(transactionRepo.save(transactionSavingsToCurrent)).thenReturn(transactionSavingsToCurrent);
    when(transactionRepo.save(transactionSavingsToSavings)).thenReturn(transactionSavingsToSavings);
    when(transactionRepo.save(transaction1)).thenReturn(transaction1);
    when(transactionRepo.save(transaction2)).thenReturn(transaction2);
    when(transactionRepo.save(transaction3)).thenReturn(transaction3);
    when(transactionRepo.findAll()).thenReturn(expectedTransactionList);
    when(transactionRepo.findById(transaction1.getTransactionId())).thenReturn(Optional.of(transaction1));
  }

  private void accountSetup() {
    accountToCurrent = new Account("accountToCurrent", AccountType.CURRENT, 1000d);
    accountToSavings = new Account("accountToSavings", AccountType.SAVING, 1000d);
    accountFromCurrent = new Account("accountFromCurrent", AccountType.CURRENT, 1000d);
    accountFromSavings = new Account("accountFromSavings", AccountType.SAVING, 1000d);
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
                add(accountToSavings);
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
                add(accountFromSavings);
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
    transaction1 =
        new RegularTransaction(
            accountToCurrent.getIban(),
            accountFromCurrent.getIban(),
            690d,
            employee.getId(),
            LocalDateTime.now());
    transaction2 =
        new RegularTransaction(
            accountToCurrent.getIban(), accountToSavings.getIban(), 50.0, customerTo.getId());
    transaction3 =
        new RegularTransaction(
            accountToCurrent.getIban(), accountFromCurrent.getIban(), 80.0, customerFrom.getId());
    transactionCurrentToCurrent =
        new RegularTransaction(
            accountToCurrent.getIban(),
            accountFromCurrent.getIban(),
            200d,
            employee.getId(),
            LocalDateTime.now());

    transactionCurrentToSavings =
        new RegularTransaction(
            accountToSavings.getIban(),
            accountFromCurrent.getIban(),
            400d,
            employee.getId(),
            LocalDateTime.now());

    transactionSavingsToCurrent =
        new RegularTransaction(
            accountToCurrent.getIban(),
            accountFromSavings.getIban(),
            400d,
            employee.getId(),
            LocalDateTime.now());

    transactionSavingsToSavings =
        new RegularTransaction(
            accountToSavings.getIban(),
            accountFromSavings.getIban(),
            400d,
            employee.getId(),
            LocalDateTime.now());

    expectedTransactionList =
        new ArrayList<Transaction>() {
          {
            add(transactionCurrentToCurrent);
            add(transactionCurrentToSavings);
            add(transactionSavingsToCurrent);
            add(transactionSavingsToSavings);
            add(transaction1);
            add(transaction2);
            add(transaction3);
          }
        };

    //    transactionRepo.save(transaction2);
    //    transactionRepo.save(transaction3);
  }

  @AfterEach
  void tearDown() {
    // Reset for each test
    accountToCurrent = accountToSavings = accountFromCurrent = accountFromSavings = null;
    customerTo = customerFrom = employee = null;
    transactionCurrentToCurrent =
        transactionCurrentToSavings =
            transactionSavingsToCurrent = transactionSavingsToSavings = null;
    expectedTransactionList = null;
  }

  @Test
  @DisplayName("Returns a list of Transactions")
  void getTransactions() {
    assertNotNull(transactionService.getTransactions());
  }

  @Test
  @DisplayName("Returns a transaction with the requested valid id")
  void getTransactionById() throws Exception {
    assertNotNull(
        transactionService.getTransactionById(transaction1.getTransactionId().toString()));
  }

  /*@Test
  @DisplayName("Removes Dashes from the provided string during UUID conversion")
  void getTransactionByIdRemovesDashesInString() {
    String testUUID = transaction3.getTransactionDate().toString();
    UUID newTestUUID = UUID.fromString(testUUID.replace("-",""));
    assertEquals(newTestUUID,transaction3.getTransactionId());
  }*/

  @Test
  @DisplayName("Throws an exception if provided a wrong/non-existent id")
  void getTransactionByIdThrowsExceptionIfProvidedWrongID() throws Exception {
    assertThrows(Exception.class, () -> transactionService.getTransactionById("testtesttesttest"));
  }

  //does not work yet, seems like customerFrom has no transactions?
  @Test
  @DisplayName("Returns a filtered list based on user id provided")
  void getTransactionsByUserId() throws Exception {
    assertNotNull(transactionService.getTransactionsByUserId(customerFrom.getId(), 10, 0));
  }

  @Test
  @DisplayName("Throws an if user ID provided is not a real user")
  void getTransactionsByUserIdThrowsExceptionIfProvidedWrongID() {
    assertThrows(
        Exception.class,
        () -> transactionService.getTransactionsByUserId(UUID.randomUUID(), 10, 0));
  }

  @Test
  @DisplayName("Throws an if no transactions are present")
  void getTransactionsByUserIdThrowsExceptionIfNoTransactionsPresent() throws Exception {
    when(userService.getUserById(customerFrom.getId())).thenReturn(employee);
    assertThrows(
        Exception.class,
        () -> transactionService.getTransactionsByUserId(customerFrom.getId(), 10, 0));
  }

  //does not work, requires a transaction
  @Test
  @DisplayName("Returns a list of transactions associated with provided iban")
  void getTransactionsByIban() throws Exception {
    expectedTransactionList.add(new Deposit("accountToCurrent",123d ,employee.getId()));
    expectedTransactionList.add(new Deposit("accountFromCurrent",100d ,employee.getId()));
    expectedTransactionList.add(new Deposit("accountFromCurrent",100d ,employee.getId()));
    expectedTransactionList.add(new Withdrawal("accountToCurrent",123d ,employee.getId()));
    expectedTransactionList.add(new Withdrawal("accountFromCurrent",100d ,employee.getId()));
    expectedTransactionList.add(new Withdrawal("accountFromCurrent",100d ,employee.getId()));
    List<Transaction> returnedTransactionList;
    assertNotNull(transactionService.getTransactionsByIban(accountFromCurrent.getIban(), 10, 0));


  }

  @Test
  @DisplayName("Throws an exception when provided an iban with no transactions")
  @Disabled
  void getTransactionsByIbanThrowsExceptionIfProvidedIbanWithNoTransactions() {
    assertThrows(
        Exception.class,
        () -> transactionService.getTransactionsByIban(accountFromCurrent.getIban(), 10, 0));
  }

  @Test
  @DisplayName("Throws an exception when provided an invalid iban")
  @Disabled
  void getTransactionsByIbanThrowsExceptionIfProvidedInvalidIban() {
    assertThrows(
        Exception.class, () -> transactionService.getTransactionsByIban("invalidIban", 10, 0));
  }

  @Test
  @DisplayName("Deletes a transaction")
  void deleteTransactionById() throws Exception {
    transactionService.deleteTransactionById(transaction1.getTransactionId().toString());

    verify(transactionRepo).deleteById(any());
  }

  @Test
  @DisplayName("Error thrown when deleteById fails")
  void deleteTransactionByIdThrowsException() throws Exception {

    doThrow(new IllegalStateException("DB Error")).when(transactionRepo).deleteById(any());
    assertThrows(
        Exception.class,
        () -> transactionService.deleteTransactionById(transaction1.getTransactionId().toString()));

    verify(transactionRepo).deleteById(any());
    when(transactionRepo.findById(transaction1.getTransactionId())).thenReturn(Optional.empty());
    assertThrows(Exception.class, () -> transactionService.getTransactionById(transaction1.getTransactionId().toString()));
  }

  @Test
  @DisplayName("Throws an exception when a transaction that does not exist")
  void deleteTransactionByIdThrowsExceptionWhenTransactionDoesNotExist() {
    assertThrows(Exception.class, () -> transactionService.deleteTransactionById("test"));
  }

  @Test
  @DisplayName("createTransaction throws Illegal state exception if date older than 5 minutes")
  public void
      createTransactionShouldNotAllowDatesOlderThan5MinutesPast_ThrowsIllegalStateException() {

    // Date can only be set in constructor
    RegularTransaction expectedTransaction =
        new RegularTransaction(
            accountToCurrent.getIban(),
            accountToCurrent.getIban(),
            400d,
            employee.getId(),
            LocalDateTime.now().minusMinutes(5));

    assertThrows(
        IllegalStateException.class,
        () -> transactionService.createTransaction(expectedTransaction, employee));
  }

  @Test
  @DisplayName("createTransaction should not allow accountTo that does not exist")
  public void
      createTransactionShouldNotAllowAccountToThatDoNotExist_ThrowsIllegalArgumentException() {

    transactionCurrentToCurrent.setAccountTo("ThisAccountDoesNotExist");

    assertThrows(
        IllegalArgumentException.class,
        () -> transactionService.createTransaction(transactionCurrentToCurrent, employee));
  }

  @ParameterizedTest
  @DisplayName("createTransaction should not allow accountFrom that does not exist")
  @ValueSource(strings = {"", "fakeAccount", "123"})
  public void
      createTransactionShouldNotAllowAccountFromThatDoNotExist_ThrowsIllegalArgumentException(
          String account) {
    RegularTransaction expectedTransaction =
        new RegularTransaction(account, "", 400d, UUID.randomUUID(), LocalDateTime.now());
    assertThrows(
        IllegalArgumentException.class,
        () -> transactionService.createTransaction(expectedTransaction, employee));
  }

  @Test
  @DisplayName("Valid transaction between two current accounts performed by employee")
  public void createTransactionTest() throws Exception {

    Transaction returnedTransaction =
        transactionService.createTransaction(transactionCurrentToCurrent, employee);

    assertNotNull(returnedTransaction);
    assertEquals(transactionCurrentToCurrent, returnedTransaction);
  }

  @Test
  @DisplayName(
      "createTransaction should not allow customer to transfer from account that is not theirs")
  public void
      createTransactionShouldNotAllowUserToTransferFromAccountThatIsNotTheirs_ThrowsIllegalArgumentException() {

    transactionCurrentToCurrent.setAccountFrom(accountFromCurrent.getIban());
    transactionCurrentToCurrent.setPerformedBy(customerTo.getId());
    assertThrows(
        IllegalArgumentException.class,
        () -> transactionService.createTransaction(transactionCurrentToCurrent, customerTo));
  }

  @Test
  @DisplayName(
      "createTransaction should now allow to transfer between savings account if not same user")
  public void
      createTransactionShouldNotAllowUserToTransferFromSavingsAccountToAnotherSavingsAccount_ThrowsIllegalArgumentException() {

    // Savings to another users savings
    assertThrows(
        IllegalArgumentException.class,
        () -> transactionService.createTransaction(transactionSavingsToSavings, employee));

    transactionSavingsToSavings.setAccountTo(accountFromSavings.getIban());

    // Savings to same user (same account)
    assertDoesNotThrow(
        () -> transactionService.createTransaction(transactionSavingsToSavings, employee));
  }

  @Test
  @DisplayName("undoTransaction should be called when error is thrown")
  public void undoTransactionShouldBeCalledWhenErrorThrown() throws Exception {
    when(accountService.subtractBalance(
            transactionCurrentToCurrent.getAccountFrom(), transactionCurrentToCurrent.getAmount()))
        .thenThrow(new Exception());

    assertThrows(
        Exception.class,
        () -> transactionService.createTransaction(transactionCurrentToCurrent, employee));
    verify(accountService).subtractBalance(Mockito.anyString(), Mockito.anyDouble());
  }

  @Test
  @DisplayName("undoTransactionShould call subtractBalance and addBalance twice when error occurs")
  public void undoTransactionShouldCallSubtractBalanceAndAddBalanceTwiceWhenErrorOccurs()
      throws Exception {
    when(transactionRepo.save(transactionCurrentToCurrent))
        .thenThrow(new IllegalStateException("Bad stuff happened"));
    when(accountService.subtractBalance(
            transactionCurrentToCurrent.getAccountFrom(), transactionCurrentToCurrent.getAmount()))
        .thenReturn(true);
    when(accountService.addBalance(
            transactionCurrentToCurrent.getAccountTo(), transactionCurrentToCurrent.getAmount()))
        .thenReturn(true);

    assertThrows(
        Exception.class,
        () -> transactionService.createTransaction(transactionCurrentToCurrent, employee));
    verify(accountService, times(2)).subtractBalance(Mockito.anyString(), Mockito.anyDouble());
    verify(accountService, times(2)).addBalance(Mockito.anyString(), Mockito.anyDouble());
  }

  @Test
  @DisplayName("withdrawMoney calls transaction repo and accountService")
  public void withdrawMoney() throws Exception {
    Withdrawal withdrawal = new Withdrawal("AccountFrom", 100d, employee.getId());
    transactionService.withdrawMoney(withdrawal, employee);
    verify(transactionRepo).save(withdrawal);
    verify(accountService).subtractBalance(withdrawal.getAccountFrom(), withdrawal.getAmount());
  }

  @Test
  @DisplayName("withdrawMoney does not allow customer to access another account")
  public void withdrawMoneyDoesNotAllowCustomerToAccessOtherAccount_ThrowsIllegalArgumentException() throws Exception {
    Withdrawal withdrawal = new Withdrawal("AccountFrom", 100d, customerTo.getId());

    when(accountService.getAccountByIban(withdrawal.getAccountFrom())).thenReturn(accountFromCurrent);

    assertThrows(IllegalArgumentException.class, () -> transactionService.withdrawMoney(withdrawal, customerTo));
    verify(transactionRepo, times(0)).save(withdrawal);
    verify(accountService, times(0)).subtractBalance(withdrawal.getAccountFrom(), withdrawal.getAmount());
  }

  @Test
  void getAllTransactionsForAccountByIban() {}

  @Test
  @DisplayName("depositMoney calls transaction repo and accountService")
  void depositMoney() throws Exception {
    Deposit deposit = new Deposit("AccountTo", 100d, employee.getId());
    transactionService.depositMoney(deposit);
    verify(transactionRepo).save(deposit);
    verify(accountService).addBalance(deposit.getAccountTo(), deposit.getAmount());
  }
}
