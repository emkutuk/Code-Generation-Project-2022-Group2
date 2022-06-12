package io.swagger.repo;

import io.swagger.model.Deposit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface DepositRepo extends JpaRepository<Deposit, UUID> {
  List<Deposit> findAllByAccountTo(String iban);

  @Query(value = "SELECT SUM(d.amount) FROM Deposit as d WHERE current_date = CAST(d.transactionDate as date) AND d.accountTo = :iban")
  Double findDepositByAccountTo(String iban);
}
