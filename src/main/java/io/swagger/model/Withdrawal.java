package io.swagger.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Withdrawal
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2021-06-06T11:20:30.422Z[GMT]")

@Entity
@NoArgsConstructor
@Schema(allOf = Transaction.class)
public class Withdrawal extends Transaction {
  @JsonProperty("accountFrom")
  private String accountFrom = null;

  public Withdrawal(String accountFrom, Double amount, UUID performedBy) {
    super(amount, performedBy, LocalDateTime.now());
    this.accountFrom = accountFrom;
  }

  /**
   * IBAN of the account withdrawing the money.
   * @return accountFrom
   **/
  @Schema(example = "NL04INHO6818968668", required = true, description = "IBAN of the account withdrawing the money.")
      @NotNull

  @Size(max=34)   public String getAccountFrom() {
    return accountFrom;
  }

  public void setAccountFrom(String accountFrom) {
    this.accountFrom = accountFrom;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Withdrawal withdrawal = (Withdrawal) o;
    return Objects.equals(this.accountFrom, withdrawal.accountFrom) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(accountFrom, super.hashCode());
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Withdrawal {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    accountFrom: ").append(toIndentedString(accountFrom)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
