package com.github.shanecba.capstone.entity;

import org.springframework.stereotype.Component;

import javax.persistence.*;

@Component
@Entity
@Table(name = "inventory")
public class Inventory {
    @Id
    @Column(name = "id_inventory")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
    @JoinColumn(name = "id_user")
    private User user;

    @ManyToOne(cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
    @JoinColumn(name = "id_transaction")
    private Transaction transaction;

    @ManyToOne(cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
    @JoinColumn(name = "id_inventory_item")
    private InventoryItem item;

    public Inventory() {

    }

    public Inventory(User user, InventoryItem item) {
        this.user = user;
        this.item = item;
    }

    public Inventory(User user, Transaction transaction, InventoryItem item) {
        this.user = user;
        this.transaction = transaction;
        this.item = item;
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

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public InventoryItem getItem() {
        return item;
    }

    public void setItem(InventoryItem item) {
        this.item = item;
    }

    @Override
    public String toString() {
        return "Inventory{" +
                "id=" + id +
                ", item=" + item +
                '}';
    }
}
