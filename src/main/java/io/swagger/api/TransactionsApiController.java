package io.swagger.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.model.*;
import io.swagger.security.JwtTokenProvider;
import io.swagger.service.TransactionService;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.UUID;

@javax.annotation.Generated(
    value = "io.swagger.codegen.v3.generators.java.SpringCodegen",
    date = "2021-06-06T11:20:30.422Z[GMT]")
@RestController
public class TransactionsApiController implements TransactionsApi {

  private static final Logger log = LoggerFactory.getLogger(TransactionsApiController.class);

  private final ObjectMapper objectMapper;

  private final HttpServletRequest request;

  @Autowired TransactionService transactionService;
  @Autowired UserService userService;
  @Autowired JwtTokenProvider tokenProvider;

  @org.springframework.beans.factory.annotation.Autowired
  public TransactionsApiController(ObjectMapper objectMapper, HttpServletRequest request) {
    this.objectMapper = objectMapper;
    this.request = request;
  }

  @PreAuthorize("hasRole('EMPLOYEE') OR hasRole('CUSTOMER')")
  public ResponseEntity createTransaction(
      @Parameter(in = ParameterIn.DEFAULT, description = "", required = true, schema = @Schema())
          @Valid
          @RequestBody
          RegularTransaction transaction) {

    try {
      User user = getUserFromToken();
      log.info("should be creating transaction");
      return new ResponseEntity<>(
          transactionService.createTransaction(transaction, user), HttpStatus.CREATED);
    } catch (Exception e) {
      e.printStackTrace();
      // Handle exceptions
      return new ResponseEntity<>(e.toString(),HttpStatus.BAD_REQUEST);
    }
  }

  @PreAuthorize("hasRole('EMPLOYEE') OR hasRole('CUSTOMER')")
  public ResponseEntity<Void> deleteTransactionById(
      @Parameter(in = ParameterIn.PATH, description = "", required = true, schema = @Schema())
          @PathVariable("id")
          String id) {
    try {
      System.out.println(id);
      transactionService.deleteTransactionById(id);
      return new ResponseEntity<Void>(HttpStatus.ACCEPTED);
    } catch (Exception e) {
      return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
    }
  }

  @PreAuthorize("hasRole('EMPLOYEE') OR hasRole('CUSTOMER')")
  public ResponseEntity<Deposit> depositMoney(
      @Parameter(in = ParameterIn.DEFAULT, description = "", required = true, schema = @Schema())
          @Valid
          @RequestBody
          Deposit deposit) {

    try {
      log.info("Trying to save deposit");
      return new ResponseEntity<Deposit>(
          transactionService.depositMoney(deposit), HttpStatus.CREATED);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
  }

  @PreAuthorize("hasRole('EMPLOYEE') OR hasRole('CUSTOMER')")
  public ResponseEntity<List<Transaction>> getTransactionByIBAN(
      @Size(max = 34)
          @Parameter(
              in = ParameterIn.PATH,
              description = "The account to perform the action on.",
              required = true,
              schema = @Schema())
          @PathVariable("iban")
          String iban,
      @Min(10)
          @Max(50)
          @Parameter(
              in = ParameterIn.QUERY,
              description = "The maximum number of items to return.",
              schema =
                  @Schema(
                      allowableValues = {},
                      minimum = "10",
                      maximum = "50",
                      defaultValue = "10"))
          @Valid
          @RequestParam(value = "max", required = false, defaultValue = "10")
          Integer max,
      @Min(0)
          @Parameter(
              in = ParameterIn.QUERY,
              description =
                  "The number of items to skip before starting to collect the result set.",
              schema = @Schema(allowableValues = {}))
          @Valid
          @RequestParam(value = "offset", required = false)
          Integer offset) {
    try {
      return new ResponseEntity<List<Transaction>>(
          transactionService.getTransactionsByIban(iban, max, offset), HttpStatus.OK);
    } catch (Exception e) {
      e.printStackTrace();
      log.error("Couldn't serialize response for content type application/json", e);
      return new ResponseEntity<List<Transaction>>(HttpStatus.BAD_REQUEST);
    }
  }

  @PreAuthorize("hasRole('EMPLOYEE') OR hasRole('CUSTOMER')")
  public ResponseEntity<Transaction> getTransactionById(
      @Parameter(in = ParameterIn.PATH, description = "", required = true, schema = @Schema())
          @PathVariable("id")
          String id) {
    try {
      return new ResponseEntity<Transaction>(
          transactionService.getTransactionById(id), HttpStatus.OK);
    } catch (Exception e) {
      log.error("Couldn't serialize response for content type application/json", e);
      return new ResponseEntity<Transaction>(HttpStatus.BAD_REQUEST);
    }
  }

  @PreAuthorize("hasRole('EMPLOYEE') OR hasRole('CUSTOMER')")
  public ResponseEntity<List<Transaction>> getTransactionsByUser(
      @Min(10)
          @Max(50)
          @Parameter(
              in = ParameterIn.QUERY,
              description = "The maximum number of items to return.",
              schema =
                  @Schema(
                      allowableValues = {},
                      minimum = "10",
                      maximum = "50",
                      defaultValue = "10"))
          @Valid
          @RequestParam(value = "max", required = false, defaultValue = "10")
          Integer max,
      @Min(0)
          @Parameter(
              in = ParameterIn.QUERY,
              description =
                  "The number of items to skip before starting to collect the result set.",
              schema = @Schema(allowableValues = {}))
          @Valid
          @RequestParam(value = "offset", required = false, defaultValue = "1")
          Integer offset)
      throws Exception {
    String token = tokenProvider.resolveToken(request);
    String email = tokenProvider.getUsername(token);

    User loggedInUser = userService.getUserByEmail(email);
    UUID userid = loggedInUser.getId();
    System.out.println(userid);
    try {
      return new ResponseEntity<List<Transaction>>(
          transactionService.getTransactionsByUserId(userid, max, offset), HttpStatus.OK);
    } catch (Exception e) {
      log.error("Couldn't serialize response for content type application/json", e);
      return new ResponseEntity<List<Transaction>>(HttpStatus.FORBIDDEN);
    }
  }

  @PreAuthorize("hasRole('EMPLOYEE') OR hasRole('CUSTOMER')")
  public ResponseEntity<Withdrawal> withdrawMoney(
      @Parameter(in = ParameterIn.DEFAULT, description = "", required = true, schema = @Schema())
          @Valid
          @RequestBody
          Withdrawal withdrawal) {

    try {
      log.info("Trying to save withdrawal");
      return new ResponseEntity<Withdrawal>(
          transactionService.withdrawMoney(withdrawal, getUserFromToken()), HttpStatus.OK);
    } catch (Exception e) {
      log.error("Couldn't serialize response for content type application/json", e);
      return new ResponseEntity<Withdrawal>(HttpStatus.BAD_REQUEST);
    }
  }

  private User getUserFromToken() throws Exception {
    String token = tokenProvider.resolveToken(request);
    String email = tokenProvider.getUsername(token);
    return userService.getUserByEmail(email);
  }
}
