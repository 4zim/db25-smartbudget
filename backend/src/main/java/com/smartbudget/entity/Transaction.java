package com.smartbudget.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * PROVIDED – JPA entity for transactions table.
 *
 * TICKET-F014 (Day 2): Write a plain Java POJO version in com.smartbudget.model
 * TICKET-F048 (Day 5): Study @ManyToOne, @JoinColumn, @Positive
 *
 * STRETCH GOAL (Day 2): Add a Builder pattern to your POJO:
 *   new Transaction.Builder().withAmount(100).withCategory("Food").build()
 */
@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long txnId;

    @JsonIgnoreProperties({"transactions", "savingsGoals"})
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @JsonIgnoreProperties({"transactions"})
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than zero")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "txn_date", nullable = false)
    private LocalDate txnDate = LocalDate.now();

    @Column(length = 255)
    private String description;

    @NotBlank
    @Column(nullable = false, length = 10)
    private String type;

    public Transaction() {}

    public Long getTxnId()                         { return txnId; }
    public void setTxnId(Long txnId)               { this.txnId = txnId; }
    public User getUser()                          { return user; }
    public void setUser(User user)                 { this.user = user; }
    public Category getCategory()                  { return category; }
    public void setCategory(Category category)     { this.category = category; }
    public BigDecimal getAmount()                  { return amount; }
    public void setAmount(BigDecimal amount)       { this.amount = amount; }
    public LocalDate getTxnDate()                  { return txnDate; }
    public void setTxnDate(LocalDate txnDate)      { this.txnDate = txnDate; }
    public String getDescription()                 { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getType()                        { return type; }
    public void setType(String type)               { this.type = type; }

    @Override
    public String toString() {
        return String.format("Transaction[id=%d, amount=%.2f, type=%s, date=%s]", txnId, amount, type, txnDate);
    }
}
