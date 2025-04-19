package com.example.task.adaptor.member.controller;

import com.example.task.common.ApiResponse;
import com.example.task.domain.member.entity.Member;
import com.example.task.domain.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Tag(name = "유저", description = "유저 조회 API")
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "모든 유저 정보 조회", description = "테스트를 위해 모든 유저의 정보를 확인합니다." +
            "테스트 용도이기에 자세한 설명은 생략합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<Member>>> getAllMember() {
        return ApiResponse.success(memberService.findAllMember());
    }
}
