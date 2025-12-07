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
    private final Gson gson = new Gson();

    /**
     * 특정 호스트 주소에 대해 API를 호출하고 ServerStatus 객체를 반환합니다.
     * (ServerService에서 이 메서드를 직접 호출하지 않고 내부적으로 사용됨)
     * @param domain 서버 호스트 주소
     * @return API 응답에 매핑된 ServerStatus 객체
     */
    public ServerStatusDTO getStatusByDomain(String domain) {
        String apiUrl = API_BASE_URL + domain;
        String jsonResponse = callApi(apiUrl); // 🚨 callApi 메서드 사용으로 변경

        if (jsonResponse != null) {
            try {
                return gson.fromJson(jsonResponse, ServerStatusDTO.class);
            } catch (Exception e) {
                System.err.println("JSON 파싱 중 예외 발생: " + e.getMessage());
                return createOfflineStatusObject();
            }
        } else {
            return createOfflineStatusObject();
        }
    }

    /**
     * 🚨 추가/수정: ServerService가 요구하는 메서드 (API 호출 및 JSON 문자열 반환)
     * 이 메서드를 ServerService.java에서 사용합니다.
     * @param apiUrl 호출할 API의 전체 URL
     * @return API 응답 JSON 문자열 또는 오류 시 null
     */
    public String callApi(String apiUrl) {
        HttpURLConnection conn = null;
        BufferedReader br = null;
        try {
            URL url = new URL(apiUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(3000);

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                // 🚨 try-with-resources가 아니므로 여기서 br.close() 필요
                br.close();
                return sb.toString();

            } else {
                System.err.println("API 호출 실패. 응답 코드: " + conn.getResponseCode() + ", URL: " + apiUrl);
                return null;
            }
        } catch (Exception e) {
            System.err.println("API 통신 중 예외 발생: " + apiUrl + ", 오류: " + e.getMessage());
            return null;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
            // br이 null이 아닌 경우 닫는 안전 로직은 Service나 이 메서드 내에 포함되어야 함.
            // (getStatusByDomain 로직을 따랐을 때 br.close()는 try 블록 안에 있었습니다.)
        }
    }


    private ServerStatusDTO createOfflineStatusObject() {
        ServerStatusDTO status = new ServerStatusDTO();
        // ServerStatusDTO의 setOnline은 boolean 타입이라고 가정
        status.setOnline(false);
        return status;
    }
}