package com.smartbudget.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * PROVIDED – JPA entity for categories table.
 *
 * TICKET-F013 (Day 2): Write a plain Java POJO version in com.smartbudget.model
 * TICKET-F047 (Day 5): Study @Pattern(regexp) for type validation
 */
@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

    @NotBlank(message = "Category name is required")
    @Column(nullable = false, length = 50)
    private String name;

    @NotBlank
    @Pattern(regexp = "INCOME|EXPENSE", message = "Type must be INCOME or EXPENSE")
    @Column(nullable = false, length = 10)
    private String type;

    public Category() {}
    public Category(String name, String type) { this.name = name; this.type = type; }

    public Long getCategoryId()          { return categoryId; }
    public void setCategoryId(Long id)   { this.categoryId = id; }
    public String getName()              { return name; }
    public void setName(String name)     { this.name = name; }
    public String getType()              { return type; }
    public void setType(String type)     { this.type = type; }

    @Override
    public String toString() {
        return String.format("Category[id=%d, name=%s, type=%s]", categoryId, name, type);
    }
}
