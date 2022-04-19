package com.github.shanecba.capstone.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.shanecba.capstone.types.PetCondition;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.time.DayOfWeek;
import java.time.LocalDate;

import static java.time.temporal.ChronoUnit.DAYS;

@Component
@Entity
@Table(name = "pet")
@DynamicInsert
public class Pet {
    @Id
    @Column(name = "id_pet")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name", nullable = false)
    private String name;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_user", referencedColumnName = "id_user", nullable = false)
    @JsonIgnore
    private User user;

    @Column(name = "happiness")
    private Integer happiness = Math.max(7 - (int) DAYS.between(
            LocalDate.now().minusDays(1).with(DayOfWeek.FRIDAY),
            LocalDate.now()), 0);

    @Column(name = "satiation")
    private Integer satiation = Math.max(7 - (int) DAYS.between(
            LocalDate.now().minusDays(1).with(DayOfWeek.FRIDAY),
            LocalDate.now()), 0);

    @Enumerated(EnumType.STRING)
    @Column(name = "pet_condition")
    private PetCondition condition = PetCondition.NORMAL;

    public Pet() {
    }

    public Pet(String name, User user) {
        this.name = name;
        this.user = user;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getHappiness() {
        return happiness;
    }

    public void setHappiness(int happiness) {
        this.happiness = happiness;
    }

    //TODO replace boolean with an exception
    public boolean incrementHappiness() {
        if (happiness >= 7)
            return false;
        happiness += 1;
        return true;
    }

    public int getSatiation() {
        return satiation;
    }

    public void setSatiation(int satiation) {
        this.satiation = satiation;
    }

    //TODO replace boolean with an exception
    public boolean incrementSatiation() {
        if (satiation >= 7)
            return false;
        satiation += 1;
        return true;
    }

    public PetCondition getCondition() {
        return condition;
    }

    public void setCondition(PetCondition condition) {
        this.condition = condition;
    }

    public void update(Pet newPet) {
        this.setName(newPet.getName());
    }

    @Override
    public String toString() {
        return "Pet{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", happiness=" + happiness +
                ", satiation=" + satiation +
                ", condition=" + condition +
                '}';
    }
}
