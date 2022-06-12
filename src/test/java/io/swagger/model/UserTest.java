package io.swagger.model;

import io.swagger.security.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserTest
{
    private User user;
    private User userConstructor;
    private List<Account> accounts;

    @BeforeEach
    public void Setup() {
        accounts = new ArrayList<>();
        accounts.add(new Account("testIban", AccountType.CURRENT, 500D));
        user = new User("firstNameTest", "lastNameTest", "3100000000", "emailTest", "passwordTest", accounts, Role.ROLE_CUSTOMER, AccountStatus.ACTIVE);
        userConstructor = new User();
    }

    @Test
    void getFirstName()
    {
        assertEquals(user.getFirstName(), "firstNameTest");
    }

    @Test
    void setFirstName()
    {
        user.setFirstName("firstNameTestChanged");
        assertEquals(user.getFirstName(), "firstNameTestChanged");
    }

    @Test
    void getLastName()
    {
        assertEquals(user.getLastName(), "lastNameTest");
    }

    @Test
    void setLastName()
    {
        user.setLastName("lastNameTestChanged");
        assertEquals(user.getLastName(), "lastNameTestChanged");
    }

    @Test
    void getPhoneNumber()
    {
        assertEquals(user.getPhoneNumber(), "3100000000");
    }

    @Test
    void setPhoneNumber()
    {
        user.setPhoneNumber("3100000001");
        assertEquals(user.getPhoneNumber(), "3100000001");
    }

    @Test
    void getEmail()
    {
        assertEquals(user.getEmail(), "emailTest");
    }

    @Test
    void setEmail()
    {
        user.setEmail("emailTestChanged");
        assertEquals(user.getEmail(), "emailTestChanged");
    }

    @Test
    void getAccounts()
    {
        assertEquals(user.getAccounts(), accounts);
    }

    @Test
    void setAccounts()
    {
        accounts.add(new Account("testIban2", AccountType.SAVING, 0D));
        user.setAccounts(accounts);
        assertEquals(user.getAccounts(), accounts);
    }

    @Test
    void getRole()
    {
        assertEquals(user.getRole(), Role.ROLE_CUSTOMER);
    }

    @Test
    void setRole()
    {
        user.setRole(Role.ROLE_EMPLOYEE);
        assertEquals(user.getRole(), Role.ROLE_EMPLOYEE);
    }

    @Test
    void getAccountStatus()
    {
        assertEquals(user.getAccountStatus(), AccountStatus.ACTIVE);
    }

    @Test
    void setAccountStatus()
    {
        user.setAccountStatus(AccountStatus.DISABLED);
        assertEquals(user.getAccountStatus(), AccountStatus.DISABLED);
    }

    @Test
    void getPassword()
    {
        assertEquals(user.getPassword(), "passwordTest");
    }

    @Test
    void setPassword()
    {
        user.setPassword("newPasswordTest");
        assertEquals("newPasswordTest", user.getPassword());
    }

    @Test
    void constructorWithNoParameters()
    {
        User actualUser = new User();
        assertEquals(userConstructor, actualUser);
    }
}