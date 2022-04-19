package com.github.shanecba.capstone.repository;

import com.github.shanecba.capstone.entity.Account;
import com.github.shanecba.capstone.entity.PlaidTransaction;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface PlaidTransactionRepository extends CrudRepository<PlaidTransaction, Integer> {
    public List<PlaidTransaction> findFirstByAccountOrderByDateDesc(Account account);
    public List<PlaidTransaction> findByTransactionID(String transactionID);
    public List<PlaidTransaction> deleteByTransactionID(String transactionID);

    public List<PlaidTransaction> findByDateGreaterThanAndAccount(LocalDate date, Account account);

}
