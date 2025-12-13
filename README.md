# 🧩짭인리스트

## 🎯 개요
나 혼자만 즐기던 게임에서 다른 사람들이 만든 서버에 들어가 함께 즐기는 시간까지

## 📂 MVC 프로젝트 구조
### 1. Java 소스 코드
```
src/main/java
└── com.example.myservletproject
    ├── controller    <-- Controller 역할 (Servlet)
    │   └── *Servlet.java
    ├── service       <-- Model 일부 (비즈니스 로직)
    │   └── *Service.java
    ├── dao           <-- Model 일부 (데이터 접근)
    │   └── *DAO.java
    ├── dto           <-- Model 일부 (데이터 객체)
    │   └── *DTO.java (or Model, Domain)
    └── util          <-- 지원 계층 (공통 유틸리티)
        └── *Util.java
```
### 2. Web 리소스
> 임시 폴더 구조 세팅
```
src/main/webapp
├── WEB-INF
│   └── views       <-- View 역할 (JSP)
│       └── *jsp
├── static          <-- 정적 리소스 (CSS, JS 등)
│   ├── css
│   └── js
└── index.jsp       <-- 초기 진입점
```

## 🌍노션
> https://www.notion.so/2a1c655cd28080868542f7ad5295a52b?source=copy_link

## ⭐️피그마
> https://www.figma.com/design/mRlFUtz2dAt6VKyelDkb1B/짭인리스트?node-id=0-1&p=f&t=s15OzWBe0GIXK1a4-0

## 🗓️ERD CLOUD
> [https://www.erdcloud.com/d/tZtqCGjXQyviHkXWE](https://www.erdcloud.com/d/QyWwW9pBPaaYZMEpg)
