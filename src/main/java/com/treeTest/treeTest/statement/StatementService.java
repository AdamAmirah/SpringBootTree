package com.treeTest.treeTest.statement;

import com.treeTest.treeTest.account.Account;
import com.treeTest.treeTest.account.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Service
@RequiredArgsConstructor
public class StatementService {
    private final StatementRepository statementRepository;
    private final AccountRepository accountRepository;
    private final Logger logger = LoggerFactory.getLogger(StatementService.class);

    public List<Statement> viewStatements(
            Integer accountId,
            Optional<String> fromDate,
            Optional<String> toDate,
            Optional<String> fromAmount,
            Optional<String> toAmount
    ) throws ParseException {
        try {
            Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        List<Statement> statements = statementRepository.findByAccountId(account.getId());

        logger.debug("Statements: {}", statements);

        // Convert date and amount strings to appropriate types
        Date parsedFromDate = fromDate != null ? convertToDate(fromDate.get(), "dd.mm.yyyy") : null;
        Date parsedToDate = toDate != null  ? convertToDate(toDate.get(), "dd.mm.yyyy") : null;
        BigDecimal parsedFromAmount = fromAmount != null ? convertToBigDecimal(fromAmount.get()) : null;
        BigDecimal parsedToAmount = toAmount != null ? convertToBigDecimal(toAmount.get()) : null;


        // Validate date
        if (parsedFromDate != null && parsedToDate != null && parsedFromDate.after(parsedToDate)) {
            throw new IllegalArgumentException("Invalid date range");
        }
            //Validate amount
            if (parsedFromAmount != null && parsedToAmount != null && parsedFromAmount.compareTo(parsedToAmount) > 0) {
                throw new IllegalArgumentException("Invalid amount range");
            }


            if (fromDate != null && toDate != null && fromAmount != null && toAmount != null) {
                return filterByDateAndAmount(statements, account, parsedFromDate, parsedToDate, parsedFromAmount, parsedToAmount);
            } else if (fromDate != null && toDate != null) {
                return filterByDate(statements, account, parsedFromDate, parsedToDate);
            } else if (fromAmount != null && toAmount != null) {
                return filterByAmount(statements, account, parsedFromAmount, parsedToAmount);
            } else {
                return threeMonthsStatements(statements, account);
            }
        } catch (IllegalArgumentException e) {
            logger.error("Bad request: {}", e.getMessage());
            throw new IllegalArgumentException("Bad request: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Internal server error: {}", e.getMessage());
            throw new RuntimeException("Internal server error.");
        }
    }

    private List<Statement> threeMonthsStatements(List<Statement> statements, Account account) throws ParseException {
        try {
            LocalDate currentDate = LocalDate.now();
            LocalDate threeMonthsAgo = currentDate.minusMonths(3);

            Date fromDate = convertToDate(threeMonthsAgo.atStartOfDay().toString(), "yyyy-MM-dd'T'HH:mm");
            Date toDate = convertToDate(currentDate.atStartOfDay().toString(), "yyyy-MM-dd'T'HH:mm");

            return statements.stream()
                    .filter(statement -> {
                        try {
                            Date statementDate = convertToDate(statement.getDateField(), "dd.MM.yyyy");
                            boolean isInRange = isDateInRange(statementDate, fromDate, toDate);

                            if (!isInRange) {
                                logger.debug("Statement date not in range: {}", statementDate);
                            }

                            return isInRange;
                        } catch (ParseException e) {
                            throw new RuntimeException("Error parsing statement date", e);
                        }
                    })
                    .peek(statement -> {
                        statement.getAccount().setAccountNumber(account.getAccountNumber());
                        statement.getAccount().setAccountType(account.getAccountType());

                        String hashedAccountNumber = hashAccountNumber(statement.getAccount().getAccountNumber());
                        statement.getAccount().setAccountNumber(hashedAccountNumber);
                    })
                    .collect(Collectors.toList());

        } catch (RuntimeException e) {
            logger.error("Error in threeMonthsStatements: {}", e.getMessage());
            throw new RuntimeException("Error in threeMonthsStatements: " + e.getMessage());
        }
    }

    private List<Statement> filterByAmount(List<Statement> statements, Account account, BigDecimal parsedFromAmount, BigDecimal parsedToAmount) {
        try {
            return statements.stream()
                    .filter(statement -> isAmountInRange(convertToBigDecimal(statement.getAmount()), parsedFromAmount, parsedToAmount))
                    .peek(statement -> {
                        statement.getAccount().setAccountNumber(account.getAccountNumber());
                        statement.getAccount().setAccountType(account.getAccountType());

                        String hashedAccountNumber = hashAccountNumber(statement.getAccount().getAccountNumber());
                        statement.getAccount().setAccountNumber(hashedAccountNumber);
                    })
                    .collect(Collectors.toList());
        } catch (RuntimeException e) {
            logger.error("Error in filterByAmount: {}", e.getMessage());
            throw new RuntimeException("Error in filterByAmount: " + e.getMessage());
        }
    }

    private List<Statement> filterByDate(List<Statement> statements, Account account, Date parsedFromDate, Date parsedToDate) {
        try {
            return statements.stream()
                    .filter(statement -> {
                        try {
                            return isDateInRange(convertToDate(statement.getDateField(),"dd.mm.yyyy"), parsedFromDate, parsedToDate);
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .peek(statement -> {
                        statement.getAccount().setAccountNumber(account.getAccountNumber());
                        statement.getAccount().setAccountType(account.getAccountType());

                        String hashedAccountNumber = hashAccountNumber(statement.getAccount().getAccountNumber());
                        statement.getAccount().setAccountNumber(hashedAccountNumber);
                    })
                    .collect(Collectors.toList());

        } catch (RuntimeException e) {
            logger.error("Error in filterByDate: {}", e.getMessage());
            throw new RuntimeException("Error in filterByDate: " + e.getMessage());
        }
    }

    private List<Statement> filterByDateAndAmount(List<Statement> statements, Account account, Date parsedFromDate, Date parsedToDate, BigDecimal parsedFromAmount, BigDecimal parsedToAmount) {
        try {
            return statements.stream()
                .filter(statement -> {
                    try {
                        return isDateInRange(convertToDate(statement.getDateField(),"dd.mm.yyyy"), parsedFromDate, parsedToDate);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                })
                .filter(statement -> isAmountInRange(convertToBigDecimal(statement.getAmount()), parsedFromAmount, parsedToAmount))
                .peek(statement -> {
                    statement.getAccount().setAccountNumber(account.getAccountNumber());
                    statement.getAccount().setAccountType(account.getAccountType());

                    String hashedAccountNumber = hashAccountNumber(statement.getAccount().getAccountNumber());
                    statement.getAccount().setAccountNumber(hashedAccountNumber);
                })
                .collect(Collectors.toList());
        } catch (RuntimeException e) {
            logger.error("Error in filterByDateAndAmount: {}", e.getMessage());
            throw new RuntimeException("Error in filterByDateAndAmount: " + e.getMessage());
        }
    }


    private boolean isDateInRange(Date date, Date fromDate, Date toDate) {
        return (fromDate == null || date.compareTo(fromDate) >= 0) &&
                (toDate == null || date.compareTo(toDate) <= 0);
    }

    private boolean isAmountInRange(BigDecimal amount, BigDecimal fromAmount, BigDecimal toAmount) {
        return (fromAmount == null || amount.compareTo(fromAmount) >= 0) &&
                (toAmount == null || amount.compareTo(toAmount) <= 0);
    }
    private Date convertToDate (String date, String pattern) throws ParseException {

        if (date == null) {
            return null;
        }
        try {
            return new SimpleDateFormat(pattern).parse(date);
        } catch (ParseException ex) {
            logger.error("Error parsing date: {}", ex.getMessage());
            throw new IllegalArgumentException("Invalid date format: " + date);
        }
    }

    private BigDecimal convertToBigDecimal (String str){
        if (str == null) {
            return null;
        }
        try {
            return new BigDecimal(str);
        } catch (NumberFormatException ex) {
            logger.error("Error parsing amount: {}", ex.getMessage());
            throw new IllegalArgumentException("Invalid amount format: " + str);
        }
    }

    public static String hashAccountNumber(String accountNumber) {
        try {
            // Create an instance of the SHA-256 hash algorithm
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] accountNumberBytes = accountNumber.getBytes(StandardCharsets.UTF_8);
            byte[] hashBytes = digest.digest(accountNumberBytes);

            // Convert the hash bytes to a hexadecimal representation
            StringBuilder hexString = new StringBuilder();
            for (byte hashByte : hashBytes) {
                String hex = Integer.toHexString(0xff & hashByte);
                if (hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException("Error hashing the account number", ex);
        }
    }

}
