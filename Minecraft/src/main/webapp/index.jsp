<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>CraftConnect - Find Your Perfect Minecraft Server</title>

</head>
<body>
    <%@ include file="header.jsp" %>
    

    <main>
        <section class="hero">
            <div class="hero-content">
                <h1>Find Your Perfect Minecraft Server</h1>
                <p>Explore thousands of Minecraft servers and connect with players worldwide. Discover new adventures and build your legacy.</p>
                <form action="/search" method="get" class="hero-search">
                    <input type="text" name="query" placeholder="Search for servers (e.g., survival, pvp, creative)">
                    <button type="submit">Search</button>
                </form>
            </div>
        </section>

        <div class="container">
            <section class="featured-servers">
                <h2>Featured Servers</h2>
                <div class="card-container">
                    
                    <%-- 목업 데이터 1 (서버 연동 전) --%>
                    <div class="server-card">
                        <img src="https://placehold.co/400x180/a1b1a1/ffffff?text=SurvivalCraft" alt="SurvivalCraft">
                        <div class="card-content">
                            <h3>SurvivalCraft</h3>
                            <p>A classic survival experience with a friendly community.</p>
                        </div>
                    </div>
                    <%-- 목업 데이터 2 --%>
                    <div class="server-card">
                        <img src="https://placehold.co/400x180/a1b1c1/ffffff?text=CreativeBuild" alt="CreativeBuild">
                        <div class="card-content">
                            <h3>CreativeBuild</h3>
                            <p>Unleash your imagination. Build a custom world in a creative environment.</p>
                        </div>
                    </div>
                    <%-- 목업 데이터 3 --%>
                    <div class="server-card">
                        <img src="https://placehold.co/400x180/c1b1a1/ffffff?text=AdventureQuest" alt="AdventureQuest">
                        <div class="card-content">
                            <h3>AdventureQuest</h3>
                            <p>Embark on epic quests and explore vast landscapes.</p>
                        </div>
                    </div>
                </div>
            </section>

            <section class="top-servers">
                <h2>Top Servers</h2>
                <table class="server-table">
                    <thead>
                        <tr>
                            <th>Server Name</th>
                            <th>Players</th>
                            <th>Uptime</th>
                            <th>Version</th>
                            <th>Status</th>
                        </tr>
                    </thead>
                    <tbody>
                        <%-- 목업 데이터 (생략) --%>
                        <tr>
                            <td class="server-name">CraftLandia</td>
                            <td>120<span class="player-count-max">/200</span></td>
                            <td>99.8%</td>
                            <td>1.19.2</td>
                            <td><span class="status-online">Online</span></td>
                        </tr>
                        <tr>
                            <td class="server-name">BuildTopia</td>
                            <td>85<span class="player-count-max">/150</span></td>
                            <td>100%</td>
                            <td>1.18.1</td>
                            <td><span class="status-online">Online</span></td>
                        </tr>
                        <tr>
                            <td class="server-name">QuestRealms</td>
                            <td>80<span class="player-count-max">/100</span></td>
                            <td>98.5%</td>
                            <td>1.17.1</td>
                            <td><span class="status-online">Online</span></td>
                        </tr>
                        <tr>
                            <td class="server-name">PixelCraft</td>
                            <td>45<span class="player-count-max">/80</span></td>
                            <td>99.5%</td>
                            <td>1.19.5</td>
                            <td><span class="status-online">Online</span></td>
                        </tr>
                        <tr>
                            <td class="server-name">MineCity</td>
                            <td>30<span class="player-count-max">/50</span></td>
                            <td>97.2%</td>
                            <td>1.18.2</td>
                            <td><span class="status-online">Online</span></td>
                        </tr>
                    </tbody>
                </table>
            </section>
        </div>
    </main>

    <footer class="main-footer">
        <div class="container">
            <nav class="footer-nav">
                <a href="#">About</a>
                <a href="#">Contact</a>
                <a href="#">Terms of Service</a>
                <a href="#">Privacy Policy</a>
            </nav>
            <div class="footer-socials">
                <a href="#"><i class='bx bxl-twitter'></i></a>
                <a href="#"><i class='bx bxl-facebook-square'></i></a>
                <a href="#"><i class='bx bxl-instagram'></i></a>
            </div>
            <p class="copyright">@2023 CraftConnect. All rights reserved.</p>
        </div>
    </footer>

</body>
</html>