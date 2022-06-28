package io.swagger.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.persistence.Id;

@Data
@Validated
public class AccountDto
{
    @Id
    @JsonProperty("iban")
    private String iban;

    @JsonProperty("accountType")
    private AccountType accountType;

    @JsonProperty("accountStatus")
    private AccountStatus accountStatus;

    @JsonProperty("absoluteLimit")
    private Double absoluteLimit;

    public AccountDto(String iban, AccountType type, AccountStatus status, Double absoluteLimit) {
        this.iban = iban;
        this.accountType = type;
        this.accountStatus = status;
        this.absoluteLimit = absoluteLimit;
    }

    public String getIban()
    {
        return iban;
    }

    public void setIban(String iban)
    {
        this.iban = iban;
    }

    public AccountType getAccountType()
    {
        return accountType;
    }

    public void setAccountType(AccountType accountType)
    {
        this.accountType = accountType;
    }

    public AccountStatus getAccountStatus()
    {
        return accountStatus;
    }

    public void setAccountStatus(AccountStatus accountStatus)
    {
        this.accountStatus = accountStatus;
    }


    public Double getAbsoluteLimit()
    {
        return absoluteLimit;
    }

    public void setAbsoluteLimit(Double absoluteLimit)
    {
        this.absoluteLimit = absoluteLimit;
    }
}
