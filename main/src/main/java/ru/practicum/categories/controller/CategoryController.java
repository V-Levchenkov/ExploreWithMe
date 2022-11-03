package ru.practicum.categories.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.categories.service.CategoryService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/categories/{categoryId}")
    public CategoryDto getById(@PathVariable Long categoryId) {
        return categoryService.getCategoryById(categoryId);
    }

    @GetMapping("/categories")
    public List<CategoryDto> getAll(@PositiveOrZero @RequestParam(value = "from", defaultValue = "0") Integer from,
                                    @Positive @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return categoryService.getAllCategoryList(from, size);
    }

    @PostMapping("/admin/categories")
    public CategoryDto add(@Valid @RequestBody CategoryDto categoryDto) {
        return categoryService.addCategory(categoryDto);
    }

    @PatchMapping("/admin/categories")
    public CategoryDto update(@Valid @RequestBody CategoryDto categoryDto) {
        return categoryService.updateCategory(categoryDto);
    }

    @DeleteMapping("/admin/categories/{categoryId}")
    public void remove(@PathVariable Long categoryId) {
        categoryService.removeCategoryById(categoryId);
    }

}

