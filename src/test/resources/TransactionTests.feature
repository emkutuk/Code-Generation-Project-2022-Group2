Feature: Transaction Tests

  Scenario: As a customer I want to return all my transactions
    Given I am A Customer
    When I want to return all my transactions with offset "0" and max "10"
    Then Return HTTPStatus 200

  Scenario: As a customer I want to return transaction
    Given I am A Customer
    When I want to return my transaction with id "cae6a16b6e424a8badbdde9a636d229f"
    Then Return HTTPStatus 200

  Scenario: As a customer I want to deposit cash into my account
    Given I am A Customer
    When I want to deposit "15" Euros into my account
    Then Return HTTPStatus 201

  Scenario: As a customer I want to withdraw money from my account
    Given I am A Customer
    When I want to withdraw "15" Euros from my account with iban "NL01INHO0000000055" and user id "3fa85f64-5717-4562-b3fc-2c963f66afa7"
    Then Return HTTPStatus 200

  Scenario: As an employee I want to review a transaction for a customer
    Given I am another employee
    When I want to review a customer's transaction with id "cae6a16b6e424a8badbdde9a636d229f"
    Then Return HTTPStatus 200

  Scenario: As a customer I want to transfer money to another customers account
    Given I am A Customer
    When I want to transfer "15" Euro from my account that has "NL01INHO0000000055" to their account that has "NL01INHO0000000054"
    Then Return HTTPStatus 201

  Scenario: As a customer I want to transfer money between my accounts
    Given I am A Customer
    When I want to transfer "20" Euro from my one account that has iban "NL01INHO0000000055" to my another account that has iban "NL01INHO0000000054"
    Then Return HTTPStatus 200

  Scenario: As an employee I want to transfer money to a savings account on behalf of a customer
    Given I am another employee
    When I want to transfer "15" Euros from current account "NL01INHO0000000055" to savings account "NL01INHO0000000054" on behalf of a customer
    Then Return HTTPStatus 200

  Scenario: As an employee I want to transfer money to another customer on behalf of a customer
    Given I am another employee
    When I want to transfer "25" Euros from current account with iban "NL01INHO0000000055" to another customers account with iban "NL01INHO0000000052" on behalf of a customer
    Then Return HTTPStatus 200

