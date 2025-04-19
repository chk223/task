package com.example.task.adaptor.member.controller;

import com.example.task.adaptor.member.dto.response.MemberResponse;
import com.example.task.common.ApiResponse;
import com.example.task.domain.member.service.AdminMemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "관리자", description = "관리자 계정 권한 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/users")
public class AdminMemberController {

    private final AdminMemberService adminMemberService;

    @PostMapping
    @Operation(
            summary = "테스트용 관리자 계정 생성",
            description = "테스트 목적으로 새로운 관리자 계정을 생성합니다. 인증 없이 호출 가능합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "관리자 계정 생성 성공",
                    content = @Content(
                            schema = @Schema(implementation = MemberResponse.class),
                            mediaType = "application/json"
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "관리자 계정이 이미 생성됨",
                    content = @Content(
                            schema = @Schema(implementation = ApiResponse.class),
                            mediaType = "application/json"
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "서버 내부 오류",
                    content = @Content(
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            )
    })
    public ResponseEntity<ApiResponse<MemberResponse>> createAdmin() {
        return ApiResponse.created(adminMemberService.generateAdmin());
    }

    @Operation(
            summary = "관리자 권한 부여",
            description = "사용자에게 관리자 권한을 부여합니다. 관리자 계정으로만 사용 가능합니다." +
                    " 초기 관리자 계정은 테스트용 관리자 계정 API를 사용해주세요."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "관리자 권한 부여 성공",
                    content = @Content(
                            schema = @Schema(implementation = MemberResponse.class),
                            mediaType = "application/json"
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "권한 부족 (관리자 계정이 아님)",
                    content = @Content(
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "사용자를 찾을 수 없음",
                    content = @Content(
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "서버 내부 오류",
                    content = @Content(
                            schema = @Schema(implementation = ApiResponse.class)
                    )
            )
    })
    @PostMapping("/{userId}/roles")
    public ResponseEntity<ApiResponse<MemberResponse>> updateUserRoleToAdmin(@PathVariable Long userId) {
        return ApiResponse.success(adminMemberService.grantAdminToMember(userId));
    }
}
