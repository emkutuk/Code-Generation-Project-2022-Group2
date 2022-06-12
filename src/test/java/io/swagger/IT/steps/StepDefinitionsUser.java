package io.swagger.IT.steps;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.swagger.model.*;
import io.swagger.security.Role;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

public class StepDefinitionsUser
{
    private String token;

    private RestTemplate template;
    private HttpHeaders headers;
    private HttpEntity<String> entity;
    private ResponseEntity<String> responseEntity;
    private ObjectMapper mapper;
    private String loginUrl = "http://localhost:8080/api/Login";
    private String userUrl = "http://localhost:8080/api/Users";

    @Before
    public void setup()
    {
        template = new RestTemplate();
        headers = new HttpHeaders();
        mapper = new ObjectMapper();
    }

    @Given("I am an Employee")
    public void iAmAnEmployee() throws URISyntaxException, JsonProcessingException
    {
        Login("employee", "employee");
    }

    @Then("Show HTTPStatus {int}")
    public void showHTTPStatus(int arg0) throws URISyntaxException, JsonProcessingException
    {
        Login("customer", "customer");
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

    @When("I want to create another user with Firstname {string}, Lastname {string}, Phone number {string}, Email {string}, Password {string} , Type {string}, Status {string}")
    public void iWantToCreateAnotherUserWithFirstnameLastnamePhoneNumberEmailPasswordTypeStatus(String firstName, String lastName, String phoneNumber, String email, String password, String userRole, String accountStatus) throws URISyntaxException, JsonProcessingException
    {
        URI uri = new URI(userUrl);

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        User userToBeAdded = new User(firstName, lastName, phoneNumber, email, password, null, Role.valueOf(userRole), AccountStatus.valueOf(accountStatus));

        HttpEntity<String> entity = new HttpEntity<>(mapper.writeValueAsString(userToBeAdded), headers);
        responseEntity = template.postForEntity(uri, entity, String.class);
    }


    @When("I want to get all users")
    public void iWantToGetAllUsers() throws URISyntaxException
    {
        String parameters = "?max=10&offset=0";
        URI uri = new URI(userUrl + parameters);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        entity = new HttpEntity<>(headers);
        responseEntity = template.exchange(uri, HttpMethod.GET, entity, String.class);
    }

    @When("I want to login with email {string}, password {string}")
    public void iWantToLoginWithEmailPassword(String email, String password) throws URISyntaxException, JsonProcessingException
    {
        Login(email, password);
    }

    @When("I want to update an user with id {string} with a new account that has  Firstname {string}, Lastname {string}, Phone number {string}, Email {string}, Password {string} , Type {string}, Status {string}")
    public void iWantToUpdateAnUserWithIdWithANewAccountThatHasFirstnameLastnamePhoneNumberEmailPasswordTypeStatus(String id, String firstName, String lastName, String phoneNumber, String email, String password, String userRole, String accountStatus) throws URISyntaxException, JsonProcessingException
    {
        URI uri = new URI(userUrl + "/" + id);

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + token);

        User newUser = new User(firstName, lastName, phoneNumber, email, password, null, Role.valueOf(userRole), AccountStatus.valueOf(accountStatus));

        HttpEntity<String> entity = new HttpEntity<>(mapper.writeValueAsString(newUser), headers);

        responseEntity = template.exchange(uri, HttpMethod.PUT, entity, String.class);
    }
}
