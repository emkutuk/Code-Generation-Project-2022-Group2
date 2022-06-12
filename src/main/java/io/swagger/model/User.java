package io.swagger.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.UUID;

/**
 * User
 */
@JsonIgnoreProperties(value = {"handler","hibernateLazyInitializer","fieldHandler"})
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2021-06-06T11:20:30.422Z[GMT]")
@Data
@Entity
public class User
{
    public User(String firstName, String lastName, String phoneNumber, String email, String password, List<Account> accounts, io.swagger.security.Role role, AccountStatus accountStatus)
    {
        this.id = UUID.randomUUID();
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.password = password;
        this.accounts = accounts;
        this.role = role;
        this.accountStatus = accountStatus;
    }

    public User()
    {

    }

    public User(UUID userID, String firstName, String lastName, String phoneNumber, String email, String password, List<Account> accounts, io.swagger.security.Role role, AccountStatus accountStatus)
    {
        this.id = userID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.password = password;
        this.accounts = accounts;
        this.role = role;
        this.accountStatus = accountStatus;
    }

    @JsonProperty("id")
    @Id
    private UUID id = null;

    @JsonProperty("firstName")
    private String firstName = null;

    @JsonProperty("lastName")
    private String lastName = null;

    @JsonProperty("phoneNumber")
    private String phoneNumber = null;

    @JsonProperty("email")
    private String email = null;

    @JsonProperty("transactionLimit")
    private int transactionLimit = 250;

    @JsonProperty("password")
    private String password = null;

    //@JsonProperty("accounts")
    @NotFound(action = NotFoundAction.IGNORE)
    @OneToMany
    private List<Account> accounts = null;

    @JsonProperty("role")
    private io.swagger.security.Role role = io.swagger.security.Role.ROLE_CUSTOMER;

    @JsonProperty("accountStatus")
    private AccountStatus accountStatus = AccountStatus.ACTIVE;

    /**
     * Get id
     *
     * @return id
     */
    @Schema(required = true, accessMode = Schema.AccessMode.READ_ONLY, description = "")
    @Valid
    public UUID getId()
    {
        return id;
    }

    /**
     * Get firstName
     *
     * @return firstName
     */
    @Schema(example = "Rob", required = true, description = "")
    @NotNull
    @Size(max = 100)
    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    /**
     * Get lastName
     *
     * @return lastName
     */
    @Schema(example = "Banks", required = true, description = "")
    @NotNull
    @Size(max = 100)
    public String getLastName()
    {
        return lastName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    /**
     * Get phoneNumber
     *
     * @return phoneNumber
     */
    @Schema(example = "321123527", required = true, description = "")
    @NotNull
    @Size(max = 20)
    public String getPhoneNumber()
    {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber)
    {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Get email
     *
     * @return email
     */
    @Schema(example = "rbanks@gmail.com", required = true, description = "")
    @NotNull
    @Size(max = 256)
    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    /**
     * Get password
     *
     * @return password
     */
    @Schema(example = "GetMyP@ss1", required = true, description = "")
    @NotNull
    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    /**
     * Get accounts
     *
     * @return accounts
     */
    @Schema(example = "[{\"id\":3,\"iban\":\"NL03INHO0033576852\",\"accountType\":\"current\",\"balance\":320},{\"id\":3,\"iban\":\"NL03INHO0033576883\",\"accountType\":\"savings\",\"balance\":800}]", accessMode = Schema.AccessMode.READ_ONLY, description = "")
    @Valid
    @Size(max = 2)
    public List<Account> getAccounts()
    {
        return accounts;
    }

    public void setAccounts(List<Account> accounts)
    {
        this.accounts = accounts;
    }

    /**
     * Get role
     *
     * @return role
     */
    @Schema(description = "")
    public io.swagger.security.Role getRole()
    {
        return role;
    }

    public void setRole(io.swagger.security.Role role)
    {
        this.role = role;
    }

    /**
     * Get accountStatus
     *
     * @return accountStatus
     */
    @Schema(description = "")
    public AccountStatus getAccountStatus()
    {
        return accountStatus;
    }

    public void setAccountStatus(AccountStatus accountStatus)
    {
        this.accountStatus = accountStatus;
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces (except the first line).
     */
    private String toIndentedString(java.lang.Object o)
    {
        if (o == null)
        {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}
