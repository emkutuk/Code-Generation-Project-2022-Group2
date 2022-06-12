package io.swagger.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class AccountTest
{
    private Account account;
    private List<Transaction> transactions;
    private User user;

    private Account accountConstructor1;
    private Account accountConstructor2;
    private Account accountConstructor3;
    private Account accountConstructor4;

    @BeforeEach
    public void Setup() throws Exception
    {
        user = new User("Hein", "Eken", "31685032148", "customer", "customer", new ArrayList<>(), io.swagger.security.Role.ROLE_CUSTOMER, AccountStatus.ACTIVE);
        account = new Account("testIban", AccountType.SAVING, 400.0);
        transactions = new ArrayList<>();
        transactions.add(new RegularTransaction("NL03INHO0000009000", "NL03INHO0000009001", 20.00, user.getId()));

        accountConstructor1 = new Account();
        accountConstructor2 = new Account(AccountType.CURRENT, 0D);
        accountConstructor3 = new Account("testIban", AccountType.CURRENT, 0D);
        accountConstructor4 = new Account(AccountType.CURRENT);
    }

    @Test
    public void getIban()
    {
        assertEquals("testIban", account.getIban());
    }

    @Test
    public void setIban()
    {
        account.setIban("newIban");
        assertEquals("newIban", account.getIban());
    }

    @Test
    public void getAccountType()
    {
        assertEquals(AccountType.SAVING, account.getAccountType());
    }

    @Test
    public void setAccountType()
    {
        account.setAccountType(AccountType.CURRENT);
        assertEquals(AccountType.CURRENT, account.getAccountType());
    }

    @Test
    public void getBalance()
    {
        assertTrue(account.getBalance() == 400.0);
    }

    @Test
    public void setBalance()
    {
        account.setBalance((double) 0);
        assertTrue(account.getBalance() == 0);
    }

    @Test
    void getTransactions()
    {
        assertEquals(account.getTransactions().stream().count(), 0);
    }

    @Test
    void setTransactions()
    {
        account.setTransactions(transactions);
        assertEquals(transactions, account.getTransactions());
    }

    @Test
    void getAbsoluteLimit()
    {
        Double expectedLimit = 0D;
        assertEquals(expectedLimit, account.getAbsoluteLimit());
    }

    @Test
    void setAbsoluteLimit()
    {
        Double expectedLimit = 5000D;
        account.setAbsoluteLimit(expectedLimit);
        assertEquals(expectedLimit, account.getAbsoluteLimit());
    }

    @Test
    void getAccountStatus()
    {
        assertEquals(AccountStatus.ACTIVE, account.getAccountStatus());
    }

    @Test
    void setAccountStatus()
    {
        account.setAccountStatus(AccountStatus.DISABLED);
        assertEquals(AccountStatus.DISABLED, account.getAccountStatus());
    }

    @Test
    void equals()
    {
        assertTrue(account.equals(account));
    }

    @Test
    void equalsReturnsNullWhenObjectIsNull()
    {
        assertFalse(account.equals(null));
    }

    @Test
    void emptyConstructor()
    {
        Account actualAccount = new Account();
        assertEquals(actualAccount, accountConstructor1);
    }

    @Test
    void constructorWith2ParametersGiven() throws Exception
    {
        Account actualAccount = new Account(AccountType.CURRENT, 0D);
        assertEquals(actualAccount, accountConstructor2);
    }

    @Test
    void constructorWith3ParametersGiven() throws Exception
    {
        Account actualAccount = new Account("testIban", AccountType.CURRENT, 0D);
        assertEquals(actualAccount, accountConstructor3);
    }

    @Test
    void constructorWith1ParameterGiven() throws Exception
    {
        Account actualAccount = new Account(AccountType.CURRENT);
        assertEquals(actualAccount, accountConstructor4);
    }

    @Test
    void testHashCode() throws Exception
    {
        int actualHashCode = Objects.hash(account.getIban(), account.getAccountType(), account.getAccountStatus(), account.getBalance());
        assertEquals(actualHashCode, account.hashCode());
    }

    @Test
    void testToString()
    {
        assertEquals(account.toString(), "class Account {\n" + "    iban: testIban\n" + "    accountType: saving\n" + "    accountStatus: active\n" + "    balance: 400.0\n" + "}");
    }

    @Test
    void toIndentedStringWhenNullGiven()
    {
        account.setIban(null);
        assertEquals(account.toString(), "class Account {\n" + "    iban: null\n" + "    accountType: saving\n" + "    accountStatus: active\n" + "    balance: 400.0\n" + "}");
    }

    @Test
    void accountStatusWhenStringGiven()
    {
        assertEquals(AccountStatus.fromValue("active"), AccountStatus.ACTIVE);
    }

    @Test
    void accountTypeWhenStringGiven()
    {
        assertEquals(AccountType.fromValue("current"), AccountType.CURRENT);
    }

    @Test
    void accountStatusWhenWrongStringGiven()
    {
        assertNull(AccountStatus.fromValue("blah"));
    }

    @Test
    void accountTypeWhenWrongStringGiven()
    {
        assertNull(AccountType.fromValue("blah"));
    }
}