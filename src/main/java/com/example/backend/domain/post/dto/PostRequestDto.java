package com.example.backend.domain.post.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor
public class PostRequestDto {
    private String title;
    private String content;
    private List<Long> categoryIds;
}