package com.treeTest.treeTest.statement;

import com.treeTest.treeTest.account.Account;
import com.treeTest.treeTest.account.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;

import java.text.ParseException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class StatementServiceTest {
    @Mock
    private StatementRepository statementRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private Logger logger;

    @InjectMocks
    private StatementService statementService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void viewStatements_NoFilters_Success() throws ParseException { // return all the statements going back three months
        // Arrange
        Integer accountId = 4;
        String accountNumber = "0012250016001";
        String accountType = "Current";

        Account account = new Account();
        account.setId(accountId);
        account.setAccountNumber(accountNumber);
        account.setAccountType(accountType);

        List<Statement> statements = Arrays.asList(
                new Statement(1, account, "29.06.2023", "100.00"),
                new Statement(2, account, "02.07.2023", "200.00")
        );

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(statementRepository.findByAccountId(accountId)).thenReturn(statements);


        // Act
        List<Statement> result = statementService.viewStatements(accountId, null, null, null, null);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(statements, result);
    }


    @Test
    void viewStatements_DateFilter_Success() throws ParseException {
        // Arrange
        Integer accountId = 4;
        String accountNumber = "0012250016001";
        String accountType = "Current";

        Account account = new Account();
        account.setId(accountId);
        account.setAccountNumber(accountNumber);
        account.setAccountType(accountType);

        List<Statement> statements = Arrays.asList(
                new Statement(1, account, "01.01.2023", "100.00"),
                new Statement(2, account, "02.01.2023", "200.00"),
                new Statement(3, account, "03.01.2023", "300.00")
        );

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(statementRepository.findByAccountId(accountId)).thenReturn(statements);

        // Act
        List<Statement> result = statementService.viewStatements(accountId, Optional.of("01.01.2023"), Optional.of("02.01.2023"), null, null);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(statements.subList(0, 2), result);
    }

    @Test
    void viewStatements_AmountFilter_Success() throws ParseException {
        // Arrange
        Integer accountId = 4;
        String accountNumber = "0012250016001";
        String accountType = "Current";

        Account account = new Account();
        account.setId(accountId);
        account.setAccountNumber(accountNumber);
        account.setAccountType(accountType);

        List<Statement> statements = Arrays.asList(
                new Statement(1, account, "01.01.2023", "100.00"),
                new Statement(2, account, "02.01.2023", "200.00"),
                new Statement(3, account, "03.01.2023", "300.00")
        );

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(statementRepository.findByAccountId(accountId)).thenReturn(statements);

        // Act
        List<Statement> result = statementService.viewStatements(accountId, null, null, Optional.of("100.00"), Optional.of("200.00"));

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(statements.subList(0, 2), result);
    }

    @Test
    void viewStatements_DateAndAmountFilter_Success() throws ParseException {
        // Arrange
        Integer accountId = 4;
        String accountNumber = "0012250016001";
        String accountType = "Current";

        Account account = new Account();
        account.setId(accountId);
        account.setAccountNumber(accountNumber);
        account.setAccountType(accountType);

        List<Statement> statements = Arrays.asList(
                new Statement(1, account, "01.01.2023", "100.00"),
                new Statement(2, account, "02.01.2023", "200.00"),
                new Statement(3, account, "03.01.2023", "300.00")
        );

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(statementRepository.findByAccountId(accountId)).thenReturn(statements);

        // Act
        List<Statement> result = statementService.viewStatements(accountId, Optional.of("01.01.2023"), Optional.of("02.01.2023"), Optional.of("100.00"), Optional.of("200.00"));
        List<Statement> expected = new ArrayList<>(result);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expected, result);
    }

    @Test
    void viewStatements_InvalidDateRange_Exception() throws ParseException {
        // Arrange
        Integer accountId = 4;
        String accountNumber = "0012250016001";
        String accountType = "Current";

        Account account = new Account();
        account.setId(accountId);
        account.setAccountNumber(accountNumber);
        account.setAccountType(accountType);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                statementService.viewStatements(accountId, Optional.of("02.01.2023"), Optional.of("01.01.2023"), null, null)
        );
    }

    @Test
    void viewStatements_InvalidAmountRange_Exception() throws ParseException {
        // Arrange
        Integer accountId = 4;
        String accountNumber = "0012250016001";
        String accountType = "Current";

        Account account = new Account();
        account.setId(accountId);
        account.setAccountNumber(accountNumber);
        account.setAccountType(accountType);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                statementService.viewStatements(accountId, null, null, Optional.of("200.00"), Optional.of("100.00"))
        );
    }

    @Test
    void viewStatements_InternalServerError_Exception() throws ParseException {
        // Arrange
        Integer accountId = 4;
        String accountNumber = "0012250016001";
        String accountType = "Current";

        Account account = new Account();
        account.setId(accountId);
        account.setAccountNumber(accountNumber);
        account.setAccountType(accountType);

        when(accountRepository.findById(accountId)).thenThrow(new RuntimeException("Test Exception"));

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                statementService.viewStatements(accountId, null, null, null, null)
        );
    }

    @Test
    void viewStatements_ParseException_Exception() throws ParseException {
        // Arrange
        Integer accountId = 4;
        String accountNumber = "0012250016001";
        String accountType = "Current";

        Account account = new Account();
        account.setId(accountId);
        account.setAccountNumber(accountNumber);
        account.setAccountType(accountType);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                statementService.viewStatements(accountId, null, Optional.of("InvalidDate"), null, null)
        );
    }


    @Test
    void hashAccountNumber_ValidInput_Success() {
        // Arrange
        String accountNumber = "1234567890";

        // Act
        String hashedAccountNumber = statementService.hashAccountNumber(accountNumber);

        // Assert
        assertNotNull(hashedAccountNumber);
        assertNotEquals(accountNumber, hashedAccountNumber);
    }

    @Test
    void hashAccountNumber_NullInput_Exception() {
        // Arrange
        String accountNumber = null;

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                statementService.hashAccountNumber(accountNumber)
        );
    }
}