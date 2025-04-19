package com.example.task.adaptor.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "로그인 응답 DTO")
public record LoginResponse(
        @Schema(description = "JWT 액세스 토큰, 요구사항에 Refresh 토큰, 만료 기한 등이 없어 이것만 전달.",
                example = "eKDIkdfjoakIdkfjpekdkcjdkoIOdjOKJDFOlLDKFJKL")
        String token
) {//refresh 토큰도 주는 게 맞지만, 예시에 맞게 access 토큰만 전달
}
