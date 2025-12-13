<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<div id="login-modal" class="modal-backdrop hidden">

    <div id="modal-backdrop-area" class="modal-backdrop-area"></div>

    <div class="modal-content">

        <button
                id="login-close-btn"
                type="button"
                class="modal-close-btn"
        >
            <i class='bx bx-x w-6 h-6'></i>
        </button>

        <div class="modal-header-bg">
            <div class="modal-icon-wrap">
                <i class='bx bx-user modal-user-icon'></i>
            </div>
            <h2 class="modal-title">로그인</h2>
            <p class="modal-subtitle">CraftConnect에 오신 것을 환영합니다</p>
        </div>

        <div class="modal-form-section">
            <form action="login.do" method="post" id="login-form" class="modal-form-spacing">

                <div class="input-group">
                    <label class="input-label">이메일 (ID)</label>
                    <div class="input-relative-wrap">
                        <div class="input-icon-left">
                            <i class='bx bx-envelope input-icon-style'></i>
                        </div>
                        <input
                                type="text"
                                placeholder="이메일을 입력하세요"
                                class="modal-input"
                                name="id"
                                required
                        />
                    </div>
                </div>

                <div class="input-group">
                    <label class="input-label">비밀번호</label>
                    <div class="input-relative-wrap">
                        <div class="input-icon-left">
                            <i class='bx bx-lock-alt input-icon-style'></i>
                        </div>
                        <input
                                type="password"
                                id="password-input"
                                placeholder="비밀번호를 입력하세요"
                                class="modal-input"
                                name="pw"
                                required
                        />
                        <button
                                type="button"
                                id="toggle-password-btn"
                                class="input-icon-right cursor-pointer"
                        >
                            <i class='bx bx-show-alt password-icon' id="eye-show"></i>
                            <i class='bx bx-hide password-icon hidden' id="eye-off"></i>
                        </button>
                    </div>
                </div>

                <div class="options-row">
                    <label class="checkbox-label">
                        <input type="checkbox" class="modal-checkbox" />
                        <span>로그인 상태 유지</span>
                    </label>
                    <button type="button" class="forgot-password-link">
                        비밀번호 찾기
                    </button>
                </div>

                <button type="submit" class="modal-login-btn">
                    로그인
                </button>
            </form>

            <div class="modal-footer-text">
                계정이 없으신가요?
                <button type="button" class="register-link">
                    회원가입
                </button>
            </div>

        </div>
    </div>
</div>