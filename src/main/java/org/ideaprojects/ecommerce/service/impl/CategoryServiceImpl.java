package org.ideaprojects.ecommerce.service.impl;

import org.ideaprojects.ecommerce.exceptions.APIException;
import org.ideaprojects.ecommerce.exceptions.ResourceNotFoundException;
import org.ideaprojects.ecommerce.model.Category;
import org.ideaprojects.ecommerce.repository.CategoryRepository;
import org.ideaprojects.ecommerce.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;


    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> getAllCategories(){

        List<Category> categories = categoryRepository.findAll();
        if (categories.isEmpty()) {
            throw new APIException("No category created till now");

        }
        return categories;
    }

    @Override
    public String createCategory( Category category){

       Category categoryDB= categoryRepository.findByCategoryName(category.getCategoryName());
        if (categoryDB != null) {
            throw new APIException("Category with the name " + category.getCategoryName() +" already exist!");
        }
        categoryRepository.save(category);
        return "added categories successfully";
    }

    @Override
    public String deleteCategory(Long categoryId) {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException( "Category", "categoryId",categoryId));
        categoryRepository.delete(category);
        return "Category with categoryId " + categoryId + " deleted successfully";
    }

    @Override
    public String updateCategory(Category category, Long categoryId) {

        Category categoryDB = categoryRepository.findById(categoryId)
                .orElseThrow(()->new  ResourceNotFoundException( "Category", "categoryId",categoryId));

        categoryDB.setCategoryName(category.getCategoryName());

        Category savedCategory = categoryRepository.save(categoryDB);

        return "category updated successfully " + savedCategory ;
    }
}
