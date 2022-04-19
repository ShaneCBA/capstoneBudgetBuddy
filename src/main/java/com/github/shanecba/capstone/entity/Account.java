package com.github.shanecba.capstone.entity;

import com.plaid.client.model.AccountType;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Component
@Entity
@Table(name = "account")
public class Account {
    @Id
    @Column(name="account_id")
    private String accountId;

    @ManyToOne(cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
    @JoinColumn(name="item_id")
    private PlaidItem item;

    @Column(name="name")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name="type")
    private AccountType type;

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.DETACH,
            CascadeType.REFRESH })
    private List<PlaidTransaction> transactionList;

    public Account() {
        transactionList = new ArrayList<>();
    }

    public Account(String accountId, PlaidItem item, String name, AccountType type) {
        this();
        this.accountId = accountId;
        this.item = item;
        this.name = name;
        this.type = type;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public PlaidItem getItem() {
        return item;
    }

    public void setItem(PlaidItem item) {
        this.item = item;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AccountType getType() {
        return type;
    }

    public void setType(AccountType type) {
        this.type = type;
    }

    public List<PlaidTransaction> getTransactionList() {
        return transactionList;
    }

    public void setTransactionList(List<PlaidTransaction> transactionList) {
        this.transactionList = transactionList;
    }

    public void addTransaction(PlaidTransaction transaction) {
        this.transactionList.add(transaction);
    }

    public void removeTransaction(PlaidTransaction transaction) {
        this.transactionList.remove(transaction);
    }

    @Override
    public String toString() {
        return "Account{" +
                "accountId='" + accountId + '\'' +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", transactionList=" + transactionList +
                '}';
    }
}
