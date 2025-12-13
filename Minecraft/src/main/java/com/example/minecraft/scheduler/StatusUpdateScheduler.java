package com.example.minecraft.scheduler;

import com.example.minecraft.service.MinecraftStatusService;

public class StatusUpdateScheduler extends Thread {

    private final MinecraftStatusService statusService = new MinecraftStatusService();
    private volatile boolean running = true; // 스레드 안전 종료를 위한 플래그
    private static final long UPDATE_INTERVAL_MS = 5 * 60 * 1000; // 5분 = 300,000ms

    @Override
    public void run() {
        System.out.println("INFO: [Scheduler] 서버 상태 갱신 스케줄러 시작.");
        while (running) {
            try {
                // 1. DB 갱신 로직 실행
                statusService.updateAllServerStatuses();

                System.out.println("INFO: [Scheduler] 서버 상태 DB 갱신 완료. 다음 실행까지 대기.");

                // 2. 5분 대기
                Thread.sleep(UPDATE_INTERVAL_MS);

            } catch (InterruptedException e) {
                // 스레드 중단 요청 시 (서버 종료 시)
                Thread.currentThread().interrupt();
                running = false;
            } catch (Exception e) {
                System.err.println("ERROR: [Scheduler] 서버 상태 갱신 중 치명적인 오류 발생.");
                e.printStackTrace();
                // 오류 발생 시에도 5분 대기 후 재시도
                try { Thread.sleep(UPDATE_INTERVAL_MS); } catch (InterruptedException ignored) { }
            }
        }
        System.out.println("INFO: [Scheduler] 서버 상태 갱신 스케줄러 종료.");
    }

    // 🚨 외부에서 스케줄러를 안전하게 종료하기 위한 메서드
    public void stopScheduler() {
        this.running = false;
        this.interrupt();
    }
}