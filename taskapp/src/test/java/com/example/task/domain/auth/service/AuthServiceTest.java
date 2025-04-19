package com.example.task.domain.auth.service;

import com.example.task.adaptor.auth.dto.request.LoginRequest;
import com.example.task.adaptor.auth.dto.request.SignupRequest;
import com.example.task.adaptor.auth.dto.response.LoginResponse;
import com.example.task.adaptor.auth.dto.response.SignupResponse;
import com.example.task.adaptor.auth.util.JwtUtil;
import com.example.task.common.exception.EntityAlreadyExistsException;
import com.example.task.common.exception.InvalidRequestException;
import com.example.task.common.exception.code.ErrorCode;
import com.example.task.domain.auth.MemberDetail;
import com.example.task.domain.auth.repository.RefreshTokenRepository;
import com.example.task.domain.member.entity.Member;
import com.example.task.domain.member.entity.enums.Role;
import com.example.task.domain.member.repository.MemoryMemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.matchers.Any;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private MemoryMemberRepository memberRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Nested
    @DisplayName("회원가입")
    class Signup {

        @Test
        @DisplayName("회원가입 성공")
        void 회원가입_성공한다() {
            // given
            SignupRequest request = new SignupRequest("JIN HO", "12341234", "Mentos");
            List<Role> roles = new ArrayList<>();
            roles.add(Role.USER);
            Member member = Member.builder()
                    .id(1L)
                    .username("JIN HO")
                    .password("encoded-password")
                    .nickname("Mentos")
                    .roles(roles)
                    .build();
            when(memberRepository.existsByUsername("JIN HO")).thenReturn(false);
            when(passwordEncoder.encode("12341234")).thenReturn("encodedPw");
            when(memberRepository.save(any(Member.class))).thenReturn(member);
            // when
            SignupResponse signupResponse = authService.signup(request);
            // then
            assertEquals("JIN HO", signupResponse.username());
            assertEquals("Mentos", signupResponse.nickname());
            assertNotNull(signupResponse.roles(), "역할은 null이 아니어야 합니다.");
            assertTrue(signupResponse.roles().contains(Role.USER), "ROLE_USER가 포함되어야 합니다.");
        }

        @Test
        @DisplayName("회원가입 실패: 이름 중복")
        void 회원_이름이_중복되어_가입_실패한다() {
            // given
            SignupRequest request = new SignupRequest("JIN HO", "12341234", "Mentos");
            when(memberRepository.existsByUsername("JIN HO")).thenReturn(true);

            // when + then
            assertThrows(EntityAlreadyExistsException.class, () -> authService.signup(request));
            verify(memberRepository, times(1)).existsByUsername("JIN HO");
            verify(passwordEncoder, never()).encode(anyString());
            verify(memberRepository, never()).save(any(Member.class));
        }
    }

    @Nested
    @DisplayName("로그인")
    class Login {
        @Test
        @DisplayName("로그인 성공")
        void 로그인_성공한다() {
            // given
            SignupRequest signupRequest = new SignupRequest("JIN HO", "12341234", "Mentos");
            LoginRequest loginRequest = new LoginRequest("JIN HO", "12341234");
            List<Role> roles = new ArrayList<>();
            roles.add(Role.USER);
            Member member = Member.builder()
                    .id(1L)
                    .username("JIN HO")
                    .password("encodedPw")
                    .nickname("Mentos")
                    .roles(roles)
                    .build();
            String accessToken = "accessToken";
            String refreshToken = "refreshToken";
            //signup
            when(memberRepository.existsByUsername("JIN HO")).thenReturn(false);
            when(passwordEncoder.encode("12341234")).thenReturn("encoded-password");
            when(memberRepository.save(any(Member.class))).thenReturn(member);
            //login
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken("JIN HO", "12341234");
            List<GrantedAuthority> authorities = new ArrayList<>();
            if (member.isAdmin()) {
                authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            }
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            MemberDetail memberDetail =  new MemberDetail(member, authorities);
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    memberDetail, null, memberDetail.getAuthorities()
            );
            when(authenticationManager.authenticate(authToken)).thenReturn(authentication);
            when(jwtUtil.generateAccessToken(authentication)).thenReturn(accessToken);
            when(jwtUtil.generateRefreshToken(authentication)).thenReturn(refreshToken);
            when(refreshTokenRepository.save(member.getId(), refreshToken))
                    .thenReturn(refreshToken);
            // when
            authService.signup(signupRequest);
            LoginResponse loginResponse = authService.login(loginRequest);
            // then
            assertNotNull(loginResponse.token());
            assertEquals(accessToken, loginResponse.token());
            assertEquals("accessToken", loginResponse.token());
        }

        @Test
        @DisplayName("로그인 실패: 회원 정보 없음")
        void 존재하지_않는_이름_입력으로_로그인_실패한다() {
            // given
            SignupRequest signupRequest = new SignupRequest("JIN HO", "12341234", "Mentos");
            Member member = Member.builder()
                    .id(1L)
                    .username("JIN HO")
                    .password("encoded-password")
                    .nickname("Mentos")
                    .roles(new ArrayList<>())
                    .build();

            // Signup
            when(memberRepository.existsByUsername("JIN HO")).thenReturn(false);
            when(passwordEncoder.encode("12341234")).thenReturn("encoded-password");
            when(memberRepository.save(any(Member.class))).thenReturn(member);

            // Login
            LoginRequest loginRequest = new LoginRequest("JUN HO", "12341234");
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken("JUN HO", "12341234");
            when(authenticationManager.authenticate(authToken))
                    .thenThrow(new AuthenticationException("존재하지 않는 계정입니다.") {});

            // when
            authService.signup(signupRequest);

            // then
            assertThrows(InvalidRequestException.class, () -> authService.login(loginRequest));
            verify(jwtUtil, never()).generateAccessToken(any());
            verify(jwtUtil, never()).generateRefreshToken(any());
            verify(refreshTokenRepository, never()).save(anyLong(), anyString());
        }

        @Test
        @DisplayName("로그인 실패: 회원 정보 불일치")
        void 비밀번호_불일치로_인해_로그인_실패한다() {
            // given
            SignupRequest signupRequest = new SignupRequest("JIN HO", "12341234", "Mentos");
            Member member = Member.builder()
                    .id(1L)
                    .username("JIN HO")
                    .password("encoded-password")
                    .nickname("Mentos")
                    .roles(new ArrayList<>())
                    .build();

            // Signup
            when(memberRepository.existsByUsername("JIN HO")).thenReturn(false);
            when(passwordEncoder.encode("12341234")).thenReturn("encoded-password");
            when(memberRepository.save(any(Member.class))).thenReturn(member);

            // Login
            LoginRequest loginRequest = new LoginRequest("JIN HO", "123");
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken("JIN HO", "123");
            when(authenticationManager.authenticate(authToken))
                    .thenThrow(new BadCredentialsException("아이디 또는 비밀번호가 올바르지 않습니다.") {});

            // when
            authService.signup(signupRequest);

            // then
            assertThrows(InvalidRequestException.class, () -> authService.login(loginRequest));
            verify(jwtUtil, never()).generateAccessToken(any());
            verify(jwtUtil, never()).generateRefreshToken(any());
            verify(refreshTokenRepository, never()).save(anyLong(), anyString());
        }
    }

}