package com.example.task.common.exception.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    // Common
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", "올바르지 않은 요청입니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "METHOD_NOT_ALLOWED", "잘못된 HTTP 메서드를 호출했습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "서버에 에러가 발생했습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "NOT_FOUND", "존재하지 않는 엔티티입니다."),
    ENTITY_ALREADY_EXISTS(HttpStatus.CONFLICT, "ENTITY_ALREADY_EXISTS", "이미 존재하는 엔티티입니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "ACCESS_DENIED", "접근 권한이 없습니다."),
    TOO_MANY_REQUEST(HttpStatus.TOO_MANY_REQUESTS, "TOO_MANY_REQUEST", "현재 요청이 많아 처리할 수 없습니다. 잠시 후 다시 시도해주세요."),

    // Member
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER_NOT_FOUND", "존재하지 않는 유저입니다."),
    MEMBER_DELETED(HttpStatus.FORBIDDEN, "MEMBER_DELETED", "삭제된 유저입니다."),
    MEMBER_ALREADY_EXISTS(HttpStatus.CONFLICT, "MEMBER_ALREADY_EXISTS", "이미 존재하는 유저입니다."),
    SAME_VALUE_REQUEST(HttpStatus.CONFLICT, "SAME_VALUE_REQUEST", "이전에 사용하던 정보를 재사용할 수 없습니다."),
    ADMIN_ROLE_ALREADY_EXISTS(HttpStatus.CONFLICT, "ADMIN_ROLE_ALREADY_EXISTS", "관리자 역할이 이미 부여되었습니다."),

    // Auth
    INVALID_CREDENTIALS(HttpStatus.BAD_REQUEST, "INVALID_CREDENTIALS", "아이디 또는 비밀번호가 올바르지 않습니다."),
    LOGIN_NOT_ACCEPTABLE(HttpStatus.NOT_ACCEPTABLE, "LOGIN_NOT_ACCEPTABLE", "로그인 요청이 거부되었습니다."),
    NOT_CORRECT_TOKEN_TYPE(HttpStatus.BAD_REQUEST, "NOT_CORRECT_TOKEN_TYPE", "올바르지 않은 토큰 타입입니다."),
    DUPLICATED_INFO(HttpStatus.CONFLICT, "DUPLICATED_INFO", "이미 존재하는 회원정보입니다."),
    AUTH_INFO_NOT_FOUND(HttpStatus.NOT_FOUND, "AUTH_INFO_NOT_FOUND", "존재하지 않는 계정입니다."),
    EXPIRED_TOKEN(HttpStatus.NOT_ACCEPTABLE, "EXPIRED_TOKEN", "토큰이 만료되었습니다."),
    INVALID_TOKEN(HttpStatus.BAD_REQUEST, "INVALID_TOKEN", "유효하지 않은 토큰입니다."),
    NOT_FOUND_TOKEN(HttpStatus.NOT_FOUND, "NOT_FOUND_TOKEN", "존재하지 않는 토큰입니다."),
    ADMIN_ALREADY_EXISTS(HttpStatus.CONFLICT, "ADMIN_ALREADY_EXISTS", "관리자 계정이 이미 생성되었습니다."),
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
