<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="com.example.minecraft.dto.UserDTO"%>
<%
    // searchUser.do 서블릿에서 조회 후 request에 저장된 userToEdit 객체를 사용합니다.
    UserDTO userToEdit = (UserDTO) request.getAttribute("userToEdit");
    String userRole = (String) session.getAttribute("userRole");
    
    // AJAX 로드 실패 방지를 위해 userToEdit이 null일 경우를 처리해야 합니다.
    if (userToEdit == null) {
        // (실제 데이터가 없는 경우의 에러 처리 로직)
        out.println("<div class=\"user-form-container\"><p>사용자 정보를 찾을 수 없습니다. 다시 로그인해 주세요.</p></div>");
        return; 
    }
%>

<div class="user-form-container">
    <h2 style="margin-bottom: 24px;">개인 정보 수정</h2>
    <hr style="margin-bottom: 24px;">
    
    <form action="updateUser.do" method="post">
        <input type="hidden" name="userId" value="<%= userToEdit.getUserId() %>">
        
         <div class="user-form-group">
            <label>가입일시 (수정불가)</label>
            <input type="text" value="<%= userToEdit.getCreatedAt() %>" readonly>
        </div>
         <div class="user-form-group">
            <label for="name">이름</label>
            <input type="text" id="name" name="name" value="<%= userToEdit.getName() %>" required>
        </div>
        <div class="user-form-group">
            <label for="email">이메일 (ID)</label>
            <input type="email" id="email" name="email" value="<%= userToEdit.getEmail() %>" required>
        </div>
        <div class="user-form-group">
            <label for="password">새 비밀번호</label>
            <input type="password" id="password" name="password" placeholder="변경할 경우에만 입력하세요">
        </div>
        <div class="user-form-group">
            <label for="role">역할</label>
            <%-- 관리자가 아니면 역할 수정 불가능 --%>
            <% if ("ADMIN".equals(userRole)) { %>
                <select name="role" id="role">
                    <option value="ADMIN" <%= "ADMIN".equals(userToEdit.getRole()) ? "selected" : "" %>>ADMIN</option>
                    <option value="USER"  <%= "USER".equals(userToEdit.getRole()) ? "selected" : "" %>>USER</option>
                </select>
            <% } else { %>
                <input type="text" value="<%= userToEdit.getRole() %>" readonly>
                <input type="hidden" name="role" value="<%= userToEdit.getRole() %>">
            <% } %>
        </div>
        <div class="user-form-group">
            <input type="submit" value="정보 수정하기">
        </div>
    </form>
</div>