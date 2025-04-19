package com.example.task.adaptor.auth.dto.response;

import com.example.task.domain.member.entity.Member;
import com.example.task.domain.member.entity.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
@Schema(description = "회원가입 응답 DTO")
public record SignupResponse(
        @Schema(description = "생성된 사용자 이름", example = "JIN HO")
        String username,
        @Schema(description = "사용자 닉네임", example = "Mentos")
        String nickname,
        @Schema(description = "사용자 역할(Admin 이 포함되면 관리자)", example = "Role: USER, Admin")
        List<Role> roles) {
    public static SignupResponse from(Member member) {
        return SignupResponse.builder()
                .username(member.getUsername())
                .nickname(member.getNickname())
                .roles(member.getRoles())
                .build();
    }
}
