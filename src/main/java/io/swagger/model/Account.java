package io.swagger.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.validation.annotation.Validated;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Account
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2021-06-10T11:32:06.118Z[GMT]")

@Entity
public class Account
{
    @Id
    @JsonProperty("iban")
    private String iban = null;

    @JsonProperty("accountType")
    private AccountType accountType = AccountType.CURRENT;

    @JsonProperty("accountStatus")
    private AccountStatus accountStatus = AccountStatus.ACTIVE;

    @JsonProperty("balance")
    private Double balance = 0d;

    @JsonProperty("absoluteLimit")
    private Double absoluteLimit = 550d;

    //@JsonProperty("transactions")
    @NotFound(action = NotFoundAction.IGNORE)
    @OneToMany
    private List<Transaction> transactions = null;

    public List<Transaction> getTransactions()
    {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions)
    {
        this.transactions = transactions;
    }

    /*public Account iban(String iban)
    {
        this.iban = iban;
        return this;
    }*/

    /**
     * IBAN number of the account.
     *
     * @return iban
     **/
    @Schema(example = "NL03INHO0033576852", required = true, description = "IBAN number of the account.")
    @NotNull

    @Size(max = 34)
    public String getIban()
    {
        return iban;
    }

    public void setIban(String iban)
    {
        this.iban = iban;
    }

    public Double getAbsoluteLimit()
    {
        return absoluteLimit;
    }

    public void setAbsoluteLimit(Double absoluteLimit)
    {
        this.absoluteLimit = absoluteLimit;
    }

    /*public Account accountType(AccountType accountType)
    {
        this.accountType = accountType;
        return this;
    }*/

    /**
     * The type of the account. This can be either saving or current.
     *
     * @return accountType
     **/
    @Schema(required = true, description = "The type of the account. This can be either saving or current.")
    @NotNull

    public AccountType getAccountType()
    {
        return accountType;
    }

    public void setAccountType(AccountType accountType)
    {
        this.accountType = accountType;
    }

    /*public Account accountStatus(AccountStatus accountStatus)
    {
        this.accountStatus = accountStatus;
        return this;
    }*/

    /**
     * The status of the account. This can be either active or closed.
     *
     * @return accountStatus
     **/
    @Schema(required = true, description = "The status of the account. This can be either active or closed.")
    @NotNull

    public AccountStatus getAccountStatus()
    {
        return accountStatus;
    }

    public void setAccountStatus(AccountStatus accountStatus)
    {
        this.accountStatus = accountStatus;
    }

    /*public Account balance(Double balance)
    {
        this.balance = balance;
        return this;
    }*/

    /**
     * Get balance
     * minimum: 0
     *
     * @return balance
     **/
    @Schema(required = true, description = "")
    @NotNull

    @DecimalMin("0")
    public Double getBalance()
    {
        return balance;
    }

    public void setBalance(Double balance)
    {
        this.balance = balance;
    }


    @Override
    public boolean equals(java.lang.Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }
        Account account = (Account) o;
        return Objects.equals(this.iban, account.iban) && Objects.equals(this.accountType, account.accountType) && Objects.equals(this.accountStatus, account.accountStatus) && Objects.equals(this.balance, account.balance);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(iban, accountType, accountStatus, balance);
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("class Account {\n");

        sb.append("    iban: ").append(toIndentedString(iban)).append("\n");
        sb.append("    accountType: ").append(toIndentedString(accountType)).append("\n");
        sb.append("    accountStatus: ").append(toIndentedString(accountStatus)).append("\n");
        sb.append("    balance: ").append(toIndentedString(balance)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(java.lang.Object o)
    {
        if (o == null)
        {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }

    //Its here to prevent no default constructor error
    public Account()
    {

    }

    public Account(AccountType accountType) throws Exception
    {
        this.iban = "";
        this.accountType = accountType;
        this.balance = 0.0;
        this.transactions = new ArrayList<>();
    }

    //These constructors are for testing
    public Account(AccountType accountType, Double balance) throws Exception
    {
        this.iban = "";
        this.accountType = accountType;
        this.balance = balance;
        this.transactions = new ArrayList<>();
    }


    public Account(String iban, AccountType accountType, Double balance)
    {
        this.iban = iban;
        this.accountType = accountType;
        this.balance = balance;
        this.transactions = new ArrayList<>();
    }

}
