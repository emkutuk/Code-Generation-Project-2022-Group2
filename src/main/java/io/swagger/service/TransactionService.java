package io.swagger.service;

import io.swagger.model.*;
import io.swagger.repo.DepositRepo;
import io.swagger.repo.RegularTransactionRepo;
import io.swagger.repo.TransactionRepo;
import io.swagger.repo.WithdrawalRepo;
import io.swagger.security.Role;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.beans.Transient;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Log
@NoArgsConstructor
public class TransactionService {

  private final double dailyTransactionLimit = 1000;

  private TransactionRepo transactionRepo;
  private RegularTransactionRepo regularTransactionRepo;
  private DepositRepo depositRepo;
  private WithdrawalRepo withdrawalRepo;
  private AccountService accountService;
  private UserService userService;

  @Autowired
  public TransactionService(
      TransactionRepo repo,
      RegularTransactionRepo regularTransactionRepo,
      DepositRepo depositRepo,
      WithdrawalRepo withdrawalRepo,
      AccountService accountService,
      UserService userService) {
    this.transactionRepo = repo;
    this.regularTransactionRepo = regularTransactionRepo;
    this.depositRepo = depositRepo;
    this.withdrawalRepo = withdrawalRepo;
    this.accountService = accountService;
    this.userService = userService;
  }

  // omar
  public List<Transaction> getTransactions() {
    // validate user
    return transactionRepo.findAll();
  }

  // omar
  public Transaction getTransactionById(String id) throws Exception {
    // validate user
    String s = id.replace("-", "");
    UUID uuid =
        new UUID(
            new BigInteger(s.substring(0, 16), 16).longValue(),
            new BigInteger(s.substring(16), 16).longValue());
    Optional<?> transaction = transactionRepo.findById(uuid);
    if (transaction.isPresent()) {
      return (Transaction) transaction.get();
    } else {
      throw new Exception("Transaction not found");
    }
  }

  // omar
  public List<Transaction> getTransactionsByUserId(UUID id, Integer max, Integer offset)
      throws Exception {
    User user = userService.getUserById(id);
    try {
      List<Transaction> allTransactions = new ArrayList<Transaction>();
      List<Transaction> filteredList = new ArrayList<>();
      List<Transaction> accountTransactions;
      List<Account> userAccounts = user.getAccounts();

      for (Account a : userAccounts) {
        accountTransactions = getAllTransactionsForAccountByIban(a.getIban());
        if (!accountTransactions.isEmpty()) {
          allTransactions.addAll(accountTransactions);
        }
      }
      long maxValue = max + offset;
      // If the maxValue is bigger then existing transactions, max value is equal to allTransactions
      // count
      if (maxValue > allTransactions.stream().count()) maxValue = allTransactions.stream().count();

      for (int i = offset; i < maxValue; i++) filteredList.add(allTransactions.get(i));

      return filteredList;
    } catch (Exception e) {
      throw new Exception("Invalid User ID or no transactions found");
    }
  }

  // omar
  public List<Transaction> getTransactionsByIban(String Iban, Integer max, Integer offset)
      throws Exception {
    // validate user

    List<Transaction> allAccountTransactions = getAllTransactionsForAccountByIban(Iban);
    ArrayList<Transaction> filteredList = new ArrayList<Transaction>();
    try {
      for (int i = offset; i < allAccountTransactions.size(); i++) {
        filteredList.add(allAccountTransactions.get(i));
        if (filteredList.size() == max) {
          break;
        }
      }
    } catch (Exception e) {
      throw new Exception("Transaction not found");
    }
    return filteredList;
  }

  // omar
  @Transient
  public void deleteTransactionById(String id) throws Exception {
    // validate user
    try {
      // Converting String to UUID (Using UUID.toString(id) causes an illegal argument exception
      String s = id.replace("-", "");
      UUID uuid =
          new UUID(
              new BigInteger(s.substring(0, 16), 16).longValue(),
              new BigInteger(s.substring(16), 16).longValue());
      transactionRepo.deleteById(uuid);
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("Something went wrong");
      throw new Exception("Unable to delete transaction");
    }
  }

  public RegularTransaction createTransaction(RegularTransaction transaction, User user)
      throws Exception {
    validateTransactionDate(transaction);
    Account accountFrom = accountService.getAccountByIban(transaction.getAccountFrom());
    User userFrom = userService.getUserByIban(accountFrom);
    Account accountTo = accountService.getAccountByIban(transaction.getAccountTo());

    if (userFrom == null || accountFrom == null || accountTo == null) {
      log.info("User or account doesn't exist");
      throw new IllegalArgumentException("User or account doesn't exist");
    }

    // If user is a customer and does not own the account from which the money is leaving
    if (!userFrom.getId().equals(user.getId()) && user.getRole().equals(Role.ROLE_CUSTOMER)) {
      log.info("User trying to make transaction from another users account");
      throw new IllegalArgumentException("user is not authorized to do this");
    }

    // If accountTo is savings and not owned by user of accountFrom
    if (!(userFrom.getAccounts().contains(accountTo))
        && accountTo.getAccountType().equals(AccountType.SAVING)) {
      log.info("User trying to make transaction to savings account of another user");
      throw new IllegalArgumentException("Cannot transfer to savings of another user");
    }

    // If Transaction would be above daily transaction limit
    double dailyTransactionAmount = calculateDailyTransactionAmount(accountFrom);

    if (dailyTransactionAmount + transaction.getAmount() > dailyTransactionLimit) {
      log.info("Transaction exceeds daily limit");
      throw new IllegalArgumentException("Transaction amount exceeds daily limit");
    }

    // If Transaction exceeds user transaction limit
    if (transaction.getAmount() > userFrom.getTransactionLimit()) {
      log.info("Transaction exceeds user transaction limit");
      throw new IllegalArgumentException("Transaction amount exceeds user limit");
    }

    log.info("Checked role and account owner");

    return performRegularTransaction(transaction);
  }

  @Transactional
  protected double calculateDailyTransactionAmount(Account accountFrom) {
    double dailyTransactions = 500.0;

    Double transactions =
        regularTransactionRepo.findRegularTransactionByAccountFrom(accountFrom.getIban());
    Double deposits = depositRepo.findDepositByAccountTo(accountFrom.getIban());
    Double withdrawals = withdrawalRepo.findWithdrawalByAccountFrom(accountFrom.getIban());

    dailyTransactions = transactions != null ? dailyTransactions + transactions : dailyTransactions;
    dailyTransactions = deposits != null ? dailyTransactions + deposits : dailyTransactions;
    dailyTransactions = withdrawals != null ? dailyTransactions + withdrawals : dailyTransactions;

    log.warning("Daily Transaction " + dailyTransactions);

    return dailyTransactions;
  }

  @Transactional
  public List<Transaction> getAllTransactionsForAccountByIban(String iban) {
    return new ArrayList<>() {
      {
        addAll(regularTransactionRepo.findAllByAccountFrom(iban));
        addAll(depositRepo.findAllByAccountTo(iban));
        addAll(withdrawalRepo.findAllByAccountFrom(iban));
      }
    };
  }

  public Deposit depositMoney(Deposit deposit) throws Exception {
    return performDeposit(deposit);
  }

  public Withdrawal withdrawMoney(Withdrawal withdrawal, User user) throws Exception {

    Account account = accountService.getAccountByIban(withdrawal.getAccountFrom());
    User accountOwner = userService.getUserByIban(account);

    if (user.getRole().equals(Role.ROLE_EMPLOYEE) || accountOwner.getId() == user.getId()) {
      return performWithdrawal(withdrawal);
    } else {
      throw new IllegalArgumentException("User is not authorized to do this");
    }
  }

  @Transactional
  protected Deposit performDeposit(Deposit deposit) throws Exception {
    // Assuming valid user
    // Assuming validation done in account service
    // Assuming deposit is a valid deposit
    accountService.addBalance(deposit.getAccountTo(), deposit.getAmount());
    deposit.setTransactionId(UUID.randomUUID());
    return transactionRepo.save(deposit);
  }

  @Transactional
  protected Withdrawal performWithdrawal(Withdrawal withdrawal) throws Exception {
    // Assuming valid user
    // Assuming validation done in account
    // Assuming withdrawal is a valid withdrawal
    accountService.subtractBalance(withdrawal.getAccountFrom(), withdrawal.getAmount());
    withdrawal.setTransactionId(UUID.randomUUID());
    return transactionRepo.save(withdrawal);
  }

  @Transactional
  protected RegularTransaction performRegularTransaction(RegularTransaction transaction)
      throws Exception {
    // Assuming valid user
    // Assuming validation done in account
    // Assuming transaction is a valid transaction

    log.info("Performing transaction");
    accountService.subtractBalance(transaction.getAccountFrom(), transaction.getAmount());
    accountService.addBalance(transaction.getAccountTo(), transaction.getAmount());
    transaction.setTransactionId(UUID.randomUUID());
    return transactionRepo.save(transaction);
  }

  private void validateTransactionDate(Transaction transaction) throws IllegalArgumentException {
    if (transaction.getTransactionDate() == null) {
      transaction.setTransactionDate(LocalDateTime.now());
    } else if (LocalDateTime.now().minusMinutes(3).isAfter(transaction.getTransactionDate())) {
      // Transaction is too old
      log.info("Transaction is too old");
      throw new IllegalArgumentException(
          String.format("Date %s is too old to be valid", transaction.getTransactionDate()));
    }
  }
}
