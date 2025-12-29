package com.example.backend.domain.user.service;

import com.example.backend.domain.user.dto.LoginRequestDto;
import com.example.backend.domain.user.dto.UserRequestDto;
import com.example.backend.domain.user.dto.UserResponseDto;
import com.example.backend.domain.user.entity.UserEntity;
import com.example.backend.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    // 1-1. 유저 회원가입
    @Test
    @DisplayName("유저 회원가입 - 성공")
    void signup_Success() {
        // given
        UserRequestDto request = UserRequestDto.builder()
                .name("유시영")
                .email("newuser@test.com")
                .password("password123")
                .build();

        // 가공할 엔티티 생성
        UserEntity savedUser = request.toEntity();

        // Mock 설정: 이메일 조회는 결과가 없고(Optional.empty), 저장은 성공한다고 가정
        given(userRepository.findByEmail(request.getEmail())).willReturn(Optional.empty());
        given(userRepository.save(any(UserEntity.class))).willReturn(savedUser);
        given(passwordEncoder.encode(any())).willReturn("encoded_password");

        // 2. When (실행)
        UserResponseDto result = userService.signup(request);

        // 3. Then (검증)
        assertThat(result.getName()).isEqualTo("유시영");
        assertThat(result.getEmail()).isEqualTo("newuser@test.com");
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    // 1-2. 유저 로그인
    @Test
    @DisplayName("유저 로그인 - 성공")
    void login_Success() {
        // given
        UserEntity user = UserEntity.builder()
                .email("sy@test.com")
                .password("encoded_password") // 암호화된 상태라고 가정
                .build();

        given(userRepository.findByEmail("sy@test.com")).willReturn(Optional.of(user));
        // 핵심: matches가 true를 반환하도록 설정
        given(passwordEncoder.matches("1234", "encoded_password")).willReturn(true);

        // when
        UserResponseDto result = userService.login(new LoginRequestDto("sy@test.com", "1234"));

        // then
        assertThat(result.getEmail()).isEqualTo("sy@test.com");
    }

    // 2-1. 유저 전체 조회
    @Test
    @DisplayName("유저 전체 조회 - 성공")
    void getAllUsers_Success() {
        // given
        UserEntity userEntity = UserEntity.builder()
                .userId(1L)
                .name("유시영")
                .email("sy@test.com")
                .role("USER")
                .password("hashed_password") // DTO에는 포함되면 안 되는 정보
                .build();

        given(userRepository.findAll()).willReturn(List.of(userEntity));

        // when
        List<UserResponseDto> result = userService.getAllUsers();

        // then
        assertThat(result).hasSize(1); // 리스트 크기가 1인지
        assertThat(result.get(0).getName()).isEqualTo("유시영"); // 이름이 일치하는지
        assertThat(result.get(0).getEmail()).isEqualTo("sy@test.com"); // 이메일이 일치하는지

        // 중요: 비밀번호 필드는 DTO에 없어야 하므로 확인이 불가능하거나 노출되지 않음을 보장
        System.out.println("테스트 결과 첫 번째 유저 이름: " + result.get(0).getName());
    }

}