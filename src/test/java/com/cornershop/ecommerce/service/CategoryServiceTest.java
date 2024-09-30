package com.cornershop.ecommerce.service;

import com.cornershop.ecommerce.exception.CategoryDeleteException;
import com.cornershop.ecommerce.exception.CategoryDuplicateException;
import com.cornershop.ecommerce.exception.CategoryNotFoundException;
import com.cornershop.ecommerce.helper.CategoryDOFactory;
import com.cornershop.ecommerce.model.Category;
import com.cornershop.ecommerce.repository.CategoryRepository;
import com.cornershop.ecommerce.repository.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.parameters.P;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


public class CategoryServiceTest {

    @InjectMocks
    private CategoryService categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductRepository productRepository;

    private CategoryDOFactory categoryDOFactory;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        this.categoryDOFactory = new CategoryDOFactory();
    }

    @Test
    public void createCategory_successful() {
        Category category = categoryDOFactory.getCategoryWithoutId();

        Category savedCategory = categoryDOFactory.getCategoryWithId(1L);

        when(categoryRepository.findCategoryByName(category.getName())).thenReturn(Optional.empty());
        when(categoryRepository.save(category)).thenReturn(savedCategory);

        Category response = categoryService.createCategory(category);

        assertEquals(category.getName(), response.getName());
        verify(categoryRepository, times(1)).findCategoryByName(category.getName());
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    public void createCategory_fail() {
        Category category = categoryDOFactory.getCategoryWithoutId();

        Category savedCategory = categoryDOFactory.getCategoryWithId(1L);

        when(categoryRepository.findCategoryByName(category.getName())).thenReturn(Optional.ofNullable(savedCategory));

        CategoryDuplicateException thrown = Assertions.assertThrows(CategoryDuplicateException.class, () -> categoryService.createCategory(category));


        assertEquals("Category is already defined : " + category.getName(), thrown.getMessage());
        verify(categoryRepository, times(1)).findCategoryByName(category.getName());
        verify(categoryRepository, times(0)).save(category);
    }

    @Test
    public void deleteCategory_success() {
        Long categoryId = 3L;

        when(productRepository.getProductCountOfCategoryId(categoryId)).thenReturn(0L);

        // To test void method, use doNothing because it doesn't return any data.
        Mockito.doNothing().when(categoryRepository).deleteById(categoryId);

        categoryService.deleteCategory(categoryId);

        verify(productRepository, times(1)).getProductCountOfCategoryId(categoryId);
        verify(categoryRepository, times(1)).deleteById(categoryId);
    }

    @Test
    public void deleteCategory_fail() {
        Long categoryId = 3L;

        when(productRepository.getProductCountOfCategoryId(categoryId)).thenReturn(1L);

        CategoryDeleteException thrown = Assertions.assertThrows(CategoryDeleteException.class,
                () -> categoryService.deleteCategory(categoryId));

        assertEquals("you can not delete this category because category has 1 products", thrown.getMessage());
        verify(productRepository, times(1)).getProductCountOfCategoryId(categoryId);
        verify(categoryRepository, times(0)).deleteById(categoryId);
    }

    @Test
    public void getCategory_successful() {
        Long categoryId = 1L;
        Category category = categoryDOFactory.getCategoryWithId(categoryId);
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        Category response = categoryService.getCategory(categoryId);

        assertEquals(categoryId, response.getId());
        assertEquals(category.getName(), response.getName());
        verify(categoryRepository, times(1)).findById(categoryId);
    }

    @Test
    public void getCategory_fail() {
        Long categoryId = 1L;

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        CategoryNotFoundException thrown = Assertions.assertThrows(CategoryNotFoundException.class,
                () -> categoryService.getCategory(categoryId));

        assertEquals("Category not found id : 1", thrown.getMessage());
        verify(categoryRepository, times(1)).findById(categoryId);
    }

    @Test
    public void getAllCategoryList_successful() {
        List<Category> categoryList = categoryDOFactory.getCategoryListWithId();

        when(categoryRepository.findAll()).thenReturn(categoryList);

        List<Category> response = categoryService.getAllCategoryList();

        assertEquals(categoryList.size(), response.size());
        assertEquals(categoryList.get(0).getName(), response.get(0).getName());
        assertEquals(categoryList.get(0).getId(), response.get(0).getId());
        assertEquals(categoryList.get(2).getName(), response.get(2).getName());
        assertEquals(categoryList.get(2).getId(), response.get(2).getId());
        verify(categoryRepository, times(1)).findAll();
    }

    
}
