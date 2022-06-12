Feature: Account Tests

  Scenario: As an employee, I want to see all accounts
    Given I am an employee
    When I want to see all accounts
    Then Display HTTPStatus 200

  Scenario: As a customer, I want to see my accounts
    Given I am a customer
    When I want to see all accounts
    Then Display HTTPStatus 200

  Scenario: As an employee, I want to get an account by its iban
    Given I am an employee
    When I want to get the account with iban "NL01INHO0000000044"
    Then Display HTTPStatus 200

  Scenario: As a customer, I want to get an account by its iban
    Given I am a customer
    When I want to get the account with iban "NL01INHO0000000052"
    Then Display HTTPStatus 200

  Scenario: As an employee, I want to create a new account
    Given I am an employee
    When I want to create a new "saving" account
    Then Display HTTPStatus 201

  Scenario: As an employee, I want to change an account's account type
    Given I am an employee
    When I want to change account with iban "NL01INHO0000000044" type to "current"
    Then Display HTTPStatus 200

  Scenario: As an employee, I want to change an account's account status
    Given I am an employee
    When I want to change account with iban "NL01INHO0000000044" status to "disabled"
    Then Display HTTPStatus 200

  Scenario: As a customer, I want to change an account's account status
    Given I am a customer
    When I want to change account with iban "NL01INHO0000000052" status to "disabled"
    Then Display HTTPStatus 200

  Scenario: As an employee, I want to get an account's balance
    Given I am an employee
    When I want to get account's balance with iban "NL01INHO0000000044"
    Then Display HTTPStatus 200

  Scenario: As a customer, I want to get an account's balance
    Given I am a customer
    When I want to get account's balance with iban "NL01INHO0000000052"
    Then Display HTTPStatus 200

  Scenario: As an employee, I want to update an account
    Given I am an employee
    When I want to update an account iban "NL01INHO0000000044" with a new "saving" account
    Then Display HTTPStatus 200

  Scenario: As a customer, I want to update an account
    Given I am a customer
    When I want to update an account iban "NL01INHO0000000052" with a new "saving" account
    Then Display HTTPStatus 200