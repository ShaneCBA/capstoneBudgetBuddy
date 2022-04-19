package com.github.shanecba.capstone.entity;

import com.github.shanecba.capstone.types.TransactionState;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.time.LocalDate;

@Component
@Entity
@Table(name = "plaid_transaction")
public class PlaidTransaction {
    @Id
    @Column(name = "event_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int eventID;

    @Column(name = "transaction_id")
    private String transactionID;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @Enumerated(EnumType.STRING)
    @Column(name="state")
    private TransactionState state;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "name")
    private String name;

    @Column(name = "merchant")
    private String merchant;

    @Column(name = "category")
    private String category;

    @Column(name = "amount")
    private double amount;

    public PlaidTransaction() {

    }

    public PlaidTransaction(String transactionID, Account account, TransactionState state, LocalDate date) {
        this.transactionID = transactionID;
        this.account = account;
        this.state = state;
        this.date = date;
    }

    public PlaidTransaction(String transactionID, Account account, TransactionState state, LocalDate date, String category) {
        this(transactionID, account, state, date);
        this.category = category;
    }

    public int getEventID() {
        return eventID;
    }

    public void setEventID(int eventID) {
        this.eventID = eventID;
    }

    public String getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public TransactionState getState() {
        return state;
    }

    public void setState(TransactionState state) {
        this.state = state;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMerchant() {
        return merchant;
    }

    public void setMerchant(String merchant) {
        this.merchant = merchant;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "PlaidTransaction{" +
                "eventID=" + eventID +
                ", transactionID='" + transactionID + '\'' +
                ", state=" + state +
                ", date=" + date +
                ", category='" + category + '\'' +
                ", amount=" + amount +
                '}';
    }
}
