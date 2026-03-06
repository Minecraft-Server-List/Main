package com.example.backend.domain.post.service;

import com.example.backend.domain.category.entity.CategoryEntity;
import com.example.backend.domain.category.repository.CategoryRepository;
import com.example.backend.domain.post.dto.PostRequestDto;
import com.example.backend.domain.post.dto.PostResponseDto;
import com.example.backend.domain.post.entity.PostClassificationEntity;
import com.example.backend.domain.post.entity.PostEntity;
import com.example.backend.domain.post.repository.PostClassificationRepository;
import com.example.backend.domain.post.repository.PostRepository;
import com.example.backend.domain.user.entity.UserEntity;
import com.example.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final PostClassificationRepository postClassificationRepository;

    @Transactional
    public PostResponseDto createPost(PostRequestDto requestDto, Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        PostEntity post = postRepository.save(PostEntity.builder()
                .user(user)
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .build());

        if (requestDto.getCategoryIds() != null) {
            requestDto.getCategoryIds().forEach(id -> {
                CategoryEntity category = categoryRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("카테고리 없음: " + id));
                postClassificationRepository.save(PostClassificationEntity.builder()
                        .post(post).category(category).build());
            });
        }
        return PostResponseDto.from(post);
    }

    public List<PostResponseDto> getAllPosts() {
        return postRepository.findAll().stream()
                .map(PostResponseDto::from)
                .toList();
    }

    @Transactional
    public PostResponseDto getPost(Long postId) {
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글 없음"));
        post.increaseViewCount();
        return PostResponseDto.from(post);
    }

    @Transactional
    public PostResponseDto updatePost(Long postId, PostRequestDto requestDto) {
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        post.update(requestDto.getTitle(), requestDto.getContent());

        if (requestDto.getCategoryIds() != null) {
            post.getPostClassifications().clear();
            requestDto.getCategoryIds().forEach(id -> {
                CategoryEntity category = categoryRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("카테고리 없음"));
                postClassificationRepository.save(PostClassificationEntity.builder()
                        .post(post).category(category).build());
            });
        }
        return PostResponseDto.from(post);
    }

    // 4. 게시글 삭제
    @Transactional
    public void deletePost(Long postId) {
        postRepository.deleteById(postId);
    }
}
