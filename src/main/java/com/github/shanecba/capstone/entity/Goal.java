package com.github.shanecba.capstone.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.shanecba.capstone.types.GoalType;
import net.minidev.json.annotate.JsonIgnore;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Component
@Entity
@Table(name = "goal")
public class Goal {
    @Id
    @Column(name = "id_goal")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne
    @JoinColumn(name="id_user")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    User user;

    @OneToMany(mappedBy = "goal", fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.DETACH,
            CascadeType.REFRESH })
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<Transaction> transactions;

    @Column(name = "goal_type")
    @Enumerated(EnumType.STRING)
    private GoalType type;

    @Column(name = "start_date")
    private LocalDate startDate = LocalDate.now();

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "target")
    private int target;

    public Goal() {
    }

    public Goal(User user) {
        this.user = user;
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

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public GoalType getType() {
        return type;
    }

    public void setType(GoalType type) {
        this.type = type;
    }

    public GoalType[] getAllTypes() {
        return GoalType.values();
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }

    public void update(Goal newGoal) {
        setType(newGoal.getType());
        setTarget(newGoal.getTarget());
    }

    @Override
    public String toString() {
        return "Goal{" +
                "id=" + id +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", target=" + target +
                '}';
    }
}
