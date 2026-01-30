package com.example.backend.domain.post.service;

import com.example.backend.domain.category.entity.CategoryEntity;
import com.example.backend.domain.category.repository.CategoryRepository;
import com.example.backend.domain.post.dto.PostRequestDto;
import com.example.backend.domain.post.dto.PostResponseDto;
import com.example.backend.domain.post.entity.PostEntity;
import com.example.backend.domain.post.repository.PostClassificationRepository;
import com.example.backend.domain.post.repository.PostRepository;
import com.example.backend.domain.user.entity.UserEntity;
import com.example.backend.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private PostClassificationRepository postClassificationRepository;

    @InjectMocks
    private PostService postService;

    // // 1. 게시글 생성 테스트
    @Test
    @DisplayName("게시글 생성 - 성공 (카테고리 포함)")
    void createPost_Success() {
        // given
        Long userId = 1L;
        UserEntity user = UserEntity.builder().userId(userId).name("유시영").build();
        CategoryEntity category = CategoryEntity.builder().categoryId(10L).name("자유게시판").build();

        PostRequestDto request = new PostRequestDto("테스트 제목", "테스트 내용", List.of(10L));
        PostEntity savedPost = PostEntity.builder().postId(100L).user(user).title("테스트 제목").build();

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(postRepository.save(any(PostEntity.class))).willReturn(savedPost);
        given(categoryRepository.findById(10L)).willReturn(Optional.of(category));

        // when
        PostResponseDto result = postService.createPost(request, userId);

        // then
        assertThat(result.getTitle()).isEqualTo("테스트 제목");
        assertThat(result.getAuthorName()).isEqualTo("유시영");
        verify(postRepository, times(1)).save(any(PostEntity.class));
        verify(postClassificationRepository, times(1)).save(any());
    }

    // // 2. 게시글 상세 조회 테스트 (조회수 증가 확인)
    @Test
    @DisplayName("게시글 상세 조회 - 성공 및 조회수 증가")
    void getPost_Success() {
        // given
        Long postId = 100L;
        UserEntity user = UserEntity.builder().name("유시영").build();
        PostEntity post = PostEntity.builder()
                .postId(postId)
                .user(user)
                .title("조회수 테스트")
                .viewCount(0)
                .build();

        given(postRepository.findById(postId)).willReturn(Optional.of(post));

        // when
        PostResponseDto result = postService.getPost(postId);

        // then
        assertThat(result.getTitle()).isEqualTo("조회수 테스트");
        assertThat(post.getViewCount()).isEqualTo(1); // // 서비스 로직 내 increaseViewCount() 검증
    }

    // // 3. 게시글 전체 조회 테스트
    @Test
    @DisplayName("게시글 전체 목록 조회 - 성공")
    void getAllPosts_Success() {
        // given
        UserEntity user = UserEntity.builder().name("유시영").build();
        List<PostEntity> posts = List.of(
                PostEntity.builder().postId(1L).user(user).title("제목1").build(),
                PostEntity.builder().postId(2L).user(user).title("제목2").build()
        );
        given(postRepository.findAll()).willReturn(posts);

        // when
        List<PostResponseDto> result = postService.getAllPosts();

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getTitle()).isEqualTo("제목1");
    }
}