<%--
  파일명: error.jsp
  설명: 사용자에게 친절하고 안내적인 오류 페이지 제공
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>페이지 오류 발생</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css">

    <%-- 🚨 오류 페이지 전용 스타일 --%>
    <style>
        .error-container {
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            min-height: 100vh;
            text-align: center;
            background-color: #f8f9fa; /* 밝은 배경 */
            padding: 20px;
        }
        .error-code {
            font-size: 8rem;
            font-weight: 900;
            color: #dc3545; /* 빨간색 (에러 색상) */
            margin-bottom: 0;
            line-height: 1;
        }
        .error-message {
            font-size: 2rem;
            color: #343a40;
            margin-top: 10px;
            margin-bottom: 30px;
        }
        .error-details {
            max-width: 600px;
            margin-top: 20px;
            padding: 20px;
            background-color: #fff;
            border: 1px solid #e9ecef;
            border-radius: 8px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
        }
        .error-details p {
            font-size: 1rem;
            color: #6c757d;
            line-height: 1.6;
        }
        .btn-home {
            background-color: #007bff;
            color: white;
            padding: 10px 20px;
            border-radius: 6px;
            text-decoration: none;
            font-weight: 600;
            margin-top: 30px;
            transition: background-color 0.3s;
        }
        .btn-home:hover {
            background-color: #0056b3;
        }
    </style>
</head>
<body>

<div class="error-container">

    <%-- 🚨 오류 코드 (HTTP 상태 코드를 받아 출력할 수 있도록 설계) --%>
    <div class="error-code">
        <c:out value="${empty requestScope['jakarta.servlet.error.status_code'] ? 'Error' : requestScope['jakarta.servlet.error.status_code']}" default="500" />
    </div>

    <h1 class="error-message">
        죄송합니다. 요청하신 페이지를 처리할 수 없습니다.
    </h1>

    <div class="error-details">
        <p>
            **문제가 발생했습니다.** <br>
            일시적인 오류일 수 있으니 잠시 후 다시 시도해 주시거나, 아래 버튼을 눌러 홈으로 돌아가 주세요.
        </p>

        <%-- 개발자를 위한 상세 정보 (선택 사항) --%>
        <c:if test="${!empty requestScope['jakarta.servlet.error.message']}">
            <p style="margin-top: 15px; font-style: italic; font-size: 0.9em;">
                상세 오류: <c:out value="${requestScope['jakarta.servlet.error.message']}" />
            </p>
        </c:if>
    </div>

    <a href="${pageContext.request.contextPath}/index" class="btn-home">
        메인 페이지로 돌아가기
    </a>

</div>

</body>
</html>