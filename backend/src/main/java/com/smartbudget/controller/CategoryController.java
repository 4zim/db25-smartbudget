package com.smartbudget.controller;

import com.smartbudget.entity.Category;
import com.smartbudget.repository.CategoryRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/** PROVIDED – returns all categories. Used by the React category dropdown. */
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryRepository repo;

    public CategoryController(CategoryRepository repo) { this.repo = repo; }

    @GetMapping
    public List<Category> getAll() { return repo.findAll(); }
}
