package io.swagger.service;

import io.swagger.model.*;
import io.swagger.repo.AccountRepo;
import io.swagger.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static java.lang.Integer.parseInt;

@Service
public class AccountService
{

    private final AccountRepo accountRepo;
    private final UserService userService;
    private final IbanGeneratorService ibanGeneratorService;

    public AccountService(AccountRepo accountRepo, UserService userService, IbanGeneratorService ibanGeneratorService)
    {
        this.accountRepo = accountRepo;
        this.userService = userService;
        this.ibanGeneratorService = ibanGeneratorService;
    }


    public Account addANewAccount(Account account) throws Exception
    {
        try
        {
            if (Objects.isNull(account.getIban())) account.setIban(ibanGeneratorService.generateIban());
            accountRepo.save(account);
            return account;
        } catch (Exception e)
        {
            throw new Exception(e.getMessage());
        }
    }

    public List<Account> getAllAccounts() throws Exception
    {
        try
        {
            return accountRepo.findAll();

        } catch (Exception e)
        {
            throw new Exception(e.getMessage());
        }
    }

    //iban control is here due to this method called by Transaction Service
    public Account getAccountByIban(String iban)
    {
        if (ibanExists(iban))
        {
            Account account = accountRepo.findByIban(iban);
            return account;
        } else return null;
    }

    //Check iban
    public void changeAccountStatus(String iban, String status) throws Exception
    {
        try
        {
            Account account = getAccountByIban(iban);
            AccountStatus newStatus = AccountStatus.valueOf(status.toUpperCase(Locale.ROOT));
            account.setAccountStatus(newStatus);
        } catch (Exception e)
        {
            throw new Exception(e.getMessage());
        }
    }

    public Account updateAccount(AccountDto account) throws Exception
    {
        try
        {
            Account acc = applyAccChanges(account);
            accountRepo.save(acc);
            return acc;
        } catch (Exception e)
        {
            throw new Exception(e.getMessage());
        }
    }

    private Account applyAccChanges(AccountDto modifiedAccount)
    {
        Account accFromDb = getAccountByIban(modifiedAccount.getIban());
        accFromDb.setAccountStatus(modifiedAccount.getAccountStatus());
        accFromDb.setAccountType(modifiedAccount.getAccountType());
        accFromDb.setAbsoluteLimit(modifiedAccount.getAbsoluteLimit());
        return accFromDb;
    }

    //Check iban
    public Account changeAccountType(String iban, String typeEnum) throws Exception
    {
        Account acc = accountRepo.findByIban(iban);
        try
        {
            AccountType newType = AccountType.valueOf(typeEnum.toUpperCase(Locale.ROOT));
            acc.setAccountType(newType);
            accountRepo.save(acc);
            return acc;
        } catch (Exception e)
        {
            throw new Exception(e.getMessage());
        }
    }

    //Check iban
    public Double getAccountBalanceByIban(String iban) throws Exception
    {
        try
        {
            Account account = accountRepo.findByIban(iban);
            return account.getBalance();
        } catch (Exception e)
        {
            throw new Exception(e.getMessage());
        }
    }

    public boolean addBalance(String iban, double amount) throws Exception
    {
        try
        {
            if (ibanExists(iban) && amountIsValid(amount))
            {
                performAdding(iban, amount);
                return true;
            } else return false;
        } catch (Exception e)
        {
            return false;
        }
    }

    private void performAdding(String iban, double amount)
    {
        Account account = getAccountByIban(iban);
        double balance = account.getBalance();
        account.setBalance(balance + amount);
    }

    public boolean subtractBalance(String iban, double amount) throws Exception
    {
        try
        {
            if (ibanExists(iban) && accountIsEligibleForSubtraction(iban, amount))
            {
                performSubtraction(iban, amount);
                return true;
            } else return false;
        } catch (Exception e)
        {
            return false;
        }
    }

    private void performSubtraction(String iban, double amount)
    {
        Account account = getAccountByIban(iban);
        double balance = account.getBalance();
        account.setBalance(balance - amount);
    }

    private boolean accountIsEligibleForSubtraction(String iban, double amount)
    {
        //I get balance here so that, i dont have to get the same balance for 2 different methods below, thus less calls to db
        double balance = getBalanceByIban(iban);

        return amountIsValid(amount) && hasEnoughAbsoluteLimit(iban, amount, balance) && hasEnoughBalance(balance, amount);
    }


    private boolean hasEnoughAbsoluteLimit(String iban, double amount, double balance)
    {
        double absoluteLimit = getAbsoluteLimitByIban(iban);
        return (balance - amount) <= absoluteLimit;
    }

    private double getAbsoluteLimitByIban(String iban)
    {
        Account account = getAccountByIban(iban);
        return account.getAbsoluteLimit();
    }

    private boolean hasEnoughBalance(double balance, double amount)
    {
        return balance >= amount;
    }

    private boolean amountIsValid(double amount)
    {
        return amount > 0;
    }


    private double getBalanceByIban(String iban)
    {
        Account account = getAccountByIban(iban);
        return account.getBalance();
    }

    public boolean isBanksAccount(String iban)
    {
        return iban.equals(IbanGeneratorService.BANKSIBAN);
    }

    public boolean ibanExists(String iban)
    {
        return !Objects.isNull(accountRepo.findByIban(iban));
    }

    public boolean accountBelongsToUser(User user, String iban) throws Exception
    {
        for (Account a : user.getAccounts())
        {
            if (a.getIban().equals(iban))
            {
                return true;
            }
        }
        return false;
    }
}
