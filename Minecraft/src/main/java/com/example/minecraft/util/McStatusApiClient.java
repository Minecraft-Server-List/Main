package com.example.minecraft.util;

import com.example.minecraft.dto.ServerStatusDTO;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class McStatusApiClient {

    // mcstatus.io API 사용 주소
    private static final String API_BASE_URL = "https://api.mcstatus.io/v2/status/java/";
    // JSON <-> 문자열 직렬화 및 역직렬화 도와주는 라이브러리
    private final Gson gson = new Gson();

    /**
     * 특정 호스트 주소에 대해 mcstatus.io API를 호출하고 ServerStatus 객체를 반환합니다.
     * @param domain 서버 호스트 주소 (예: "ddingtycoon.kr")
     * @return API 응답에 매핑된 ServerStatus 객체
    */
    public ServerStatusDTO getStatusByDomain(String domain) {
        String apiUrl = API_BASE_URL + domain;

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(3000);

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                br.close();
                String jsonResponse = sb.toString();

                return gson.fromJson(jsonResponse, ServerStatusDTO.class);

            } else {
                System.err.println("API 호출 실패: " + domain + ", 응답 코드: " + conn.getResponseCode());
                return createOfflineStatusObject();
            }
        } catch (Exception e) {
            System.err.println("API 연동 중 예외 발생: " + domain + ", 오류: " + e.getMessage());
            return createOfflineStatusObject();
        }
    }

    private ServerStatusDTO createOfflineStatusObject() {
        ServerStatusDTO status = new ServerStatusDTO();
        status.setOnline(false);
        return status;
    }
}