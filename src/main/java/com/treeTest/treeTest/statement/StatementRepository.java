package com.treeTest.treeTest.statement;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public interface StatementRepository extends CrudRepository<Statement, Integer>
{
    @Query("SELECT s.*, a.* " +
            "FROM statement s " +
            "JOIN account a ON s.account_id = a.id " +
            "WHERE a.id = :accountId")
    List<Statement> findByAccountId(Integer accountId);
}
