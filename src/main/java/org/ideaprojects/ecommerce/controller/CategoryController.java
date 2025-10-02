package org.ideaprojects.ecommerce.controller;


import org.ideaprojects.ecommerce.model.Category;
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
    public ResponseEntity<List<Category>> getAllCategories(){

        return new ResponseEntity<>(categoryService.getAllCategories(),HttpStatus.OK);
    }
    @PostMapping("/public/categories")
    public ResponseEntity<String> createCategory(@RequestBody Category category){

        return new ResponseEntity<>(  categoryService.createCategory(category),HttpStatus.CREATED);
    }

    @DeleteMapping("/admin/categories/{categoryId}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long categoryId){

        try{
            return new ResponseEntity<>(categoryService.deleteCategory(categoryId),HttpStatus.OK);

        } catch (ResponseStatusException e) {
            return new ResponseEntity<>(e.getReason(),e.getStatusCode());
        }
    }

    @PutMapping("/public/categories/{categoryId}")
    public ResponseEntity<String> updateCategory(@RequestBody Category category,
                                                 @PathVariable Long categoryId){

        try{
            return new ResponseEntity<>(categoryService.updateCategory(category,categoryId),HttpStatus.OK);

        }catch (ResponseStatusException e){
            return new ResponseEntity<>(e.getMessage(),e.getStatusCode());

        }
    }
}
