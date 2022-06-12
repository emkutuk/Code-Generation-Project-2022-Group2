package io.swagger.service;

import io.swagger.model.*;
import io.swagger.repo.AccountRepo;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
@ExtendWith(MockitoExtension.class)
class AccountServiceTest
{
    @InjectMocks
    private AccountService accountService;

    @Mock
    private AccountRepo accountRepo;

    private Account account;
    private Account account2;
    private Account bankAccount;

    @BeforeEach
    void setup()
    {
        account = new Account("testIban", AccountType.SAVING, 500D);
        account2 = new Account("testIban2", AccountType.CURRENT, 250D);
        bankAccount = new Account("NL01INHO0000000001", AccountType.CURRENT,0D);
    }


    @Test
    void getAccountByIbanReturnsNullIfIbanDoesNotExist()
    {
        when(accountRepo.findByIban("IbanThatDoesNotExist")).thenReturn(null);
        assertNull(accountService.getAccountByIban("IbanThatDoesNotExist"));
    }

    @Test
    void getAccountByIbanReturnsAccountWhenExists()
    {
        when(accountRepo.findByIban(account.getIban())).thenReturn(account);
        assertEquals(accountService.getAccountByIban("testIban"), account);
    }

    @Test
    void changeAccountStatusHappyFlow() throws Exception
    {
        when(accountRepo.findByIban(account.getIban())).thenReturn(account);
        accountService.changeAccountStatus("testIban", "disabled");
        assertEquals(account.getAccountStatus(), AccountStatus.DISABLED);
    }

    @Test
    void substractHappyFlow() throws Exception
    {
        when(accountRepo.findByIban(account.getIban())).thenReturn(account);
        assertTrue(accountService.subtractBalance("testIban", 200D));
    }

    @Test
    void substractTheAmountMoreThanBalance() throws Exception
    {
        when(accountRepo.findByIban(account.getIban())).thenReturn(account);
        Exception exception = assertThrows(Exception.class, () -> accountService.subtractBalance("testIban", 600D));
        Assertions.assertEquals("Balance needs to be more than the amount.", exception.getMessage());
    }

    @Test
    void substractMinusAmount() throws Exception
    {
        when(accountRepo.findByIban(account.getIban())).thenReturn(account);
        Exception exception = assertThrows(Exception.class, () -> accountService.subtractBalance("testIban", -100D));
        Assertions.assertEquals("Amount needs to be more than 0.", exception.getMessage());
    }

    @Test
    void substractionLeavesLessThanAbstractLimit() throws Exception
    {
        account.setAbsoluteLimit(100D);
        when(accountRepo.findByIban(account.getIban())).thenReturn(account);
        Exception exception = assertThrows(Exception.class, () -> accountService.subtractBalance("testIban", 500D));
        Assertions.assertEquals("Absolute limit has been reached.", exception.getMessage());
    }

    @Test
    void addAMinusBalance()
    {
        when(accountRepo.findByIban(account.getIban())).thenReturn(account);
        Exception exception = assertThrows(Exception.class, () -> accountService.addBalance("testIban", -100D));
        Assertions.assertEquals("Amount needs to be more than 0.", exception.getMessage());
    }

    @Test
    void getAccountBalanceByIbanHappyFlow() throws Exception
    {
        when(accountRepo.findByIban(account.getIban())).thenReturn(account);
        Assertions.assertEquals(accountService.getAccountBalanceByIban("testIban"), account.getBalance());
    }

    @Test
    void getAccountBalanceByIbanWhenAccountDoesNotExist() throws Exception
    {
        when(accountRepo.findByIban(account.getIban())).thenReturn(null);
        Exception exception = assertThrows(Exception.class, () -> accountService.getAccountBalanceByIban("testIban"));

        Assertions.assertEquals("Account does not exist.", exception.getMessage());
    }

    @Test
    void changeAccountTypeHappyFlow() throws Exception
    {
        when(accountRepo.findByIban(account.getIban())).thenReturn(account);
        accountService.changeAccountType(account.getIban(), "current");
        Assertions.assertEquals(account.getAccountType(), AccountType.CURRENT);
    }

    @Test
    void changeAccountTypeWithBankAccount() throws Exception
    {
        when(accountRepo.findByIban("NL01INHO0000000001")).thenReturn(bankAccount);
        Exception exception = assertThrows(Exception.class, ()
                -> accountService.changeAccountType("NL01INHO0000000001", "saving"));

        Assertions.assertEquals("This account type can not be changed!", exception.getMessage());
    }

    @Test
    void getAllAccounts() throws Exception
    {
        List <Account> accounts = new ArrayList<>();
        accounts.add(account);
        when(accountRepo.findAll()).thenReturn(accounts);

        Assertions.assertEquals(accountService.getAllAccounts(), accounts);
    }

    @Test
    void updateBankAccountByIban()
    {
        when(accountRepo.findByIban("NL01INHO0000000001")).thenReturn(bankAccount);

        Exception exception = assertThrows(Exception.class, ()
                -> accountService.updateAccountByIban("NL01INHO0000000001", account));

        Assertions.assertEquals("This account can not be updated!", exception.getMessage());
    }

    @Test
    void updateBankAccountGivesErrorWhenAccountIsNotFound()
    {
        when(accountRepo.findByIban("testIbanNotFound")).thenReturn(null);

        Exception exception = assertThrows(Exception.class, ()
                -> accountService.updateAccountByIban("testIbanNotFound", account));

        Assertions.assertEquals("The account doesn't exist!", exception.getMessage());
    }

    @Test
    void updateBankAccountHappyFlow() throws Exception
    {
        when(accountRepo.findByIban(account.getIban())).thenReturn(account);
        accountService.updateAccountByIban(account.getIban(), account2);


    }

}