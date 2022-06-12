package io.swagger.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.validation.annotation.Validated;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.UUID;

/** RegularTransaction */
@Validated
@javax.annotation.Generated(
    value = "io.swagger.codegen.v3.generators.java.SpringCodegen",
    date = "2021-06-06T11:20:30.422Z[GMT]")
@Data
@Entity
@NoArgsConstructor
@Schema(allOf = Transaction.class)
public class RegularTransaction extends Transaction {
  public RegularTransaction(String accountTo, String accountFrom, double amount, UUID performedBy)
  {
    super(amount, performedBy, null);
    this.accountTo = accountTo;
    this.accountFrom = accountFrom;
  }


  public RegularTransaction(String accountTo, String accountFrom, double amount, UUID performedBy, LocalDateTime transactionDate)
  {
    super(amount, performedBy, transactionDate);
    this.accountTo = accountTo;
    this.accountFrom = accountFrom;
  }

  @JsonProperty("accountTo")
  @NonNull
  private String accountTo = null;

  @NotNull
  @JsonProperty("accountFrom")
  private String accountFrom = null;

  public void setAccountTo(String accountTo)
  {
    if(accountTo == null || accountTo.length() < 18 || accountTo.length() > 34){
      throw new IllegalArgumentException("Invalid Account format");
    }
    this.accountTo = accountTo;
  }

  public void setAccountFrom(String accountFrom) {
    if(accountFrom == null || accountFrom.length() < 18 || accountFrom.length() > 34 ){
      throw new IllegalArgumentException("Invalid Account format");
    }
    this.accountFrom = accountFrom;
  }

  /**
   * IBAN of the account receiving the money.
   *
   * @return accountTo
   */
  @Schema(
      example = "NL04INHO6818968668",
      required = true,
      description = "IBAN of the account receiving the money.")
  @NotNull
  @Size(max = 34)
  public String getAccountTo() {
    return accountTo;
  }

  /**
   * IBAN of the account sending the money.
   *
   * @return accountFrom
   */
  @Schema(
      example = "NL01INHO0000579848",
      required = true,
      description = "IBAN of the account sending the money.")
  @NotNull
  @Size(max = 34)
  public String getAccountFrom() {
    return accountFrom;
  }
}
