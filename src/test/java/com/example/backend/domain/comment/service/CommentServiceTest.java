package com.example.backend.domain.comment.service;

import com.example.backend.domain.comment.dto.CommentRequestDto;
import com.example.backend.domain.comment.dto.CommentResponseDto;
import com.example.backend.domain.comment.entity.CommentEntity;
import com.example.backend.domain.comment.repository.CommentRepository;
import com.example.backend.domain.post.entity.PostEntity;
import com.example.backend.domain.post.repository.PostRepository;
import com.example.backend.domain.user.entity.UserEntity;
import com.example.backend.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CommentService commentService;

    // 1. 일반 댓글 작성 성공 테스트
    @Test
    @DisplayName("댓글 생성 시 작성자 이름과 내용이 정확해야 한다")
    void createComment_Success() {
        // given
        Long postId = 1L;
        Long userId = 1L;
        UserEntity user = UserEntity.builder().userId(userId).name("유시영").build();
        PostEntity post = PostEntity.builder().postId(postId).build();
        CommentRequestDto request = new CommentRequestDto("안녕하세요", null);

        CommentEntity savedComment = CommentEntity.builder()
                .commentId(10L)
                .content("안녕하세요")
                .user(user)
                .post(post)
                .children(new ArrayList<>())
                .build();

        given(postRepository.findById(postId)).willReturn(Optional.of(post));
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(commentRepository.save(any(CommentEntity.class))).willReturn(savedComment);

        // when
        CommentResponseDto result = commentService.createComment(postId, userId, request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEqualTo("안녕하세요");
        assertThat(result.getAuthorName()).isEqualTo("유시영");
    }

    // 2. 대댓글 작성 및 연관관계 테스트
    @Test
    @DisplayName("대댓글 생성 시 부모 댓글 정보가 포함되어야 한다")
    void createReply_Success() {
        // given
        Long parentId = 10L;
        UserEntity user = UserEntity.builder().name("길동").build();
        CommentEntity parentComment = CommentEntity.builder().commentId(parentId).build();
        CommentRequestDto request = new CommentRequestDto("대댓글입니다", parentId);

        given(postRepository.findById(any())).willReturn(Optional.of(PostEntity.builder().build()));
        given(userRepository.findById(any())).willReturn(Optional.of(user));
        given(commentRepository.findById(parentId)).willReturn(Optional.of(parentComment));

        CommentEntity reply = CommentEntity.builder()
                .commentId(11L)
                .content("대댓글입니다")
                .user(user)
                .parent(parentComment)
                .children(new ArrayList<>())
                .build();

        given(commentRepository.save(any(CommentEntity.class))).willReturn(reply);

        // when
        CommentResponseDto result = commentService.createComment(1L, 2L, request);

        // then
        assertThat(result.getContent()).contains("대댓글");
        assertThat(result.getCommentId()).isEqualTo(11L);
        verify(commentRepository).findById(parentId); // 부모 댓글 조회 여부 검증
    }

    // 3. 댓글 수정 검증
    @Test
    @DisplayName("댓글 수정 시 내용이 변경되어야 한다")
    void updateComment_Success() {
        // given
        Long commentId = 5L;
        CommentEntity comment = CommentEntity.builder()
                .commentId(commentId)
                .content("원래 내용")
                .user(UserEntity.builder().name("유시영").build())
                .children(new ArrayList<>())
                .build();
        CommentRequestDto request = new CommentRequestDto("수정된 내용", null);

        given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));

        // when
        CommentResponseDto result = commentService.updateComment(commentId, request);

        // then
        assertThat(result.getContent()).isEqualTo("수정된 내용");
        assertThat(result.getContent()).isNotEqualTo("원래 내용");
    }

    // 4. 댓글 삭제 검증
    @Test
    @DisplayName("댓글 삭제 요청 시 레포지토리의 삭제 메서드가 호출되어야 한다")
    void deleteComment_Success() {
        // given
        Long commentId = 7L;

        // when
        commentService.deleteComment(commentId);

        // then
        verify(commentRepository).deleteById(commentId);
    }
}