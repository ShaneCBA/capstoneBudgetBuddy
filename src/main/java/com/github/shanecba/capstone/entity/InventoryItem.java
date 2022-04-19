package com.github.shanecba.capstone.entity;

import com.github.shanecba.capstone.types.ItemCategory;
import org.springframework.stereotype.Component;

import javax.persistence.*;

@Component
@Entity
@Table(name = "inventory_item")
public class InventoryItem {
    @Id
    @Column(name = "id_inventory_item")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private ItemCategory category;

    @Column(name = "cost")
    private int cost;

    public InventoryItem() {

    }

    public InventoryItem(String name, ItemCategory category) {
        this.name = name;
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ItemCategory getCategory() {
        return category;
    }

    public void setCategory(ItemCategory category) {
        this.category = category;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    @Override
    public String toString() {
        return "InventoryItem{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", category=" + category +
                '}';
    }
}
