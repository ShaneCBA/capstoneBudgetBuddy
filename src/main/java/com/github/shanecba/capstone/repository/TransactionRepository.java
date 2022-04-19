package com.github.shanecba.capstone.repository;

import com.github.shanecba.capstone.entity.PlaidTransaction;
import com.github.shanecba.capstone.entity.Transaction;
import com.github.shanecba.capstone.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends CrudRepository<Transaction, Integer> {
    public List<Transaction> findByPlaidTransaction(PlaidTransaction plaidTransaction);

    public List<Transaction> findByPlaidTransactionDateGreaterThanAndUser(LocalDate date, User user);
    public List<Transaction> findByPlaidTransactionDateGreaterThanAndPlaidTransactionDateLessThanEqualAndUser(LocalDate start, LocalDate end, User user);
}
