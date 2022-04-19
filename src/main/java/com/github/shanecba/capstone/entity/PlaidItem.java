package com.github.shanecba.capstone.entity;

import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Entity
@Table(name = "item")
public class PlaidItem {
    @Id
    @Column(name = "item_id")
    private String itemId;

    @ManyToOne(cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
    @JoinColumn(name = "id_user")
    private User user;

    @Column(name = "access_token")
    private String accessToken;

    @Column(name = "last_update")
    private LocalDateTime lastUpdate;

    @OneToMany(mappedBy = "item", fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.DETACH,
            CascadeType.REFRESH })
    @MapKey(name="accountId")
    private Map<String, Account> accountList;

    public PlaidItem() {
        accountList = new HashMap<>();
    }

    public PlaidItem(User user, String itemId, String accessToken) {
        this();
        this.user = user;
        this.itemId = itemId;
        this.accessToken = accessToken;
        this.lastUpdate = LocalDateTime.now();
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Map<String, Account> getAccountMap() {
        return accountList;
    }

    public void setAccountList(Map<String, Account> accountList) {
        this.accountList = accountList;
    }

    @Override
    public String toString() {
        return "PlaidItem{" +
                "itemId='" + itemId + '\'' +
                ", accessToken='" + accessToken + '\'' +
                ", lastUpdate=" + lastUpdate +
                ", accountList=" + accountList +
                '}';
    }
}
