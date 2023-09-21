package com.treeTest.treeTest.account;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface AccountRepository extends CrudRepository<Account, Integer>
{
    @Query("SELECT * FROM Account a WHERE a.id = :accountId")
    Optional<Account> findById(Integer accountId);
}
