package io.swagger.configuration;

import io.swagger.model.*;
import io.swagger.security.Role;
import io.swagger.service.AccountService;
import io.swagger.service.TransactionService;
import io.swagger.service.UserService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Component
@Transactional
@Log
@ComponentScan
public class AppRunner implements ApplicationRunner {
  @Autowired AccountService accountService;

  @Autowired UserService userService;

  @Autowired TransactionService transactionService;

  List<Account> accountList = new ArrayList<Account>();
  Random rnd = new Random();

  @Override
  public void run(ApplicationArguments args) throws Exception {
    // Creating the account for the bank
    Account bankAccount = new Account("NL01INHO0000000001", AccountType.CURRENT, 0.0);
    accountService.addANewAccount(bankAccount);

    /*
    for (int i = 0; i < 50; i++) {
      if (rnd.nextBoolean())
        accountList.add(new Account(AccountType.CURRENT, (double) rnd.nextInt(500000)));
      else accountList.add(new Account(AccountType.CURRENT, (double) rnd.nextInt(500000)));
    }
    for (Account a : accountList) accountService.addANewAccount(a);
    */
    // 15900ab1-e426-4cff-bce3-0bea2de5a99b Customer
    // d98287d3-b921-427f-a4b2-6c98b716d6a9 Employee
    // 3fa85f64-5717-4562-b3fc-2c963f66afa6 cucumberUserEmployee
    // 3fa85f64-5717-4562-b3fc-2c963f66afa7 cucumberUserCustomer

    // Users
    User customer =
        new User(
            UUID.fromString("15900ab1-e426-4cff-bce3-0bea2de5a99b"),
            "Hein",
            "Eken",
            "31685032148",
            "customer",
            "customer",
            new ArrayList<>(),
            io.swagger.security.Role.ROLE_CUSTOMER,
            AccountStatus.ACTIVE);
    User employee =
        new User(
            UUID.fromString("d98287d3-b921-427f-a4b2-6c98b716d6a9"),
            "Amst",
            "Erdam",
            "31685032149",
            "employee",
            "employee",
            new ArrayList<>(),
            io.swagger.security.Role.ROLE_EMPLOYEE,
            AccountStatus.ACTIVE);

    // User for Cucumber
    User cucumberUserEmployee =
        new User(
            UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6"),
            "Cuc",
            "Umber",
            "31685222149",
            "testCucumber",
            "testCucumber",
            new ArrayList<>(),
            io.swagger.security.Role.ROLE_EMPLOYEE,
            AccountStatus.ACTIVE);
    User cucumberUserCustomer =
        new User(
            UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa7"),
            "Cuc2",
            "Umber2",
            "31685222150",
            "testCucumber2",
            "testCucumber2",
            new ArrayList<>(),
            Role.ROLE_CUSTOMER,
            AccountStatus.ACTIVE);

    // 192be6e7-5f4a-4424-bc1c-1514a4b35b5d
    // 22b1905d-8d67-4f65-87e6-6da6279a645d
    // 08ca7061-039c-4ca0-b458-6852c248e492
    // 5ae9d83c-2938-4922-b6e6-7f70a5e625ab
    // b45a0866-c853-48bc-9ccc-f1cd68ac776b
    // 0dc37f91-b427-4f87-927d-32cc8375ead2
    // a81630e8-4fb1-4b7d-aa58-f8d87abb9b9f
    // bd0f9d60-2251-4613-b235-f3e6e8cf1122

    // Account(iban, accountType, accountStatus, balance);


    // Accounts for cucumberUser
    Account cucumberCurrentAcc = new Account(AccountType.CURRENT, 500D);
    Account cucumberSavingAcc = new Account(AccountType.SAVING, 500D);

    Account customerCurrentAcc = new Account(AccountType.CURRENT, 500D);
    Account customerSavingAcc = new Account(AccountType.SAVING, 500D);

    customer.getAccounts().add(customerCurrentAcc);
    customer.getAccounts().add(customerSavingAcc);

    cucumberUserCustomer.getAccounts().add(cucumberCurrentAcc);
    cucumberUserCustomer.getAccounts().add(cucumberSavingAcc);

    accountService.addANewAccount(customerCurrentAcc);
    accountService.addANewAccount(customerSavingAcc);

    accountService.addANewAccount(cucumberSavingAcc);
    accountService.addANewAccount(cucumberCurrentAcc);

    userService.register(customer);
    userService.register(employee);
    userService.register(cucumberUserEmployee);
    userService.register(cucumberUserCustomer);

    RegularTransaction transactionForCucumber =
        new RegularTransaction(
            "NL01INHO0000000004", "NL01INHO0000000005", 20.00, cucumberUserCustomer.getId());
    RegularTransaction transactionForCucumber2 =
        new RegularTransaction(
            "NL01INHO0000000004", "NL01INHO0000000005", 15.0, cucumberUserCustomer.getId());
    UUID transactionId =
        new UUID(
            new BigInteger("cae6a16b6e424a8badbdde9a636d229f".substring(0, 16), 16).longValue(),
            new BigInteger("cae6a16b6e424a8badbdde9a636d229f".substring(16), 16).longValue());
    transactionForCucumber.setTransactionId(
        UUID.fromString("748e68b9-812a-4312-88b7-0020fc08405f"));
    transactionForCucumber2.setTransactionId(transactionId);

    try {
      transactionService.createTransaction(transactionForCucumber, cucumberUserCustomer);
      transactionService.createTransaction(transactionForCucumber2, cucumberUserCustomer);
    } catch (Exception e) {
      e.printStackTrace();
    }

    log.info("Testing transaction");
    testTransaction();

    log.info("The application has started successfully.");
  }

  private void testTransaction() {
    User testUser1 =
        new User(
            "Test",
            "User1",
            "whocareslmao",
            "test",
            "test",
            new ArrayList<>(),
            io.swagger.security.Role.ROLE_EMPLOYEE,
            AccountStatus.ACTIVE);

    Account testAccount1 = new Account("NL03INHO0000009000", AccountType.CURRENT, 100d);
    testUser1.getAccounts().add(testAccount1);

    User testUser2 =
        new User(
            "Test",
            "User2",
            "whocareslmao",
            "test",
            "test",
            new ArrayList<>(),
            io.swagger.security.Role.ROLE_EMPLOYEE,
            AccountStatus.ACTIVE);
    Account testAccount2 = new Account("NL03INHO0000009001", AccountType.CURRENT, 100d);
    testUser2.getAccounts().add(testAccount2);

    RegularTransaction testTransaction =
        new RegularTransaction(
            "NL03INHO0000009000", "NL03INHO0000009001", 20.00, testUser2.getId());

    try {
      accountService.addANewAccount(testAccount1);
      accountService.addANewAccount(testAccount2);
      userService.createUser(testUser1);
      userService.createUser(testUser2);
      transactionService.createTransaction(testTransaction, testUser2);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
