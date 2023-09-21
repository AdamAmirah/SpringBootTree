package com.treeTest.treeTest.statement;

import com.treeTest.treeTest.account.Account;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("statement")
public class Statement {
    @Id
    private Integer id;
    @Column("account_id")
    private Account account;
    @Column("datefield")
    private String dateField;
    private String amount;
}
