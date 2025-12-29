package com.example.backend.domain.user.repository;

import com.example.backend.domain.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    // 기본 제공: save(), findById(), findAll(), deleteById() 등

    // 직접 추가: 이메일로 유저 찾기 (로그인 시 필요)
    Optional<UserEntity> findByEmail(String email);

    // 직접 추가: 이름이 존재하는지 확인 (중복 체크 시 필요)
    boolean existsByName(String name);
}