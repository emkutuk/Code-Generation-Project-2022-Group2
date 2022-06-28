package io.swagger.service;

import io.swagger.model.Account;
import io.swagger.model.AccountStatus;
import io.swagger.model.User;
import io.swagger.repo.UserRepo;
import io.swagger.security.JwtTokenProvider;
import io.swagger.security.Role;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.Transient;
import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.util.List;
import java.util.UUID;

@Log
@Service
public class UserService
{

    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserRepo userRepo;

    public String login(String email, String password)
    {
        try
        {
            manager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
            return jwtTokenProvider.createToken(email, userRepo.findUserByEmail(email).getRole());
        } catch (AuthenticationException ae)
        {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Invalid credentials");
        }
    }

    public String register(User user) throws Exception
    {
        if (userRepo.findUserByEmail(user.getEmail()) == null)
        {
            try
            {
                user.setPassword(passwordEncoder().encode(user.getPassword()));
                if (user.getRole() == null) user.setRole(Role.ROLE_CUSTOMER);
                user.setId(UUID.randomUUID());
                userRepo.save(user);
                String token = jwtTokenProvider.createToken(user.getEmail(), user.getRole());
                return token;
            } catch (Exception e)
            {
                throw new Exception(e.getMessage());
            }
        } else
        {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Email address is already in use.");
        }
    }

    public User getUserById(UUID id) throws Exception
    {
        try
        {
            return userRepo.getOne(id);
        } catch (Exception e)
        {
            throw new Exception(e.getMessage());
        }
    }

    public List<User> getAllUsers() throws Exception
    {
        if (userRepo.findAll() != null) return userRepo.findAll();
        else throw new NullPointerException("Users cannot be null!");
    }

    public User getUserByIban(Account account) throws Exception
    {
        try
        {
            return userRepo.findUserByAccountsContaining(account);
        } catch (Exception e)
        {
            throw new Exception(e.getMessage());
        }
    }

    public User getUserByEmail(String email) throws Exception
    {
        try
        {
            return userRepo.findUserByEmail(email);
        } catch (Exception e)
        {
            throw new Exception(e.getMessage());
        }
    }

    public User createUser(User user) throws Exception
    {
        try
        {

            return userRepo.save(user);
        } catch (Exception e)
        {
            throw new Exception(e.getMessage());
        }
    }

    // Sets account to inactive
    @Transient
    public User deleteUserById(UUID id)
    {
        User userToDelete = userRepo.getOne(id);
        if (userToDelete == null) return null;
        else
        {
            userToDelete.setAccountStatus(AccountStatus.DISABLED);
            return userRepo.save(userToDelete);
        }
    }

    @Transient
    public User updateUser(User user)
    {
        User userToUpdate = userRepo.getOne(user.getId());

        userToUpdate.setPassword(user.getPassword());
        userToUpdate.setAccounts(user.getAccounts());
        userToUpdate.setEmail(user.getEmail());
        userToUpdate.setRole(user.getRole());
        userToUpdate.setAccountStatus(user.getAccountStatus());
        userToUpdate.setPhoneNumber(user.getPhoneNumber());
        userToUpdate.setLastName(user.getLastName());
        userToUpdate.setFirstName(user.getFirstName());

        return userRepo.save(userToUpdate);
    }

    public UUID convertID(String id) throws Exception
    {
        UUID userID;
        try{
            String s = id.replace("-", "");
            userID = new UUID(
                    new BigInteger(s.substring(0, 16), 16).longValue(),
                    new BigInteger(s.substring(16), 16).longValue());
        }
        catch (Exception e){
            throw new Exception("Invalid uuid");
        }
        return userID;
    }

    @Bean
    public PasswordEncoder passwordEncoder() throws Exception
    {
        try
        {
            return new BCryptPasswordEncoder();
        } catch (Exception e)
        {
            throw new Exception(e.getMessage());
        }
    }

    public User getUserFromRequest(HttpServletRequest request) throws Exception
    {
        String token = jwtTokenProvider.resolveToken(request);
        String email = jwtTokenProvider.getUsername(token);
        return getUserByEmail(email);
    }
}
