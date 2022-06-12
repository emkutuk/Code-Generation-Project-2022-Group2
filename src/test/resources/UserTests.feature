Feature: User Tests

  Scenario: As an employee, I want to create another user
    Given I am an Employee
    When I want to create another user with Firstname "Emre", Lastname "Kutuk", Phone number "+31685032148", Email "emkutuk@gmail.com", Password "pa55w0rd" , Type "ROLE_CUSTOMER", Status "ACTIVE"
    Then Show HTTPStatus 201

  Scenario: As an employee, I want to get all users
    Given I am an Employee
    When I want to get all users
    Then Show HTTPStatus 200

  Scenario: As a user, I want to login into my account
    When I want to login with email "employee", password "employee"
    Then Show HTTPStatus 200