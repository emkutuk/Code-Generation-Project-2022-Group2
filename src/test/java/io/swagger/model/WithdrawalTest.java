package io.swagger.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class WithdrawalTest {

    private static Withdrawal withdrawal;

    @BeforeEach
    public void createTestWithdrawal() {
        withdrawal =
                new Withdrawal("NL04INHO68681868171", 120D,UUID.randomUUID());
    }

    @Test
    @DisplayName("AccountFrom should match iban")
    void getAccountFrom() {
        assertEquals("NL04INHO68681868171", withdrawal.getAccountFrom());
        String testIban = "NL99INHO99999999999";
        withdrawal.setAccountFrom(testIban);
        assertEquals(testIban, withdrawal.getAccountFrom());
    }

    @Test
    @DisplayName("AccountFrom should match the set account")
    void setAccountFrom() {
        assertEquals("NL04INHO68681868171", withdrawal.getAccountFrom());
        withdrawal.setAccountFrom("test");
        assertEquals("test", withdrawal.getAccountFrom());
    }

    @Test
    @DisplayName("AccountFrom should match AccountTo")
    void testEquals() {
        assertEquals("NL04INHO68681868171", withdrawal.getAccountFrom());
    }

    @Test
    @DisplayName("HashCode should not be null")
    void testHashCode() {
        assertNotNull(withdrawal.hashCode()) ;
    }

    @Test
    @DisplayName("String Should not be null")
    void testToString() {
        assertNotNull(withdrawal.toString());
    }
    @Test
    @DisplayName("Withdrawal should not be null")
    public void withdrawalShouldNotBeNull() {
        assertNotNull(withdrawal);
    }

    @Test
    @DisplayName("TransactionID should not be null")
    public void transactionIdShouldNotAllowNull() {
        assertThrows(IllegalArgumentException.class, () -> withdrawal.setTransactionId(null));
    }

    @Test
    @DisplayName("AccountFrom should not be null")
    public void setAccountFromShouldNotAllowNull() {
        assertNotNull(withdrawal.getAccountFrom());
    }
    @Test
    @DisplayName("TransactionDate should be no more than 15 minutes past")
    public void setTransactionDateThrowsIllegalArgumentExceptionIfDateBeforeToday() {
        LocalDateTime date = LocalDateTime.now().minusMinutes(15);
        assertThrows(IllegalArgumentException.class, () -> withdrawal.setTransactionDate(date));
    }

    @ParameterizedTest
    @DisplayName("Withdrawal amount should not be 0 or less")
    @ValueSource(doubles = {0, -0.01})
    public void withdrawalAmountThrowsIllegalArgumentExceptionIfZeroOrLess(double amount)
    {
        assertNotNull(withdrawal.getAmount());
        assertThrows(IllegalArgumentException.class, () -> withdrawal.setAmount(amount));
    }

    @Test
    @DisplayName("getTransactionId should match setTransactionId")
    public void setTransactionShouldMatchGivenIdWhenGetTransactionId()
    {
        UUID id = UUID.randomUUID();
        withdrawal.setTransactionId(id);
        assertEquals(id, withdrawal.getTransactionId());
    }

    @Test
    @DisplayName("No args constructor should not be null")
    public void noArgsConstructorShouldNotBeNull()
    {
        withdrawal = new Withdrawal();
        assertNotNull(withdrawal);
    }

    @Test
    @DisplayName("getTransactionDate should return now if not set")
    public void getTransactionDateShouldReturnNowIfNotSet()
    {
        assertNotNull(withdrawal.getTransactionDate());
        assertTrue(withdrawal.getTransactionDate().isAfter(LocalDateTime.now().minusSeconds(1)));
        assertTrue(withdrawal.getTransactionDate().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    @DisplayName("getTransactionDate should match setTransactionDate")
    public void getTransactionDateShouldMatchSetTransactionDate()
    {
        LocalDateTime expectedDate = LocalDateTime.now().plusMinutes(15);
        withdrawal.setTransactionDate(expectedDate);
        assertEquals(expectedDate, withdrawal.getTransactionDate());
    }

    @Test
    @DisplayName("getTransactionDate should match setTransactionDate")
    public void getPerformedByShouldMatchSetPerformedBy()
    {
        UUID expectedId = UUID.randomUUID();
        withdrawal.setPerformedBy(expectedId);
        assertEquals(expectedId, withdrawal.getPerformedBy());

    }

}