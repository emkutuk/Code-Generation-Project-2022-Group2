package io.swagger.api;

import io.swagger.model.Account;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.model.AccountDto;
import io.swagger.model.User;
import io.swagger.security.JwtTokenProvider;
import io.swagger.security.Role;
import io.swagger.service.AccountService;
import io.swagger.service.UserService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.*;
import javax.validation.Valid;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2021-06-10T11:32:06.118Z[GMT]")
@RestController
public class AccountsApiController implements AccountsApi
{

    private static final Logger log = LoggerFactory.getLogger(AccountsApiController.class);
    private final ObjectMapper objectMapper;
    private final HttpServletRequest request;

    @Autowired
    AccountService accountService;

    @Autowired
    UserService userService;

    @Autowired
    JwtTokenProvider tokenProvider;

    @org.springframework.beans.factory.annotation.Autowired
    public AccountsApiController(ObjectMapper objectMapper, HttpServletRequest request)
    {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<Account> addANewAccount(@Parameter(in = ParameterIn.DEFAULT, description = "", required = true, schema = @Schema()) @Valid @RequestBody Account account) throws Exception
    {
        Account addedAcc = accountService.addANewAccount(account);
        return new ResponseEntity<Account>(HttpStatus.CREATED).status(201).body(addedAcc);
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<Account> changeAccountType(@Size(max = 34) @Parameter(in = ParameterIn.PATH, description = "The account to perform the action on.", required = true, schema = @Schema()) @PathVariable("iban") String iban, @Parameter(in = ParameterIn.PATH, description = "The new type for the account to be changed into.", required = true, schema = @Schema(allowableValues = {"saving", "current"})) @PathVariable("type") String type) throws Exception
    {
        if (!accountService.ibanExists(iban)) return new ResponseEntity<Account>(HttpStatus.NOT_FOUND);
        if (accountService.isBanksAccount(iban)) return new ResponseEntity<Account>(HttpStatus.UNAUTHORIZED);

        Account updatedAcc = accountService.changeAccountType(iban, type);
        return new ResponseEntity<Account>(HttpStatus.OK).status(200).body(updatedAcc);
    }

    @PreAuthorize("hasRole('EMPLOYEE') OR hasRole('CUSTOMER')")
    public ResponseEntity<Void> changeAccountStatus(@Size(max = 34) @Parameter(in = ParameterIn.PATH, description = "The account to perform the action on.", required = true, schema = @Schema()) @PathVariable("iban") String iban, @Parameter(in = ParameterIn.PATH, description = "Account status to be changed to.", required = true, schema = @Schema(allowableValues = {"active", "closed"})) @PathVariable("status") String status) throws Exception
    {
        if (!accountService.ibanExists(iban)) return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        if (accountService.isBanksAccount(iban)) return new ResponseEntity<Void>(HttpStatus.UNAUTHORIZED);

        User user = userService.getUserFromRequest(request);
        Role role = user.getRole();

        //If the user is a customer, check if that iban belongs to him/her
        if (role.equals(Role.ROLE_CUSTOMER))
        {
            if (accountService.accountBelongsToUser(user, iban))
            {
                accountService.changeAccountStatus(iban, status);
            } else
                //If the account is not his/hers return unauthorized
                return new ResponseEntity<Void>(HttpStatus.UNAUTHORIZED);
        }
        //If the user is employee then chage the account's status
        else
        {
            accountService.changeAccountStatus(iban, status);
        }
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @PreAuthorize("hasRole('EMPLOYEE') OR hasRole('CUSTOMER')")
    public ResponseEntity<Double> getAccountBalanceByIban(@Size(max = 34) @Parameter(in = ParameterIn.PATH, description = "The account to perform the action on.", required = true, schema = @Schema()) @PathVariable("iban") String iban) throws Exception
    {
        if (!accountService.ibanExists(iban)) return new ResponseEntity<Double>(HttpStatus.NOT_FOUND);
        if (accountService.isBanksAccount(iban)) return new ResponseEntity<Double>(HttpStatus.UNAUTHORIZED);

        User user = userService.getUserFromRequest(request);
        Role role = user.getRole();

        Double balance = null;
        //If the user is a customer, check if that iban belongs to him/her
        if (role.equals(Role.ROLE_CUSTOMER))
        {
            if (accountService.accountBelongsToUser(user, iban))
            {
                balance = accountService.getAccountBalanceByIban(iban);
            } else
                //If the account is not his/hers return unauthorized
                return new ResponseEntity<Double>(HttpStatus.UNAUTHORIZED);
        }
        //If the user is employee then return the balance no matter what
        else
        {
            balance = accountService.getAccountBalanceByIban(iban);
        }
        return new ResponseEntity<Double>(HttpStatus.OK).status(200).body(balance);
    }


    @PreAuthorize("hasRole('EMPLOYEE') OR hasRole('CUSTOMER')")
    public ResponseEntity<Account> getAccountByIban(@Size(max = 34) @Parameter(in = ParameterIn.PATH, description = "The account to perform the action on.", required = true, schema = @Schema()) @PathVariable("iban") String iban) throws Exception
    {
        if (!accountService.ibanExists(iban)) return new ResponseEntity<Account>(HttpStatus.NOT_FOUND);
        if (accountService.isBanksAccount(iban)) return new ResponseEntity<Account>(HttpStatus.UNAUTHORIZED);

        User user = userService.getUserFromRequest(request);
        Role role = user.getRole();

        //If its a customer, check if the account belongs to him/her
        if (role.equals(Role.ROLE_CUSTOMER))
        {
            if (accountService.accountBelongsToUser(user, iban))
            {
                Account account = accountService.getAccountByIban(iban);
                return new ResponseEntity<Account>(HttpStatus.OK).status(200).body(account);
            } else
                //If the account is not his/hers return unauthorized
                return new ResponseEntity<Account>(HttpStatus.UNAUTHORIZED);
        }
        //If its an employee, then return the account no matter what
        else
        {
            Account account = accountService.getAccountByIban(iban);
            return new ResponseEntity<Account>(HttpStatus.OK).status(200).body(account);
        }
    }


    @PreAuthorize("hasRole('EMPLOYEE') OR hasRole('CUSTOMER')")
    public ResponseEntity<List<Account>> getAllAccounts(@Min(0) @Parameter(in = ParameterIn.QUERY, description = "The number of items to skip before starting to collect the result set.", schema = @Schema(allowableValues = {})) @Valid @RequestParam(value = "offset", required = false) Integer offset, @Min(10) @Max(50) @Parameter(in = ParameterIn.QUERY, description = "The maximum number of items to return.", schema = @Schema(allowableValues = {}, minimum = "10", maximum = "50", defaultValue = "10")) @Valid @RequestParam(value = "max", required = false, defaultValue = "10") Integer max) throws Exception
    {
        List<Account> accountsList = new ArrayList<Account>();
        List<Account> allAccounts = new ArrayList<Account>();

        User user = userService.getUserFromRequest(request);
        Role role = user.getRole();

        if (role == Role.ROLE_EMPLOYEE) allAccounts = accountService.getAllAccounts();
        else if (role == Role.ROLE_CUSTOMER) allAccounts = user.getAccounts();

        long maxValue = max + offset;
        //If the maxValue is bigger then existing accounts, max value is equal to account count
        if (maxValue > allAccounts.size()) maxValue = allAccounts.size();

        for (int i = offset; i < maxValue; i++)
            accountsList.add(allAccounts.get(i));

        return new ResponseEntity<List<Account>>(HttpStatus.OK).status(200).body(accountsList);
    }

    @PreAuthorize("hasRole('EMPLOYEE') OR hasRole('CUSTOMER')")
    public ResponseEntity<Account> updateAccount(@Size(max = 34) @Parameter(in = ParameterIn.DEFAULT, description = "", required = true, schema = @Schema()) @Valid @RequestBody AccountDto account) throws Exception
    {
        String iban = account.getIban();
        if (!accountService.ibanExists(iban)) return new ResponseEntity<Account>(HttpStatus.NOT_FOUND);
        if (accountService.isBanksAccount(iban)) return new ResponseEntity<Account>(HttpStatus.UNAUTHORIZED);

        User user = userService.getUserFromRequest(request);
        Role role = user.getRole();

        //If its a customer, check if the account belongs to him/her
        if (role.equals(Role.ROLE_CUSTOMER))
        {
            if (accountService.accountBelongsToUser(user, iban))
            {
                Account acc = accountService.updateAccount(account);
                return new ResponseEntity<Account>(HttpStatus.OK).status(200).body(acc);
            } else
                //If the account is not his/hers return unauthorized
                return new ResponseEntity<Account>(HttpStatus.UNAUTHORIZED);
        }
        //If its an employee, then update the account no matter what
        else
        {
            Account acc = accountService.updateAccount(account);
            return new ResponseEntity<Account>(HttpStatus.OK).status(200).body(acc);
        }
    }
}


