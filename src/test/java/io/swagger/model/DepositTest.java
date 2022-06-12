package io.swagger.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DepositTest {

    private static Deposit deposit;

    @BeforeEach
    public void createTestDeposit() {
        deposit =
                new Deposit("NL04INHO68681868171", 120D, UUID.randomUUID());
    }

    @Test
    @DisplayName("AccountTo should match iban")
    void getAccountTo() {
        assertEquals("NL04INHO68681868171", deposit.getAccountTo());
        String testIban = "NL99INHO99999999999";
        deposit.setAccountTo(testIban);
        assertEquals(testIban, deposit.getAccountTo());
    }

    @Test
    @DisplayName("AccountTo should match the set account")
    void setAccountTo() {
        assertEquals("NL04INHO68681868171", deposit.getAccountTo());
        deposit.setAccountTo("test");
        assertEquals("test", deposit.getAccountTo());
    }

    @Test
    @DisplayName("AccountTo should match AccountTo")
    void testEquals() {
        assertEquals("NL04INHO68681868171", deposit.getAccountTo());
    }

    @Test
    @DisplayName("HashCode should not be null")
    void testHashCode() {
        assertNotNull(deposit.hashCode()) ;
    }

    @Test
    @DisplayName("String Should not be null")
    void testToString() {
        assertNotNull(deposit.toString());
    }
    @Test
    @DisplayName("deposit should not be null")
    public void depositShouldNotBeNull() {
        assertNotNull(deposit);
    }

    @Test
    @DisplayName("TransactionID should not be null")
    public void transactionIdShouldNotAllowNull() {
        assertThrows(IllegalArgumentException.class, () -> deposit.setTransactionId(null));
    }

    @Test
    @DisplayName("AccountTo should not be null")
    public void setAccountToShouldNotAllowNull() {
        assertNotNull(deposit.getAccountTo());
    }
    @Test
    @DisplayName("TransactionDate should be no more than 15 minutes past")
    public void setTransactionDateThrowsIllegalArgumentExceptionIfDateBeforeToday() {
        LocalDateTime date = LocalDateTime.now().minusMinutes(15);
        assertThrows(IllegalArgumentException.class, () -> deposit.setTransactionDate(date));
    }

    @ParameterizedTest
    @DisplayName("deposit amount should not be 0 or less")
    @ValueSource(doubles = {0, -0.01})
    public void depositAmountThrowsIllegalArgumentExceptionIfZeroOrLess(double amount)
    {
        assertNotNull(deposit.getAmount());
        assertThrows(IllegalArgumentException.class, () -> deposit.setAmount(amount));
    }

    @Test
    @DisplayName("getTransactionId should match setTransactionId")
    public void setTransactionShouldMatchGivenIdWhenGetTransactionId()
    {
        UUID id = UUID.randomUUID();
        deposit.setTransactionId(id);
        assertEquals(id, deposit.getTransactionId());
    }

    @Test
    @DisplayName("No args constructor should not be null")
    public void noArgsConstructorShouldNotBeNull()
    {
        deposit = new Deposit();
        assertNotNull(deposit);
    }

    @Test
    @DisplayName("getTransactionDate should return now if not set")
    public void getTransactionDateShouldReturnNowIfNotSet()
    {
        assertNotNull(deposit.getTransactionDate());
        assertTrue(deposit.getTransactionDate().isAfter(LocalDateTime.now().minusSeconds(1)));
        assertTrue(deposit.getTransactionDate().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    @DisplayName("getTransactionDate should match setTransactionDate")
    public void getTransactionDateShouldMatchSetTransactionDate()
    {
        LocalDateTime expectedDate = LocalDateTime.now().plusMinutes(15);
        deposit.setTransactionDate(expectedDate);
        assertEquals(expectedDate, deposit.getTransactionDate());
    }

    @Test
    @DisplayName("getTransactionDate should match setTransactionDate")
    public void getPerformedByShouldMatchSetPerformedBy()
    {
        UUID expectedId = UUID.randomUUID();
        deposit.setPerformedBy(expectedId);
        assertEquals(expectedId, deposit.getPerformedBy());

    }

}