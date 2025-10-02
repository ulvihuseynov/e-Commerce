package org.ideaprojects.ecommerce.controller;


import org.ideaprojects.ecommerce.model.Category;
import org.ideaprojects.ecommerce.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/api/public/categories")
    public List<Category> getAllCategories(){

        return categoryService.getAllCategories();
    }
    @PostMapping("/api/public/categories")
    public String createCategory(@RequestBody Category category){

        String status = categoryService.createCategory(category);

        return status;
    }

    @DeleteMapping("/api/admin/categories/{categoryId}")
    public String deleteCategory(@PathVariable Long categoryId){

        return categoryService.deleteCategory(categoryId);
    }
}
