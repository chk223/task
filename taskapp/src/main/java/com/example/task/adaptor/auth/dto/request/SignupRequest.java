package com.example.task.adaptor.auth.dto.request;

import com.example.task.domain.member.entity.Member;
import com.example.task.domain.member.entity.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;

@Schema(description = "회원가입 요청 DTO")
public record SignupRequest(
        @Schema(description = "사용자 이름(로그인 시 id로 사용)", example = "JIN HO", required = true)
        String username,
        @Schema(description = "비밀번호 (제약사항 설정 안함)", example = "12341234", required = true)
        String password,
        @Schema(description = "사용자 닉네임", example = "Mentos", required = true)
        String nickname) {
    public Member toEntity() {
        List<Role> roles = new ArrayList<>();
        roles.add(Role.USER);
        return Member.builder()
                .username(username)
                .password(password)
                .nickname(nickname)
                .roles(roles)
                .build();
    }
}
