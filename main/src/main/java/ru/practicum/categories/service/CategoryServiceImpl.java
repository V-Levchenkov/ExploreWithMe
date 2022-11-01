package ru.practicum.categories.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.categories.dto.CategoryMapper;
import ru.practicum.categories.model.Category;
import ru.practicum.categories.storage.CategoryRepository;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.utilits.PageableRequest;

import java.util.List;

@AllArgsConstructor
@Slf4j
@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;
    private final CategoryRepository categoryRepository;

    @Override
    public List<CategoryDto> getAllCategoryList(Integer from, Integer size) {
        List<Category> categories = categoryRepository.findAll(getPageable(from, size)).getContent();
        log.info("Получен список всех категорий категорий");
        return categoryMapper.toCategoryDto(categories);
    }

    @Override
    public CategoryDto getCategoryById(Long categoryId) {
        Category category = categoryValidate(categoryId);
        log.info("Получена категория с id: {}", categoryId);
        return categoryMapper.toCategoryDto(category);
    }

    @Override
    public CategoryDto updateCategory(CategoryDto categoryDto) {
        Category category = categoryValidate(categoryDto.getId());
        category.setName(categoryDto.getName());
        log.info("Обновлена категория: {}", category);
        return categoryMapper.toCategoryDto(category);
    }

    @Override
    public CategoryDto addCategory(CategoryDto categoryDto) {
        Category category = categoryRepository.save(categoryMapper.fromCategoryDto(categoryDto));
        log.info("Добавлена категория: {}", category);
        return categoryMapper.toCategoryDto(category);
    }

    @Override
    public void removeCategoryById(Long categoryId) {
        categoryValidate(categoryId);
        categoryRepository.deleteById(categoryId);
        log.info("Категория с id: {} удалена", categoryId);
    }

    private Category categoryValidate(Long categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(() ->
                new NotFoundException("Категория с id: {} не найдена", categoryId));
    }

    private Pageable getPageable(Integer from, Integer size) {
        return new PageableRequest(from, size, Sort.unsorted());
    }
}
