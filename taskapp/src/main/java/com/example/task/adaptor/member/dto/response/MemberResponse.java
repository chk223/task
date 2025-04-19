package com.example.task.adaptor.member.dto.response;

import com.example.task.domain.member.entity.Member;
import com.example.task.domain.member.entity.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
@Schema(description = "회원 정보 DTO")
public record MemberResponse(
        @Schema(description = "회원 이름", example = "JIN HO")
        String username,
        @Schema(description = "회원 닉네임", example = "Mentos")
        String nickname,
        @Schema(description = "회원 역할", example = "Role: USER")
        List<Role> roles) {
    public static MemberResponse from(Member member) {
        return MemberResponse.builder()
                .username(member.getUsername())
                .nickname(member.getNickname())
                .roles(member.getRoles())
                .build();
    }
}
