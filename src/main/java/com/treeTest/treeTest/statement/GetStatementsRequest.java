package com.treeTest.treeTest.statement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetStatementsRequest {
    private Integer accountId;
    private Optional<String> fromDate;
    private Optional<String> toDate;
    private Optional<String> fromAmount;
    private Optional<String> toAmount;
}
