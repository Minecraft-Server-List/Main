<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html>
<head>
    <meta charset="UTF-8">
    <title>서버 등록 - CraftConnect</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/form-style.css">
</head>
<body>

<%@ include file="/WEB-INF/views/header.jsp" %>
<main class="form-page-main">
    <div class="container">
        <div class="user-form-container">
            <h2>새 마인크래프트 서버 등록</h2>
            <p style="text-align: center; margin-bottom: 20px;">서버 이름, 버전, 주소를 입력해주세요!</p>

            <form action="${pageContext.request.contextPath}/server.do" method="post" enctype="multipart/form-data">

                <input type="hidden" name="action" value="create">

                <div class="user-form-group">
                    <label for="name">서버 이름</label>
                    <input type="text" id="name" name="name" required>
                </div>

                <div class="user-form-group">
                    <label for="version">버전 (version)</label>
                    <input type="text" id="version" name="version" required>
                </div>

                <div class="user-form-group">
                    <label for="domain">주소 (domain)</label>
                    <input type="text" id="domain" name="domain" required>
                </div>

                <div class="user-form-group">
                    <label for="serverImage">대표 이미지</label>
                    <input type="file" id="serverImage" name="serverImage" accept="image/*">
                </div>

                <div class="user-form-group">
                    <label for="category">카테고리</label>
                    <select id="category" name="category">
                        <option value="survival">Survival (생존)</option>
                        <option value="pvp">PVP (플레이어 대 플레이어)</option>
                        <option value="creative">Creative (건축)</option>
                        <option value="minigame">Minigame (미니게임)</option>
                    </select>
                </div>

                <div class="user-form-group">
                    <input type="submit" value="등록하기">
                    <input type="reset" value="초기화">
                </div>
            </form>
        </div>
    </div>
</main>

<%@ include file="/WEB-INF/views/footer.jsp" %>

</body>
</html>