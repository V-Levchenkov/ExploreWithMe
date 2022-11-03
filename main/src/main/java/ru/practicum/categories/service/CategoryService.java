package ru.practicum.categories.service;

import ru.practicum.categories.dto.CategoryDto;

import java.util.List;

public interface CategoryService {

    List<CategoryDto> getAllCategoryList(Integer from, Integer size);

    CategoryDto getCategoryById(Long categoryId);

    CategoryDto updateCategory(CategoryDto categoryDto);

    CategoryDto addCategory(CategoryDto categoryDto);

    void removeCategoryById(Long categoryId);
}
