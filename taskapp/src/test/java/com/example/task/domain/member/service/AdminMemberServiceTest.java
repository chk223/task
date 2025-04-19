package com.example.task.domain.member.service;

import com.example.task.adaptor.auth.util.JwtUtil;
import com.example.task.adaptor.member.dto.response.MemberResponse;
import com.example.task.common.exception.AccessDeniedException;
import com.example.task.common.exception.NotFoundException;
import com.example.task.common.exception.code.ErrorCode;
import com.example.task.domain.auth.MemberDetail;
import com.example.task.domain.auth.repository.RefreshTokenRepository;
import com.example.task.domain.member.entity.Member;
import com.example.task.domain.member.entity.enums.Role;
import com.example.task.domain.member.repository.MemoryMemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminMemberServiceTest {

    @Mock
    private MemoryMemberRepository memberRepository;

    @InjectMocks
    private AdminMemberService adminMemberService;

    @Nested
    @DisplayName("관리자 권한 부여")
    class GrantAdmin {

        @BeforeEach
        void setUp() {
            SecurityContextHolder.clearContext();
        }

        @Test
        @DisplayName("관리자 권한 부여 성공")
        void 관리자_권한_부여_성공한다() {
            // given
            Long memberId = 1L;
            List<Role> roles = new ArrayList<>();
            roles.add(Role.USER);
            Member member = Member.builder()
                    .id(memberId)
                    .username("JIN HO")
                    .password("encoded-password")
                    .nickname("Mentos")
                    .roles(roles)
                    .build();
            Member updatedMember = Member.builder()
                    .id(memberId)
                    .username("JIN HO")
                    .password("encoded-password")
                    .nickname("Mentos")
                    .roles(List.of(Role.USER, Role.Admin))
                    .build();

            when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
            when(memberRepository.save(any(Member.class))).thenReturn(updatedMember);

            // SecurityContext
            SecurityContext securityContext = mock(SecurityContext.class);
            SecurityContextHolder.setContext(securityContext);

            // when
            MemberResponse response = adminMemberService.grantAdminToMember(memberId);

            // then
            assertEquals("JIN HO", response.username());
            assertEquals("Mentos", response.nickname());
            assertTrue(response.roles().contains(Role.Admin), "ROLE_ADMIN이 포함되어야 합니다.");
            verify(memberRepository, times(1)).findById(memberId);
            verify(memberRepository, times(1)).save(any(Member.class));
        }

        @Test
        @DisplayName("권한 부여 실패: 권한 없음")
        void Admin_권한이_없어_실패한다() {
            // given
            Long memberId = 1L;
            List<Role> roles = new ArrayList<>();
            roles.add(Role.USER);
            Member member = Member.builder()
                    .id(1L)
                    .username("JIN HO")
                    .password("encodedPw")
                    .nickname("Mentos")
                    .roles(roles)
                    .build();

            // SecurityContext
            SecurityContext securityContext = mock(SecurityContext.class);
            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            MemberDetail memberDetail =  new MemberDetail(member, authorities);
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    memberDetail, null, memberDetail.getAuthorities()
            );
            when(securityContext.getAuthentication()).thenReturn(authentication);
            SecurityContextHolder.setContext(securityContext);

            AdminMemberService spyService = new AdminMemberServiceSpy(memberRepository);
            // when & then
            assertThrows(AccessDeniedException.class, () -> spyService.grantAdminToMember(memberId));
        }

        @Test
        @DisplayName("권한 부여 실패: 존재하지 않는 사용자")
        void 부여하고자_하는_사용자_정보가_없어_실패한다() {
            // given
            Long memberId = 1L;

            // SecurityContext 모킹: ROLE_ADMIN 있음
            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    "admin", null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
            );
            when(securityContext.getAuthentication()).thenReturn(authentication);
            SecurityContextHolder.setContext(securityContext);

            when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

            // AdminMemberServiceSpy 객체 생성
            AdminMemberService spyService = new AdminMemberServiceSpy(memberRepository);

            // when & then
            assertThrows(NotFoundException.class, () -> spyService.grantAdminToMember(memberId));
            verify(memberRepository, times(1)).findById(memberId);
            verify(memberRepository, never()).save(any(Member.class));
        }

        @Test
        @DisplayName("권한 부여 실패: 이미 관리자")
        void 관리자에게_권한_중복_부여_시_실패한다() {
            // given
            Long memberId = 1L;
            List<Role> roles = new ArrayList<>();
            roles.add(Role.USER);
            roles.add(Role.Admin);
            Member member = Member.builder()
                    .id(memberId)
                    .username("JIN HO")
                    .password("encoded-password")
                    .nickname("Mentos")
                    .roles(roles)
                    .build();

            when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

            // SecurityContext
            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    "admin", null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
            );
            when(securityContext.getAuthentication()).thenReturn(authentication);
            SecurityContextHolder.setContext(securityContext);

            // AdminMemberServiceSpy 객체 생성
            AdminMemberService spyService = new AdminMemberServiceSpy(memberRepository);

            // when & then
            assertThrows(AccessDeniedException.class, () -> spyService.grantAdminToMember(memberId));
            verify(memberRepository, times(1)).findById(memberId);
            verify(memberRepository, never()).save(any(Member.class));
        }
    }
    class AdminMemberServiceSpy extends AdminMemberService {

        public AdminMemberServiceSpy(MemoryMemberRepository memberRepository) {
            super(memberRepository, mock(PasswordEncoder.class)); // PasswordEncoder mock 객체 주입
        }

        @Override
        public MemberResponse grantAdminToMember(Long id) {
            // 권한 검사 직접 수행
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

            if (!isAdmin) {
                throw new AccessDeniedException(ErrorCode.ACCESS_DENIED);
            }

            return super.grantAdminToMember(id);
        }
    }
}