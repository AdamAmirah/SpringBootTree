package com.treeTest.treeTest.account;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    @Id
    private Integer id;
    @Column("account_type")
    private String accountType;
    @Column("account_number")
    private String accountNumber;
}
