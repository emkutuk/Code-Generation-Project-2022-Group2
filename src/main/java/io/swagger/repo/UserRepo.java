package io.swagger.repo;

import io.swagger.model.Account;
import io.swagger.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepo extends JpaRepository<User, UUID> {
  User findUserByEmail(String Email);
  User findUserByAccountsContaining(Account account);
  }
