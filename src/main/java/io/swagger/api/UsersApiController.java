package io.swagger.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.model.LoginDto;
import io.swagger.model.User;
import io.swagger.security.JwtTokenProvider;
import io.swagger.security.Role;
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

import javax.annotation.security.PermitAll;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2021-06-06T11:20:30.422Z[GMT]")
@RestController
public class UsersApiController implements UsersApi
{

    private static final Logger log = LoggerFactory.getLogger(UsersApiController.class);

    private final ObjectMapper objectMapper;
    private final HttpServletRequest request;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UserService userService;

    @org.springframework.beans.factory.annotation.Autowired
    public UsersApiController(ObjectMapper objectMapper, HttpServletRequest request)
    {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<User> createUser(@Parameter(in = ParameterIn.DEFAULT, description = "", required = true, schema = @Schema()) @Valid @RequestBody User user) throws Exception
    {
        userService.register(user);
        return new ResponseEntity<User>(HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('EMPLOYEE') OR hasRole('CUSTOMER')")
    public ResponseEntity<User> getCurrentUser() throws Exception
    {
        try{
        String token = tokenProvider.resolveToken(request);
        String email = tokenProvider.getUsername(token);

        User loggedInUser = userService.getUserByEmail(email);

        return new ResponseEntity<User>(HttpStatus.OK).status(200).body(loggedInUser);
        }
        catch (Exception e){
        return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<Void> deleteUserById(@Parameter(in = ParameterIn.PATH, description = "", required = true, schema = @Schema()) @PathVariable("id") String id) throws Exception
    {
        UUID userID = userService.convertID(id);
        User userToBeDeleted = userService.deleteUserById(userID);
        if (userToBeDeleted != null) return new ResponseEntity<Void>(HttpStatus.OK);
        else return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<List<User>> getAllUsers(@Min(0) @Parameter(in = ParameterIn.QUERY, description = "The number of items to skip before starting to collect the result set.", schema = @Schema(allowableValues = {})) @Valid @RequestParam(value = "offset", required = false) Integer offset, @Min(10) @Max(50) @Parameter(in = ParameterIn.QUERY, description = "The maximum number of items to return.", schema = @Schema(allowableValues = {}, minimum = "10", maximum = "50", defaultValue = "10")) @Valid @RequestParam(value = "max", required = false, defaultValue = "10") Integer max) throws Exception
    {
        List<User> allUsers = new ArrayList<User>();
        List<User> usersList = new ArrayList<User>();

        allUsers = userService.getAllUsers();
        if (allUsers != null)
        {
            long maxValue = max + offset;

            //If the maxValue is bigger then existing users, max value is equal to user count
            if (maxValue > allUsers.stream().count()) maxValue = allUsers.stream().count();

            for (int i = offset; i < maxValue; i++)
                usersList.add(allUsers.get(i));

            return new ResponseEntity<List<User>>(HttpStatus.OK).status(200).body(usersList);
        } else return new ResponseEntity<List<User>>(HttpStatus.NOT_FOUND);
    }

    @PreAuthorize("hasRole('EMPLOYEE') OR hasRole('CUSTOMER')")
    public ResponseEntity<User> getUserById(@Parameter(in = ParameterIn.PATH, description = "", required = true, schema = @Schema()) @PathVariable("id") String id) throws Exception
    {
        String token = tokenProvider.resolveToken(request);
        String email = tokenProvider.getUsername(token);

        User loggedInUser = userService.getUserByEmail(email);
        Role role = loggedInUser.getRole();

        if (loggedInUser != null)
        {
            UUID userID = userService.convertID(id);

            //If its a user then check if this is him/her
            if (role == Role.ROLE_CUSTOMER)
            {
                if (userID.equals(loggedInUser.getId()))
                {
                    return new ResponseEntity<User>(userService.getUserById(userID), HttpStatus.OK);
                }
                //If its not him/her, return unauthorized
                else return new ResponseEntity<User>(HttpStatus.UNAUTHORIZED);

                //If its an employee then return the user
            } else if (role == Role.ROLE_EMPLOYEE)
                return new ResponseEntity<User>(userService.getUserById(userID), HttpStatus.OK);
            else return new ResponseEntity<User>(HttpStatus.BAD_REQUEST);
        } else return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
    }


    @PostMapping("/Login")
    @PermitAll
    public ResponseEntity<String> loginUser(@Parameter(in = ParameterIn.DEFAULT, description = "", schema = @Schema()) @Valid @RequestBody LoginDto loginDto)
    {
        try
        {
            return new ResponseEntity<String>(userService.login(loginDto.getEmailAddress(), loginDto.getPassword()), HttpStatus.OK);
        } catch (Exception e)
        {
            e.printStackTrace();
            return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('EMPLOYEE') OR hasRole('CUSTOMER')")
    public ResponseEntity<User> updateUserById(@Parameter(in = ParameterIn.PATH, description = "", required = true, schema = @Schema()) @PathVariable("id") String id, @Parameter(in = ParameterIn.DEFAULT, description = "", schema = @Schema()) @Valid @RequestBody User user) throws Exception
    {
        String token = tokenProvider.resolveToken(request);
        String email = tokenProvider.getUsername(token);

        User loggedInUser = userService.getUserByEmail(email);
        Role role = loggedInUser.getRole();

        if (loggedInUser != null)
        {
            UUID userID = userService.convertID(id);
            //If its a user then check if this is him/her
            if (role == Role.ROLE_CUSTOMER)
            {
                if (userID.equals(loggedInUser.getId()))
                {
                    user.setId(loggedInUser.getId());
                    userService.updateUser(user);
                    return new ResponseEntity<User>(user, HttpStatus.OK);
                }
                //If its not him/her, return unauthorized
                else return new ResponseEntity<User>(HttpStatus.UNAUTHORIZED);
                //If its an employee then update the user
            } else
            {
                //user.setId(userID);
                userService.updateUser(user);
                return new ResponseEntity<User>(user, HttpStatus.OK);
            }
        }
        return new ResponseEntity<User>(HttpStatus.NOT_FOUND).status(404).body(null);
    }
}
