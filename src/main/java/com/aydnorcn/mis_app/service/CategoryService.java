package com.aydnorcn.mis_app.service;

import com.aydnorcn.mis_app.dto.PageResponseDto;
import com.aydnorcn.mis_app.dto.category.CreateCategoryRequest;
import com.aydnorcn.mis_app.entity.Category;
import com.aydnorcn.mis_app.exception.ResourceNotFoundException;
import com.aydnorcn.mis_app.repository.CategoryRepository;
import com.aydnorcn.mis_app.utils.MessageConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Cacheable(value = "category", key = "#categoryId")
    public Category getCategoryById(String categoryId){
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageConstants.CATEGORY_NOT_FOUND));
    }

    public PageResponseDto<Category> getCategories(int pageNo, int pageSize){
        Page<Category> page = categoryRepository.findAll(PageRequest.of(pageNo, pageSize));

        return new PageResponseDto<>(page);
    }

    public Category createCategory(CreateCategoryRequest request){
        String categoryName = request.getName().toUpperCase(Locale.ENGLISH);

        checkCategoryNameExists(categoryName);

        Category category = new Category();
        category.setName(categoryName);
        return categoryRepository.save(category);
    }

    @CachePut(value = "category", key = "#categoryId")
    public Category updateCategory(String categoryId, CreateCategoryRequest request){
        String categoryName = request.getName().toUpperCase(Locale.ENGLISH);

        checkCategoryNameExists(categoryName);

        Category category = getCategoryById(categoryId);
        category.setName(categoryName);
        return categoryRepository.save(category);
    }

    @CacheEvict(value = "category", key = "#categoryId")
    public void deleteCategory(String categoryId){
        Category category = getCategoryById(categoryId);
        categoryRepository.delete(category);
    }

    private void checkCategoryNameExists(String categoryName){
        if(categoryRepository.existsByName(categoryName)){
            throw new ResourceNotFoundException(MessageConstants.CATEGORY_ALREADY_EXISTS);
        }
    }
}