package com.treeTest.treeTest.statement;


import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/statements")
@RequiredArgsConstructor
public class StatementController {

    private final StatementService statementService;
    private final Logger logger = LoggerFactory.getLogger(StatementController.class);


    @GetMapping
    public ResponseEntity<Object> viewStatements(@RequestBody GetStatementsRequest request, Authentication authentication) throws ParseException {
        try {
            if (hasAdminRole(authentication)) {
                List<Statement> statements = statementService.viewStatements(
                        request.getAccountId(),
                        request.getFromDate(),
                        request.getToDate(),
                        request.getFromAmount(),
                        request.getToAmount());

                if (statements.isEmpty()) {
                    return new ResponseEntity<>("No statements found.", HttpStatus.NO_CONTENT);
                } else {
                    return new ResponseEntity<>(statements, HttpStatus.OK);
                }

        } else {

            if (containsParameters(request.getFromDate(), request.getToDate(), request.getFromAmount(), request.getToAmount())) {
                return new ResponseEntity<>("Unauthorized. Admin role required.", HttpStatus.UNAUTHORIZED);
            }else{
                List<Statement> statements = statementService.viewStatements(request.getAccountId(), null, null,null,null);
                if (statements.isEmpty()) {
                    return new ResponseEntity<>("No statements found.", HttpStatus.NO_CONTENT);
                } else {
                    return new ResponseEntity<>(statements, HttpStatus.OK);
                }
            }
        }
        } catch (IllegalArgumentException e) {
            logger.error("Bad request: {}", e.getMessage());
            return new ResponseEntity<>("Bad request: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Internal server error: {}", e.getMessage());
            return new ResponseEntity<>("Internal server error.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    private boolean hasAdminRole(Authentication authentication) {
        if (authentication != null) {
            logger.debug("Authentication authorities: {}", authentication.getAuthorities());
            return authentication.getAuthorities().stream()
                    .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
        }
        return false;
    }

    private boolean containsParameters(Optional<String>... parameters) {
        for (Optional<String> param : parameters) {
            if (param != null) {
                return true;
            }
        }
        return false;
    }
}
