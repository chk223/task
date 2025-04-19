package com.example.task.common.exception.handler;

import com.example.task.common.ApiResponse;
import com.example.task.common.exception.JwtException;
import com.example.task.common.exception.code.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
public class FilterExceptionHandler extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(FilterExceptionHandler.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (JwtException e) {
            log.error("JWT 예외 발생 - uri: {}, code: {}, message: {}",
                    request.getRequestURI(), e.getErrorCode().getCode(), e.getMessage());
            sendErrorResponse(response, ErrorCode.INVALID_TOKEN.getStatus(),
                    ErrorCode.INVALID_TOKEN.getMessage(), ErrorCode.INVALID_TOKEN.getCode());
        } catch (Exception e) {
            log.error("예상치 못한 필터 예외 - uri: {}, message: {}",
                    request.getRequestURI(), e.getMessage());
            sendErrorResponse(response, ErrorCode.INTERNAL_SERVER_ERROR.getStatus(),
                    ErrorCode.INTERNAL_SERVER_ERROR.getMessage(), ErrorCode.INTERNAL_SERVER_ERROR.getCode());
        }
    }

    private void sendErrorResponse(HttpServletResponse response, HttpStatus status,
                                   String message, String code) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ApiResponse<?> apiResponse = ApiResponse.failWithErrorCode(status, message, code).getBody();
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}
