package com.example.minecraft.service;

import com.example.minecraft.dao.UserDAO;
import com.example.minecraft.dto.UserDTO;

import java.util.ArrayList;

public class UserService {

    private final UserDAO userDAO = new UserDAO();

    // 1. 로그인
    public UserDTO loginService(String email, String password) {
        return userDAO.loginUser(email, password);
    }

    // 2. 회원가입
    public boolean registerService(UserDTO user) {
        int result = userDAO.insertUser(user);
        return result > 0;
    }

    // 3. 전체 회원 목록
    public ArrayList<UserDTO> getAllUsersService() {
        return userDAO.SelectAll();
    }

    // 4. 이메일로 회원 찾기
    public UserDTO getUserByEmailService(String email) {
        return userDAO.selectUserByEmail(email);
    }

    // 5. ID로 회원 찾기
    public UserDTO getUserByIdService(long userId) {
        return userDAO.selectUserById(userId);
    }

    // 6. 회원 수정
    public boolean updateUserService(UserDTO user) {
        int result = userDAO.updateUser(user);
        return result > 0;
    }

    // 7. 회원 삭제
    public boolean deleteUserService(long userId) {
        int result = userDAO.deleteUser(userId);
        return result > 0;
    }
}