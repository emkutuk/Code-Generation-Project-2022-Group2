package io.swagger.service;

import io.swagger.model.Account;
import io.swagger.model.AccountStatus;
import io.swagger.model.AccountType;
import io.swagger.model.User;
import io.swagger.repo.UserRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.constraints.Null;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
@ExtendWith(MockitoExtension.class)
class UserServiceTest
{
    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepo userRepo;

    private User user;
    private User user2;
    private Account account;

    @BeforeEach
    void setup()
    {
        account = new Account("testIban", AccountType.SAVING, 500D);
        user = new User("Hein", "Eken", "31685032148", "customer", "customer", new ArrayList<>(), io.swagger.security.Role.ROLE_CUSTOMER, AccountStatus.ACTIVE);
        user2 = new User("Amst", "Erdam", "31685032149", "employee", "employee", new ArrayList<>(), io.swagger.security.Role.ROLE_EMPLOYEE, AccountStatus.ACTIVE);
    }

    @Test
    void getAllUsers() throws Exception
    {
        List<User> allUsers = new ArrayList<>();
        allUsers.add(user);
        allUsers.add(user2);

        when(userRepo.findAll()).thenReturn(allUsers);

        Assertions.assertEquals(userService.getAllUsers(), allUsers);
    }

    @Test
    void getAllUsersButTheWorldIsAgainstYou() throws Exception
    {
        when(userRepo.findAll()).thenReturn(null);

        Exception exception = assertThrows(Exception.class, () -> userService.getAllUsers());
        Assertions.assertEquals("Users cannot be null!", exception.getMessage());
    }

    @Test
    void getUserById() throws Exception
    {
        UUID userId = UUID.randomUUID();
        when(userRepo.getOne(userId)).thenReturn(user);

        Assertions.assertEquals(userService.getUserById(userId), user);
    }

    @Test
    void getUserByWrongId() throws Exception
    {
        UUID userId = UUID.randomUUID();
        when(userRepo.getOne(userId)).thenReturn(user);

        Exception exception = assertThrows(Exception.class, () -> userService.getUserById(UUID.randomUUID()));
        Assertions.assertNotNull(exception);
    }

    @Test
    void getUserByEmail() throws Exception
    {
        when(userRepo.findUserByEmail(user.getEmail())).thenReturn(user);
        Assertions.assertEquals(userService.getUserByEmail(user.getEmail()), user);
    }

    @Test
    void getUserByEmailThatDoesNotExist()
    {
        when(userRepo.findUserByEmail(user.getEmail())).thenReturn(user);
        Exception exception = assertThrows(Exception.class, () -> userService.getUserByEmail("invalidMail"));
        Assertions.assertNotNull(exception);
    }

    @Test
    void deleteUser()
    {
        UUID userId = UUID.randomUUID();
        when(userRepo.getOne(userId)).thenReturn(user);

        userService.deleteUserById(userId);
    }

    @Test
    void getUserByIbanHappyFlow() throws Exception
    {
        when(userRepo.findUserByAccountsContaining(account)).thenReturn(user);

        assertEquals(userService.getUserByIban(account), user);
    }

    @Test
    void getUserByWrongIban()
    {
        when(userRepo.findUserByAccountsContaining(account)).thenReturn(user);

        Exception exception = assertThrows(Exception.class, () -> userService.getUserByIban(new Account()));
        Assertions.assertNotNull(exception);
    }

    @Test
    void createUserWithException()
    {
        when(userRepo.save(user)).thenThrow(new NullPointerException());

        Exception exception = assertThrows(Exception.class, () -> userService.createUser(user));
        Assertions.assertNotNull(exception);
    }

    @Test
    void updateUser()
    {
        UUID userId = UUID.randomUUID();
        user.setId(userId);
        when(userRepo.getOne(userId)).thenReturn(user);

        Assertions.assertNotEquals(userService.updateUser(user), user);
    }
}