package com.smartbudget.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * PROVIDED – JPA entity for users table.
 *
 * TICKET-F012 (Day 2): Write a plain Java POJO version in com.smartbudget.model
 *   (fields: id, name, email — no JPA annotations, just getters/setters/toString)
 *
 * TICKET-F046 (Day 5): Study how @Entity, @Table, @Id, @GeneratedValue,
 *   @Column, @OneToMany, @JsonIgnore, @NotBlank work together.
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @NotBlank(message = "Name is required")
    @Column(nullable = false, length = 100)
    private String name;

    @Email(message = "Valid email required")
    @NotBlank(message = "Email is required")
    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transaction> transactions = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SavingsGoal> savingsGoals = new ArrayList<>();

    public User() {}
    public User(String name, String email) { this.name = name; this.email = email; }

    public Long getUserId()                    { return userId; }
    public void setUserId(Long userId)         { this.userId = userId; }
    public String getName()                    { return name; }
    public void setName(String name)           { this.name = name; }
    public String getEmail()                   { return email; }
    public void setEmail(String email)         { this.email = email; }
    public LocalDateTime getCreatedAt()        { return createdAt; }
    public List<Transaction> getTransactions() { return transactions; }
    public List<SavingsGoal> getSavingsGoals() { return savingsGoals; }

    @Override
    public String toString() {
        return String.format("User[id=%d, name=%s, email=%s]", userId, name, email);
    }
}
