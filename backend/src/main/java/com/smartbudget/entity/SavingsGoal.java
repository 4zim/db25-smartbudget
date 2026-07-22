package com.smartbudget.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * PROVIDED – JPA entity for savings_goals table.
 *
 * TICKET-F015 (Day 2): Write a plain Java POJO version in com.smartbudget.model
 * TICKET-F049 (Day 5): Study @ManyToOne linking goal to User
 */
@Entity
@Table(name = "savings_goals")
public class SavingsGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long goalId;

    @JsonIgnoreProperties({"transactions", "savingsGoals"})
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank(message = "Goal name is required")
    @Column(nullable = false, length = 100)
    private String name;

    @NotNull @Positive(message = "Target amount must be > 0")
    @Column(name = "target_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal targetAmount;

    @Column(name = "current_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal currentAmount = BigDecimal.ZERO;

    @Column(name = "deadline")
    private LocalDate deadline;

    public SavingsGoal() {}

    public Long getGoalId()                              { return goalId; }
    public void setGoalId(Long goalId)                   { this.goalId = goalId; }
    public User getUser()                                { return user; }
    public void setUser(User user)                       { this.user = user; }
    public String getName()                              { return name; }
    public void setName(String name)                     { this.name = name; }
    public BigDecimal getTargetAmount()                  { return targetAmount; }
    public void setTargetAmount(BigDecimal targetAmount) { this.targetAmount = targetAmount; }
    public BigDecimal getCurrentAmount()                 { return currentAmount; }
    public void setCurrentAmount(BigDecimal a)           { this.currentAmount = a; }
    public LocalDate getDeadline()                       { return deadline; }
    public void setDeadline(LocalDate deadline)          { this.deadline = deadline; }

    @Override
    public String toString() {
        return String.format("SavingsGoal[id=%d, name=%s, target=%.2f]", goalId, name, targetAmount);
    }
}
