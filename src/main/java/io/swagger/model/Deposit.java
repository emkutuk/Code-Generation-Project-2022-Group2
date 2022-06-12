package io.swagger.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/** Deposit */
@Validated
@javax.annotation.Generated(
    value = "io.swagger.codegen.v3.generators.java.SpringCodegen",
    date = "2021-06-06T11:20:30.422Z[GMT]")
@Entity
@NoArgsConstructor
@Schema(allOf = Transaction.class)
public class Deposit extends Transaction {
  @JsonProperty("accountTo")
  private String accountTo = null;

  public Deposit(String accountTo, Double amount, UUID performedBy) {
    super(amount, performedBy, LocalDateTime.now());
    this.accountTo = accountTo;
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
  public String getAccountTo() {
    return accountTo;
  }

  public void setAccountTo(String accountTo) {
    this.accountTo = accountTo;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Deposit deposit = (Deposit) o;
    return Objects.equals(this.accountTo, deposit.accountTo) && super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(accountTo, super.hashCode());
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Deposit {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    accountTo: ").append(toIndentedString(accountTo)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
