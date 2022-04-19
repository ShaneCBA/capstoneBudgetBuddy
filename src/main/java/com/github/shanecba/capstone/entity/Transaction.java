package com.github.shanecba.capstone.entity;


import org.springframework.stereotype.Component;

import javax.persistence.*;

@Component
@Entity
@Table(name = "transaction")
public class Transaction {
    @Id
    @Column(name = "id_transaction")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
    @JoinColumn(name = "id_user")
    private User user;

    @ManyToOne(cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
    @JoinColumn(name = "id_goal")
    private Goal goal;

    @Column(name = "amount")
    private int amount;

    @OneToOne
    @JoinColumn(name = "event_id", referencedColumnName = "event_id")
    private PlaidTransaction plaidTransaction;

    public Transaction() {

    }

    public Transaction(User user, Goal goal) {
        this.id = id;
        this.user = user;
        this.goal = goal;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Goal getGoal() {
        return goal;
    }

    public void setGoal(Goal goal) {
        this.goal = goal;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", goal=" + goal +
                ", amount=" + amount +
                ", plaidTransaction=" + plaidTransaction +
                '}';
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public PlaidTransaction getPlaidTransaction() {
        return plaidTransaction;
    }

    public void setPlaidTransaction(PlaidTransaction plaidTransaction) {
        this.plaidTransaction = plaidTransaction;
    }

}
