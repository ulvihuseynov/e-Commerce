package org.ideaprojects.ecommerce.service;

import org.ideaprojects.ecommerce.model.Category;
import org.ideaprojects.ecommerce.payload.CategoryDTO;
import org.ideaprojects.ecommerce.payload.CategoryResponse;

import java.util.List;

public interface CategoryService {

    CategoryResponse getAllCategories(Integer pageSize, Integer pageNumber);
    CategoryDTO createCategory( CategoryDTO categoryDTO);
    CategoryDTO deleteCategory(Long categoryId);

    CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId);
}
