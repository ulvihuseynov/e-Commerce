package org.ideaprojects.ecommerce.controller;


import jakarta.validation.Valid;
import org.ideaprojects.ecommerce.model.Category;
import org.ideaprojects.ecommerce.payload.CategoryDTO;
import org.ideaprojects.ecommerce.payload.CategoryResponse;
import org.ideaprojects.ecommerce.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/public/categories")
    //@RequestMapping(value = "/public/categories",method=RequestMethod.GET)
    public ResponseEntity<CategoryResponse> getAllCategories(@RequestParam(name = "pageSize", defaultValue = "2", required = false) Integer pageSize,
                                                             @RequestParam(name = "pageNumber",defaultValue = "1", required = false) Integer pageNumber){

        return new ResponseEntity<>(categoryService.getAllCategories(pageSize,pageNumber),HttpStatus.OK);
    }

    @PostMapping("/public/categories")
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO categoryDTO){

        return new ResponseEntity<>(  categoryService.createCategory(categoryDTO),HttpStatus.CREATED);
    }

    @DeleteMapping("/admin/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> deleteCategory(@PathVariable Long categoryId){

            return new ResponseEntity<>(categoryService.deleteCategory(categoryId),HttpStatus.OK);


    }

    @PutMapping("/public/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> updateCategory(@Valid @RequestBody CategoryDTO categoryDTO,
                                                 @PathVariable Long categoryId){

            return new ResponseEntity<>(categoryService.updateCategory(categoryDTO,categoryId),HttpStatus.OK);


    }
}
