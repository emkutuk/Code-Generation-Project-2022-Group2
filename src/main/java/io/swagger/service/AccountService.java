package io.swagger.service;

import io.swagger.model.Account;
import io.swagger.model.AccountStatus;
import io.swagger.model.AccountType;
import io.swagger.repo.AccountRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

import static java.lang.Integer.parseInt;

@Service
public class AccountService
{
    @Autowired
    private AccountRepo accountRepo;

    public Account addANewAccount(Account account) throws Exception
    {
        try
        {
            if (account.getIban().equals("") || account.getIban() == null)
            {
                account.setIban(generateIban());
            }
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
            return (List<Account>) accountRepo.findAll();

        } catch (Exception e)
        {
            throw new Exception(e.getMessage());
        }
    }

    public Account getAccountByIban(String iban)
    {
        Account account = accountRepo.findByIban(iban);
        if (account != null) return account;
            //If there are no accounts, it returns null
        else return null;
    }

    // 0 Success
    // 1 Account not found
    // 2 Unauthorized operation
    public int changeAccountStatus(String iban, String status) throws Exception
    {
        Account account = getAccountByIban(iban);
        if (account == null) return 1;
        else if (isBankAccount(account)) return 2;
        else
        {
            try
            {
                account.setAccountStatus(AccountStatus.valueOf(status.toUpperCase(Locale.ROOT)));
                return 0;
            } catch (Exception e)
            {
                throw new Exception(e.getMessage());
            }
        }
    }

    public Account updateAccountByIban(String iban, Account account) throws Exception
    {
        Account acc = accountRepo.findByIban(iban);
        if (acc == null)
        {
            throw new Exception("The account doesn't exist!");

        } else if (isBankAccount(acc))
        {
            throw new Exception("This account can not be updated!");
        } else
        {
            try
            {
                acc.setAccountStatus(account.getAccountStatus());
                acc.setAccountType(account.getAccountType());
                acc.setAbsoluteLimit(account.getAbsoluteLimit());

                accountRepo.save(acc);
                return acc;
            } catch (Exception e)
            {
                throw new Exception(e.getMessage());
            }
        }
    }

    public Account changeAccountType(String iban, String typeEnum) throws Exception
    {
        Account acc = accountRepo.findByIban(iban);
        if (!isBankAccount(acc))
        {
            try
            {
                acc.setAccountType(AccountType.valueOf(typeEnum.toUpperCase(Locale.ROOT)));
                accountRepo.save(acc);
                return acc;
            } catch (Exception e)
            {
                throw new Exception(e.getMessage());
            }
        } else throw new Exception("This account type can not be changed!");
    }

    public Double getAccountBalanceByIban(String iban) throws Exception
    {
        Account acc = accountRepo.findByIban(iban);
        if (acc == null)
        {
            throw new Exception("Account does not exist.");
        } else
        {
            return acc.getBalance();
        }
    }

    public boolean addBalance(String iban, double amount) throws Exception
    {
        try
        {
            Account account = getAccountByIban(iban);
            double balance = account.getBalance();

            //Checks if amount is valid
            if (amount > 0)
            {
                account.setBalance(balance + amount);
                return true;
            } else
            {
                throw new Exception("Amount needs to be more than 0.");
            }
        } catch (Exception e)
        {
            throw new Exception(e.getMessage());
        }
    }

    public boolean subtractBalance(String iban, double amount) throws Exception
    {
        try
        {
            Account account = getAccountByIban(iban);
            double balance = account.getBalance();

            //Checks if amount is valid
            if (amount > 0)
            {
                //Checks if balance is sufficient
                if (balance >= amount)
                {
                    //Checks whether absolute limit is reached after the transaction
                    if ((balance - amount) <= account.getAbsoluteLimit())
                    {
                        account.setBalance(balance - amount);
                        return true;
                    } else throw new Exception("Absolute limit has been reached.");
                } else throw new Exception("Balance needs to be more than the amount.");
            } else throw new Exception("Amount needs to be more than 0.");
        } catch (Exception e)
        {
            throw new Exception(e.getMessage());
        }
    }

    private String generateIban()
    {
        //Format : NLxxINHO0xxxxxxxxx
        // 2 digits - 9 digits

        //Check what is the last iban in db
        List<Account> allAccounts = (List<Account>) accountRepo.findAll();
        String lastIban = allAccounts.get((int) allAccounts.stream().count() - 1).getIban();

        //First check the last 9 digits to see if they are not all 9
        String last9Digits = lastIban.substring(lastIban.length() - 9);
        String first2Digits = lastIban.substring(2, 4);

        if (last9Digits != "999999999")
        {
            last9Digits = String.format("%09d", (parseInt(last9Digits) + 1));
        }
        //If they are all 9es then first 2 digits needs to increase
        else
        {
            last9Digits = String.format("%09d", 0);
            first2Digits = String.format("%02d", (parseInt(first2Digits) + 1));
        }

        //Combine all iban together and return the value
        return "NL" + first2Digits + "INHO0" + last9Digits;
    }

    private boolean isBankAccount(Account account)
    {
        if (account.getIban() == "NL01INHO0000000001") return true;
        else return false;
    }
}
