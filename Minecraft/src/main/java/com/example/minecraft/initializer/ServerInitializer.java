package com.example.minecraft.initializer;

import com.example.minecraft.scheduler.StatusUpdateScheduler;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

// 🚨 Tomcat 시작/종료 이벤트를 감지하는 리스너
@WebListener
public class ServerInitializer implements ServletContextListener {

    private StatusUpdateScheduler scheduler;

    // 1. Tomcat 시작 시: 스케줄러 스레드 실행
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("INFO: [Initializer] 애플리케이션 시작됨. 스케줄러를 초기화합니다.");

        try {
            System.out.println("INFO: [Initializer] 5초 후 스케줄러 시작.");
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        scheduler = new StatusUpdateScheduler();
        scheduler.start(); // 스레드 시작!
    }

    // 2. Tomcat 종료 시: 스케줄러 스레드 안전하게 중지
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (scheduler != null) {
            System.out.println("INFO: [Initializer] 애플리케이션 종료됨. 스케줄러를 중지합니다.");
            scheduler.stopScheduler();
            try {
                scheduler.join(5000); // 최대 5초 대기
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}