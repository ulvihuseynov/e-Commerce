package org.ideaprojects.ecommerce.service.impl;

import org.ideaprojects.ecommerce.model.Category;
import org.ideaprojects.ecommerce.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private List<Category> categories=new ArrayList<>();

    private Long nextId=1L;

    public List<Category> getAllCategories(){

        return categories;
    }
    public String createCategory( Category category){

        category.setCategoryId(nextId++);
        categories.add(category);

        return "added categories successfully";
    }

    @Override
    public String deleteCategory(Long categoryId) {
        Category category = categories.stream().filter(c -> c.getCategoryId().equals(categoryId))
                .findFirst().orElse(null);
        if (category == null) {
            return "Category not found";
        }
        categories.remove(category);
        return "Category with categoryId " + categoryId + " deleted successfully";
    }
}
