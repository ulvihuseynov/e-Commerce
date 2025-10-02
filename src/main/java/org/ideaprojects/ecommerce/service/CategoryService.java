package org.ideaprojects.ecommerce.service;

import org.ideaprojects.ecommerce.model.Category;

import java.util.List;

public interface CategoryService {

    List<Category> getAllCategories();
    String createCategory( Category category);
    String deleteCategory(Long categoryId);
}
