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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
    @DisplayName("✅ [성공] 댓글 생성: 작성자 이름과 내용이 정확하게 저장되어야 한다")
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
    @DisplayName("✅ [성공] 대댓글 생성: 부모 댓글 ID가 있으면 자식 댓글로 올바르게 연결되어야 한다")
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
        verify(commentRepository).findById(parentId); // 부모 댓글 조회 로직 호출 여부 확인
    }

    // 3. 댓글 수정 검증
    @Test
    @DisplayName("✅ [성공] 댓글 수정: 수정 요청 시 기존 내용이 새로운 내용으로 변경되어야 한다")
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
    @DisplayName("✅ [성공] 댓글 삭제: 삭제 시 레포지토리의 delete 메서드가 정상 호출되어야 한다")
    void deleteComment_Success() {
        // given
        Long commentId = 7L;

        // when
        commentService.deleteComment(commentId);

        // then
        verify(commentRepository).deleteById(commentId);
    }

    // --- 실패 케이스 ---

    // 1. 존재하지 않는 게시글 예외 테스트
    @Test
    @DisplayName("❌ [실패] 댓글 작성: 존재하지 않는 게시글 ID일 경우 예외가 발생한다")
    void createComment_Fail_PostNotFound() {
        // given
        Long postId = 999L;
        Long userId = 1L;
        CommentRequestDto request = new CommentRequestDto("내용", null);

        given(postRepository.findById(postId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> commentService.createComment(postId, userId, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("게시글 없음");
    }

    // 2. 존재하지 않는 부모 댓글 예외 테스트
    @Test
    @DisplayName("❌ [실패] 대댓글 작성: 부모 댓글 ID를 찾을 수 없을 경우 예외가 발생한다")
    void createReply_Fail_ParentCommentNotFound() {
        // given
        Long postId = 1L;
        Long userId = 1L;
        Long invalidParentId = 888L;
        CommentRequestDto request = new CommentRequestDto("대댓글", invalidParentId);

        given(postRepository.findById(postId)).willReturn(Optional.of(PostEntity.builder().build()));
        given(userRepository.findById(userId)).willReturn(Optional.of(UserEntity.builder().build()));
        given(commentRepository.findById(invalidParentId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> commentService.createComment(postId, userId, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("부모 댓글 없음");
    }

    // 3. 존재하지 않는 댓글 수정 예외 테스트
    @Test
    @DisplayName("❌ [실패] 댓글 수정: 수정하려는 댓글 ID가 존재하지 않으면 예외가 발생한다")
    void updateComment_Fail_CommentNotFound() {
        // given
        Long invalidCommentId = 777L;
        CommentRequestDto request = new CommentRequestDto("수정 내용", null);

        given(commentRepository.findById(invalidCommentId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> commentService.updateComment(invalidCommentId, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("댓글 없음");
    }
}