package io.swagger.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.model.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.repo.AccountRepo;
import io.swagger.repo.UserRepo;
import io.swagger.security.Role;
import io.swagger.service.AccountService;
import io.swagger.service.UserService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.NestedServletException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UsersApiControllerTest
{
    @Autowired
    private MockMvc mvc;

    @Mock
    UserService userService;

    @Autowired
    UserService realUserService;

    private User user;
    private String employeeToken;
    private ObjectMapper mapper;

    @BeforeEach
    public void setup() throws Exception
    {
        mapper = new ObjectMapper();

        LoginDto employee = new LoginDto("employee", "employee");
        user = realUserService.getUserByEmail("employee");
        employeeToken = this.mvc.perform(post("/Login")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(employee)))
                .andReturn().getResponse().getContentAsString();
    }

    @Test
    void deleteUserById() throws Exception
    {
        given(userService.deleteUserById(user.getId())).willReturn(user);

        this.mvc.perform(post("/Users/"+user.getId()+"/Delete")
                .header("authorization", "Bearer " + employeeToken))
                .andExpect(status().isOk());
    }

    @Test
    void loginUser() throws Exception
    {
        LoginDto customerDTO = new LoginDto("customer", "customer");
        this.mvc.perform(post("/Login").contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(customerDTO)))
                .andExpect(status().isOk());
    }

    @Test
    void createUser() throws Exception
    {
        User testUser = new User("firstNameTest", "lastNameTest", "3100000000", "emailTest", "passwordTest", new ArrayList<>(), Role.ROLE_CUSTOMER, AccountStatus.ACTIVE);
        this.mvc.perform(post("/Users").contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("authorization", "Bearer " + employeeToken)
                .content(mapper.writeValueAsString(testUser)))
                .andExpect(status().isCreated());
    }

    @Test
    void getUserById() throws Exception
    {
        given(userService.getUserById(user.getId())).willReturn(user);
        given(userService.getUserByEmail(user.getEmail())).willReturn(user);

        this.mvc.perform(get("/Users/" + user.getId())
                .header("authorization", "Bearer " + employeeToken))
                .andExpect(status().isOk());
    }

    @Test
    void getAllUsers() throws Exception
    {
        given(userService.getAllUsers()).willReturn(List.of(user));
        this.mvc.perform(get("/Users?offset=0&max=50")
                .header("authorization", "Bearer " + employeeToken))
                .andExpect(status().isOk());
    }
}