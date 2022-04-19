package com.github.shanecba.capstone.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Entity
@Table(name = "user")
public class User {
    @Id
    @Column(name = "id_user")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "sub")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String sub;

    @Column(name = "username")
    private String username;

    @OneToOne(mappedBy = "user")
    private Pet pet;

    @Column(name = "points")
    private int points = 100;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.DETACH,
            CascadeType.REFRESH })
    @MapKey(name="itemId")
    @JsonIgnore
    private Map<String, PlaidItem> items;

    @OneToOne(mappedBy = "user")
    private Goal goal;

    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST,
            CascadeType.REFRESH })
    @JoinTable(
            name = "inventory",
            joinColumns = @JoinColumn(name = "id_user"),
            inverseJoinColumns = @JoinColumn(name = "id_inventory_item")
    )
    @JsonIgnore
    private List<InventoryItem> inventoryAsItems;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Inventory> inventory;

    public User() {
        this.items = new HashMap<>();
        this.inventoryAsItems = new ArrayList<>();
        this.inventory = new ArrayList<>();
    }

    public User(String sub, String username) {
        this();
        this.sub = sub;
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }

    public Map<String, PlaidItem> getItems() {
        return items;
    }

    public void setItems(Map<String, PlaidItem> items) {
        this.items = items;
    }

    public void addItem(PlaidItem item) {
        this.items.put(item.getItemId(), item);
    }

    public boolean hasItem(String itemId) {
        return items.get(itemId) != null;
    }

    public Goal getGoal() {
        return goal;
    }

    public void setGoal(Goal goal) {
        this.goal = goal;
    }

    public int countItems() {
        return items.size();
    }

    public List<Inventory> getInventory() {
        return inventory;
    }

    public void setInventory(List<Inventory> inventory) {
        this.inventory = inventory;
    }

    public List<InventoryItem> getInventoryAsItems() {
        return inventoryAsItems;
    }

    public void setInventoryAsItems(List<InventoryItem> inventoryAsItems) {
        this.inventoryAsItems = inventoryAsItems;
    }

    public void removeItemToInventory(InventoryItem inventoryItem) {
        this.inventoryAsItems.remove(inventoryAsItems);
    }

    public void addItemToInventory(InventoryItem inventory) {
        this.inventoryAsItems.add(inventory);
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void deductPoints(int points) {
        this.points -= points;
    }

    public void addPoints(int points) {
        this.points += points;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", sub='" + sub + '\'' +
                ", username='" + username + '\'' +
                ", pet=" + pet +
                ", items=" + items +
                ", inventory=" + inventoryAsItems +
                ", points=" + points +
                '}';
    }
}
