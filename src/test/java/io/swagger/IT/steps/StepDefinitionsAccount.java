package io.swagger.IT.steps;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.swagger.model.*;
import org.junit.Assert;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;


public class StepDefinitionsAccount
{
    private String token;

    private RestTemplate template;
    private HttpHeaders headers;
    private HttpEntity<String> entity;
    private ResponseEntity<String> responseEntity;
    private ObjectMapper mapper;
    private String loginUrl = "http://localhost:8080/api/Login";
    private String accountUrl = "http://localhost:8080/api/Accounts";


    @Before
    public void setup()
    {
        template = new RestTemplate();
        headers = new HttpHeaders();
        mapper = new ObjectMapper();
    }

    public void Login(String email, String password) throws JsonProcessingException, URISyntaxException
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


    @Given("I am a customer")
    public void iAmACustomer() throws JsonProcessingException, URISyntaxException
    {
        Login("customer", "customer");
    }

    @Given("I am an employee")
    public void iAmAnEmployee() throws URISyntaxException, JsonProcessingException
    {
        Login("employee", "employee");
    }

    @When("I want to see all accounts")
    public void iWantToSeeAllAccounts() throws URISyntaxException
    {
        String parameters = "?max=10&offset=0";
        URI uri = new URI(accountUrl + parameters);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        entity = new HttpEntity<>(headers);
        responseEntity = template.exchange(uri, HttpMethod.GET, entity, String.class);
    }

    @Then("Display HTTPStatus {int}")
    public void displayHTTPStatus(int statusCode)
    {
        Assert.assertEquals(statusCode, responseEntity.getStatusCodeValue());
    }

    @When("I want to get the account with iban {string}")
    public void iWantToGetTheAccountWithIban(String iban) throws URISyntaxException
    {
        URI uri = new URI(accountUrl + "/" + iban);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        entity = new HttpEntity<>(headers);
        responseEntity = template.exchange(uri, HttpMethod.GET, entity, String.class);
    }

    @When("I want to create a new {string} account")
    public void iWantToCreateANewAccount(String accountType) throws Exception
    {
        URI uri = new URI(accountUrl);

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        Account accountToBeAdded = new Account(AccountType.fromValue(accountType));

        HttpEntity<String> entity = new HttpEntity<>(mapper.writeValueAsString(accountToBeAdded), headers);
        responseEntity = template.postForEntity(uri, entity, String.class);
    }


    @When("I want to change account with iban {string} type to {string}")
    public void iWantToChangeAccountWithIbanTypeTo(String iban, String accountType) throws URISyntaxException
    {
        URI uri = new URI(accountUrl + "/" + iban + "/" + accountType);

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        responseEntity = template.postForEntity(uri, entity, String.class);
    }

    @When("I want to change account with iban {string} status to {string}")
    public void iWantToChangeAccountWithIbanStatusTo(String iban, String accountStatus) throws URISyntaxException
    {
        URI uri = new URI(accountUrl + "/" + iban + "/Status/" + accountStatus);

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        responseEntity = template.postForEntity(uri, entity, String.class);
    }

    @When("I want to get account's balance with iban {string}")
    public void iWantToGetAccountSBalanceWithIban(String iban) throws URISyntaxException
    {
        URI uri = new URI(accountUrl + "/" + iban + "/Balance");
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        entity = new HttpEntity<>(headers);
        responseEntity = template.exchange(uri, HttpMethod.GET, entity, String.class);
    }

    @When("I want to update an account iban {string} with a new {string} account")
    public void iWantToUpdateAnAccountIbanWithANewAccount(String iban, String accountType) throws Exception
    {
        URI uri = new URI(accountUrl + "/" + iban);

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + token);

        Account newAccount = new Account(AccountType.fromValue(accountType));
        HttpEntity<String> entity = new HttpEntity<>(mapper.writeValueAsString(newAccount), headers);

        responseEntity = template.exchange(uri, HttpMethod.PUT, entity, String.class);
    }
}
