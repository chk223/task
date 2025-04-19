package com.example.task.adaptor.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "로그인 요청 DTO")
public record LoginRequest(
        @Schema(description = "사용자 이름(로그인 할 id)", example = "JIN HO", required = true)
        String username,
        @Schema(description = "비밀번호", example = "12341234", required = true)
        String password) {
}
