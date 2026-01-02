package com.example.backend.domain.category.service;

import com.example.backend.domain.category.dto.CategoryResponseDto;
import com.example.backend.domain.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public List<CategoryResponseDto> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(category -> new CategoryResponseDto(category.getCategoryId(), category.getName()))
                .collect(Collectors.toList());
    }
}
