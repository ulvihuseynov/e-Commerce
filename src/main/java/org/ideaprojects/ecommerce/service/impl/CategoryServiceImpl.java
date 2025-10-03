package org.ideaprojects.ecommerce.service.impl;

import org.ideaprojects.ecommerce.exceptions.APIException;
import org.ideaprojects.ecommerce.exceptions.ResourceNotFoundException;
import org.ideaprojects.ecommerce.model.Category;
import org.ideaprojects.ecommerce.payload.CategoryDTO;
import org.ideaprojects.ecommerce.payload.CategoryResponse;
import org.ideaprojects.ecommerce.repository.CategoryRepository;
import org.ideaprojects.ecommerce.service.CategoryService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository, ModelMapper modelMapper) {
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }

    public CategoryResponse getAllCategories(Integer pageSize, Integer pageNumber,String sortBy,String sortOrder){

        Sort sortByAndOrder=sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();

        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize,sortByAndOrder);
        Page<Category> categoryPage = categoryRepository.findAll(pageRequest);
        List<Category> categories = categoryPage.getContent();
        System.out.println(categoryPage);
        if (categories.isEmpty()) {
            throw new APIException("No category created till now");

        }
        CategoryResponse categoryResponse=new CategoryResponse();
        List<CategoryDTO> categoryDTOList = categories.stream().map(category -> modelMapper.map(category, CategoryDTO.class))
                .toList();
        categoryResponse.setContent(categoryDTOList);
        categoryResponse.setPageNumber(categoryPage.getNumber());
        categoryResponse.setPageSize(categoryPage.getSize());
        categoryResponse.setTotalElements(categoryPage.getTotalElements());
        categoryResponse.setTotalPage(categoryPage.getTotalPages());
        categoryResponse.setLastPage(categoryPage.isLast());
        return categoryResponse;
    }

    @Override
    public CategoryDTO createCategory( CategoryDTO categoryDTO){

        Category category = modelMapper.map(categoryDTO, Category.class);
        Category categoryDB= categoryRepository.findByCategoryName(category.getCategoryName());
        if (categoryDB != null) {
            throw new APIException("Category with the name " + category.getCategoryName() +" already exist!");
        }
        Category savedCategory = categoryRepository.save(category);

        return modelMapper.map(savedCategory,CategoryDTO.class);
    }

    @Override
    public CategoryDTO deleteCategory(Long categoryId) {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException( "Category", "categoryId",categoryId));
        categoryRepository.delete(category);
        return modelMapper.map(category,CategoryDTO.class);
    }

    @Override
    public CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId) {

        Category category = modelMapper.map(categoryDTO, Category.class);
        Category categoryDB = categoryRepository.findById(categoryId)
                .orElseThrow(()->new  ResourceNotFoundException( "Category", "categoryId",categoryId));

        categoryDB.setCategoryName(category.getCategoryName());

        Category savedCategory = categoryRepository.save(categoryDB);

        return modelMapper.map(savedCategory,CategoryDTO.class) ;
    }
}
