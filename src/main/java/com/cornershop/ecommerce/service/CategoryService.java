package com.cornershop.ecommerce.service;

import com.cornershop.ecommerce.exception.CategoryDuplicateException;
import com.cornershop.ecommerce.exception.CategoryNotFoundException;
import com.cornershop.ecommerce.model.Category;
import com.cornershop.ecommerce.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public Category createCategory(Category category) {
        Optional <Category> optionalCategory = categoryRepository.findCategoryByName(category.getName());
        if (optionalCategory.isPresent()) {
            throw new CategoryDuplicateException("Category is already defined : " + category.getName());
        }
        return categoryRepository.save(category);
    }

    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    public Category getCategory(Long id) {
        return categoryRepository.findById(id).orElseThrow( () -> new CategoryNotFoundException("Category not found id : " + id));
    }

    public List<Category> getAllCategoryList() {
        return categoryRepository.findAll();
    }

}
