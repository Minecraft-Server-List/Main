package com.example.minecraft.dto;

import com.google.gson.annotations.SerializedName; // Gson 어노테이션 import 확인

public class VersionDTO {

    // 1. JSON 필드 이름 매핑 (API에서 오는 이름)
    @SerializedName("name_clean")
    private String nameClean; // 🚨 필드 이름은 Camel Case (nameClean) 사용

    // 2. EL이 찾는 Getter 메서드 (getNameClean) 정의
    public String getNameClean() {
        return nameClean;
    }

    // 3. Setter도 Camel Case 사용 (생략 가능하나 DTO 표준)
    public void setNameClean(String nameClean) {
        this.nameClean = nameClean;
    }

    // ... (나머지 필드)
}