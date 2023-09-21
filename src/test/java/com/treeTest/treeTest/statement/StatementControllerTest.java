package com.treeTest.treeTest.statement;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.text.ParseException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

class StatementControllerTest {
    @InjectMocks
    private StatementController statementController;
    @Mock
    private StatementService statementService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void viewStatements_AdminRole_Success() throws ParseException {
//        setUp();
        // Arrange
        GetStatementsRequest request = new GetStatementsRequest();
        Authentication authentication = mock(Authentication.class);

        Collection grantedAuthorities = Lists.newArrayList(new SimpleGrantedAuthority("ROLE_ADMIN"));
        when(authentication.getAuthorities()).thenReturn(grantedAuthorities);

        List<Statement> statements = new ArrayList<>();
        when(statementService.viewStatements(any(), any(), any(), any(), any())).thenReturn(statements);

        // Act
        ResponseEntity<Object> response = statementController.viewStatements(request, authentication);
        System.out.print(response);
        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(statementService, times(1)).viewStatements(any(), any(), any(), any(), any());
    }

    @Test
    void viewStatements_AdminRole_Error() throws ParseException {
        // Arrange
        GetStatementsRequest request = new GetStatementsRequest();
        Authentication authentication = mock(Authentication.class);
        Collection grantedAuthorities = Lists.newArrayList(new SimpleGrantedAuthority("ROLE_ADMIN"));
        when(authentication.getAuthorities()).thenReturn(grantedAuthorities);

        when(statementService.viewStatements(any(), any(), any(), any(), any())).thenThrow(new IllegalArgumentException("Invalid argument"));

        // Act
        ResponseEntity<Object> response = statementController.viewStatements(request, authentication);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(statementService, times(1)).viewStatements(any(), any(), any(), any(), any());
    }
    @Test
    void viewStatements_UserRole_WithParameters() throws ParseException {
        // Arrange
        GetStatementsRequest request = new GetStatementsRequest();
        Authentication authentication = mock(Authentication.class);

        Collection grantedAuthorities = Lists.newArrayList(new SimpleGrantedAuthority("ROLE_USER"));
        when(authentication.getAuthorities()).thenReturn(grantedAuthorities);

        request.setFromDate(Optional.of("2023-01-01"));
        request.setToDate(Optional.of("2023-12-31"));
        request.setFromAmount(Optional.of("100"));
        request.setToAmount(Optional.of("1000"));

        List<Statement> statements = new ArrayList<>();
        when(statementService.viewStatements(any(), any(), any(), any(), any())).thenReturn(statements);

        // Act
        ResponseEntity<Object> response = statementController.viewStatements(request, authentication);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        verify(statementService, never()).viewStatements(any(), any(), any(), any(), any());
    }

    @Test
    void viewStatements_UserRole_NoParameters() throws ParseException {
        // Arrange
        GetStatementsRequest request = new GetStatementsRequest();
        Authentication authentication = mock(Authentication.class);
        Collection grantedAuthorities = Lists.newArrayList(new SimpleGrantedAuthority("ROLE_USER"));
        when(authentication.getAuthorities()).thenReturn(grantedAuthorities);

        List<Statement> statements = new ArrayList<>();
        when(statementService.viewStatements(any(), any(), any(), any(), any())).thenReturn(statements);

        // Act
        ResponseEntity<Object> response = statementController.viewStatements(request, authentication);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(statementService, times(1)).viewStatements(any(), isNull(), isNull(), isNull(), isNull());
    }
    @Test
    void viewStatements() {
    }
}