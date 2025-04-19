package com.example.task.adaptor.auth.filter;


import com.example.task.adaptor.auth.util.JwtUtil;
import com.example.task.common.exception.JwtException;
import com.example.task.common.exception.NotFoundException;
import com.example.task.common.exception.code.ErrorCode;
import com.example.task.domain.auth.MemberDetail;
import com.example.task.domain.auth.service.MemberDetailService;
import com.example.task.domain.member.entity.Member;
import com.example.task.domain.member.repository.MemoryMemberRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private final MemoryMemberRepository memberRepository;
    private final MemberDetailService memberDetailService;
    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String header = request.getHeader(AUTHORIZATION_HEADER);
        log.info("uri: {} header: {}", request.getRequestURI(),header);
        if (!StringUtils.hasText(header)) {
            log.info("토큰 정보가 없지만, security 에서 보증했기에 인증 생략합니다.");
            chain.doFilter(request, response);
            return;
        }
        try{
            String token = jwtUtil.substringToken(header);
            Claims claims = jwtUtil.validateTokenAndGetClaims(token);
            Member member = memberRepository.findById(Long.parseLong(claims.getSubject()))
                    .orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));
            MemberDetail memberDetail = memberDetailService.loadUserByUsername(member);
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    memberDetail,
                    null,
                    memberDetail.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }catch (JwtException e) {
            throw e; // 예외를 던져 FilterExceptionHandler에서 처리
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
            return;
        }//일관된 에러 처리가 필요함. GlobalExceptionHandler는 filter에서 발생하는 예외처리를 잡지 못하기 때문에.
        chain.doFilter(request, response);
    }
}
