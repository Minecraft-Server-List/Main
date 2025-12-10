package com.example.minecraft.service;

import com.example.minecraft.dto.CacheEntry;
import com.example.minecraft.dto.ServerStatusDTO;
import com.example.minecraft.util.McStatusApiClient;
import com.google.gson.Gson;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

// 💡 McStatusApiClient, Gson은 ServerService에 있던 것을 가져왔다고 가정합니다.
// 실제 ServerService에서는 이 두 필드를 제거해야 합니다.

public class ServerStatusCacheManager {

    // 💡 캐시 저장소: 도메인(String)을 키로, CacheEntry를 값으로 저장. 동시성 보장
    private final ConcurrentHashMap<String, CacheEntry> cache = new ConcurrentHashMap<>();

    // 💡 캐시 만료 시간 (5분)
    private static final long CACHE_EXPIRY_MINUTES = 5;

    private static final String API_BASE_URL = "https://api.mcstatus.io/v2/status/java/";
    // 외부 API 클라이언트와 Gson 인스턴스는 여기서 유지 (API 호출 책임)
    private final McStatusApiClient apiClient = new McStatusApiClient();
    private final Gson gson = new Gson();

    // 싱글톤 패턴 (Lazy Initialization)
    private ServerStatusCacheManager() {}

    private static class LazyHolder {
        public static final ServerStatusCacheManager INSTANCE = new ServerStatusCacheManager();
    }

    public static ServerStatusCacheManager getInstance() {
        return LazyHolder.INSTANCE;
    }

    // 🚨 핵심 메서드: 상태를 반환합니다. (캐시가 유효하면 캐시 반환, 아니면 API 호출)
    public ServerStatusDTO getStatus(String domain) {

        // 1. 캐시 조회
        CacheEntry entry = cache.get(domain);

        // 2. 캐시가 존재하고 만료되지 않았다면 캐시된 상태 반환 (Cache Hit)
        if (entry != null && !isExpired(entry)) {
            System.out.println("CACHE HIT: " + domain); // 로그 출력
            return entry.getStatus();
        }

        // 3. 캐시 Miss/만료 시: API 호출
        System.out.println("CACHE MISS: API 호출 시작 - " + domain); // 로그 출력
        ServerStatusDTO newStatus = fetchStatusFromApi(domain);

        // 4. 새 결과를 캐시에 저장하고 반환
        if (newStatus != null) {
            cache.put(domain, new CacheEntry(newStatus));
            return newStatus;
        }

        // API 호출 실패 시 (Connect Timeout 등)
        System.err.println("API 호출 실패 후 오프라인 상태 반환: " + domain);
        return createOfflineStatus();
    }

    // 💡 캐시 만료 체크 로직
    private boolean isExpired(CacheEntry entry) {
        LocalDateTime expiryTime = entry.getTimestamp().plusMinutes(CACHE_EXPIRY_MINUTES);
        return LocalDateTime.now().isAfter(expiryTime);
    }

    // 💡 API 호출 로직 (외부로 노출되지 않음)
    private ServerStatusDTO fetchStatusFromApi(String domain) {
        String apiUrl = API_BASE_URL + domain;
        String jsonResponse = apiClient.callApi(apiUrl); // McStatusApiClient의 API 호출 메서드

        if (jsonResponse != null && !jsonResponse.isEmpty()) {
            // JSON 응답을 DTO로 변환
            return gson.fromJson(jsonResponse, ServerStatusDTO.class);
        }
        return null; // 호출 실패
    }

    // 💡 API 호출 실패 시 기본 오프라인 DTO 생성
    private ServerStatusDTO createOfflineStatus() {
        ServerStatusDTO statusDTO = new ServerStatusDTO();
        statusDTO.setOnline(false);
        // 필요에 따라 버전 정보 등을 기본값으로 설정할 수 있습니다.
        return statusDTO;
    }
}