package io.swagger.IT.steps;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.swagger.model.*;
import io.swagger.security.JwtTokenProvider;
import io.swagger.service.UserService;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

public class StepDefinitionsTransaction
{

    private String token;

    private RestTemplate template;
    private HttpHeaders headers;
    private HttpEntity<String> entity;
    private ResponseEntity<String> responseEntity;
    private ObjectMapper mapper;
    private String loginUrl = "http://localhost:8080/api/Login";
    private String transactionsUrl = "http://localhost:8080/api/Transactions";
    private UUID customerId;
    private UUID transactionID;

    public StepDefinitionsTransaction() throws URISyntaxException {
    }

    @Before
    public void setup() throws URISyntaxException, JsonProcessingException
    {
        template = new RestTemplate();
        headers = new HttpHeaders();
        mapper = new ObjectMapper();
        mapper.findAndRegisterModules();

        customerId = new UUID(new BigInteger("3fa85f6457174562b3fc2c963f66afa7".substring(0, 16), 16).longValue(), new BigInteger("3fa85f6457174562b3fc2c963f66afa7".substring(16), 16).longValue());
        transactionID = new UUID(new BigInteger("cae6a16b6e424a8badbdde9a636d229f".substring(0, 16), 16).longValue(), new BigInteger("cae6a16b6e424a8badbdde9a636d229f".substring(16), 16).longValue());
    }


    public void Login(String email, String password) throws Exception
    {
        //Login user
        ObjectMapper mapper = new ObjectMapper();
        LoginDto loginDto = new LoginDto(email, password);

        URI userUri = new URI(loginUrl);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(mapper.writeValueAsString(loginDto), headers);

        responseEntity = template.postForEntity(userUri, entity, String.class);
        token = responseEntity.getBody();
    }

    @Given("I am A Customer")
    public void iAmACustomer() throws Exception
    {
        Login("testCucumber2", "testCucumber2");
    }

    @Given("I am another employee")
    public void iAmAnEmployee() throws Exception
    {
        Login("testCucumber", "testCucumber");
    }

    @When("I want to review a customer's transaction with id {string}")
    public void iWantToReviewACustomersTransaction(String id) throws URISyntaxException
    {
        URI uri = new URI(transactionsUrl + "/" + id);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        entity = new HttpEntity<>(headers);
        responseEntity = template.exchange(uri, HttpMethod.GET, entity, String.class);
    }

    @When("I want to return all my transactions with offset {string} and max {string}")
    public void iWantToReturnAllMyTransactionsWithOffsetAndMax(String offset, String max) throws URISyntaxException
    {
        String parameters = "?max="+max+"&offset="+offset+"0";
        URI uri = new URI(transactionsUrl + parameters);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        entity = new HttpEntity<>(headers);
        responseEntity = template.exchange(uri, HttpMethod.GET, entity, String.class);
    }

    @Then("Return HTTPStatus {int}")
    public void returnHTTPStatus(int statusCode)
    {
        Assert.assertEquals(statusCode, responseEntity.getStatusCodeValue());
    }


    @When("I want to transfer {string} Euro from my one account that has iban {string} to my another account that has iban {string}")
    public void iWantToTransferEuroFromMyOneAccountThatHasIbanToMyAnotherAccountThatHasIban(String amount, String accountFromIban, String accountToIban) throws URISyntaxException, JsonProcessingException, ParseException
    {
        URI uri = new URI(transactionsUrl);

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        RegularTransaction transaction = new RegularTransaction(accountToIban, accountFromIban, Double.parseDouble(amount), customerId);
        HttpEntity<String> entity = new HttpEntity<>(mapper.writeValueAsString(transaction), headers);

        responseEntity = template.postForEntity(uri, entity, String.class);
    }

    @When("I want to deposit {string} Euros into my account")
    public void iWantToDepositEurosIntoMyAccount(String amount) throws URISyntaxException, JsonProcessingException {
        String requestBody = "{\n" + "  \"accountTo\": \"NL01INHO0000000055\"," +
                "\n" + " \"amount\": 15.0,\n" + " \"performedBy\": " + "\"3fa85f64-5717-4562-b3fc-2c963f66afa7\", \n" + " " +
                "\"transactionDate\": \"2021-12-01 16:02:06\", \n" + " " +
                "\"transactionId\": \"3fa85f64-5717-4562-b3fc-2c963f66afa6\"\n"+"}";

        URI uri = new URI(transactionsUrl + "/Deposit");
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        entity = new HttpEntity<>(requestBody, headers);
        responseEntity = template.exchange(uri, HttpMethod.POST, entity, String.class);
    }

    @When("I want to withdraw {string} Euros from my account with iban {string} and user id {string}")
    public void iWantToWithdrawEurosFromMyAccount(String amount, String accountFrom, String userId ) throws URISyntaxException {
        String requestBody = "{\n" + "  \"accountFrom\": \"NL01INHO0000000055\"," +
                "\n" + " \"amount\": 15.0,\n" + " \"performedBy\": " + "\"3fa85f64-5717-4562-b3fc-2c963f66afa7\", \n" + " " +
                "\"transactionDate\": \"2021-12-01 16:02:06\", \n" + " " +
                "\"transactionId\": \"3fa85f64-5717-4562-b3fc-2c963f66afa5\"\n"+"}";

        URI uri = new URI(transactionsUrl + "/Withdraw");
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        entity = new HttpEntity<>(requestBody, headers);
        responseEntity = template.exchange(uri, HttpMethod.POST, entity, String.class);
    }
    


    @When("I want to return my transaction with id {string}")
    public void iWantToReturnMyTransactionWithId(String transactionId) throws URISyntaxException {
        URI uri = new URI(transactionsUrl + "/cae6a16b6e424a8badbdde9a636d229f");
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        entity = new HttpEntity<>(headers);
        responseEntity = template.exchange(uri, HttpMethod.GET, entity, String.class);
    }

    @When("I want to transfer {string} Euro from my account that has {string} to their account that has {string}")
    public void iWantToTransferEuroFromMyAccountThatHasToTheirAccountThatHas(String amount, String accountFromIban, String accountToIban) throws URISyntaxException, JsonProcessingException {
        URI uri = new URI(transactionsUrl);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        RegularTransaction transaction = new RegularTransaction("NL01INHO0000000054", "NL01INHO0000000055", 20.0, customerId);
        HttpEntity<String> entity = new HttpEntity<>(mapper.writeValueAsString(transaction), headers);
        responseEntity = template.exchange(uri, HttpMethod.POST, entity, String.class);
    }

    @When("I want to transfer {string} Euros from current account {string} to savings account {string} on behalf of a customer")
    public void iWantToTransferEurosFromCurrentAccountToSavingsAccountOnBehalfOfACustomer(String arg0, String arg1, String arg2) throws URISyntaxException, JsonProcessingException {
        URI uri = new URI(transactionsUrl);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        RegularTransaction transaction = new RegularTransaction("NL01INHO0000000054", "NL01INHO0000000055", 15.0, customerId);
        HttpEntity<String> entity = new HttpEntity<>(mapper.writeValueAsString(transaction), headers);
        responseEntity = template.exchange(uri, HttpMethod.POST, entity, String.class);
    }

    @When("I want to transfer {string} Euros from current account with iban {string} to another customers account with iban {string} on behalf of a customer")
    public void iWantToTransferEurosFromCurrentAccountWithIbanToAnotherCustomersAccountWithIbanOnBehalfOfACustomer(String amount, String accountToIban, String accountFromIban) throws JsonProcessingException, URISyntaxException {
        URI uri = new URI(transactionsUrl);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        RegularTransaction transaction = new RegularTransaction("NL01INHO0000000052", "NL01INHO0000000055", 25.0, customerId);
        HttpEntity<String> entity = new HttpEntity<>(mapper.writeValueAsString(transaction), headers);
        responseEntity = template.exchange(uri, HttpMethod.POST, entity, String.class);
    }
}
