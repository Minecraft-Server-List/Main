package com.example.backend.domain.category.service;

import com.example.backend.domain.category.dto.CategoryResponseDto;
import com.example.backend.domain.category.entity.CategoryEntity;
import com.example.backend.domain.category.repository.CategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository; // 가짜 레포지토리

    @InjectMocks
    private CategoryService categoryService; // 가짜 레포지토리가 주입된 서비스

    // 1-1. 카테고리 전체 조회
    @Test
    @DisplayName("카테고리 전체 조회 - 성공")
    void getAllCategoriesTest() {
        // given: 이런 데이터가 있다고 가정하자 (Mocking)
        List<CategoryEntity> categories = List.of(
                new CategoryEntity(1L, "RPG"),
                new CategoryEntity(2L, "생존")
        );
        given(categoryRepository.findAll()).willReturn(categories);

        // when: 실제 서비스를 실행하면
        List<CategoryResponseDto> result = categoryService.getAllCategories();

        // then: 결과가 우리가 예상한 대로인가?
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("RPG");
    }
}