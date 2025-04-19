package com.example.task.domain.member.service;

import com.example.task.adaptor.member.dto.response.MemberResponse;
import com.example.task.common.exception.AccessDeniedException;
import com.example.task.common.exception.EntityAlreadyExistsException;
import com.example.task.common.exception.NotFoundException;
import com.example.task.common.exception.code.ErrorCode;
import com.example.task.domain.member.entity.Member;
import com.example.task.domain.member.entity.enums.Role;
import com.example.task.domain.member.repository.MemoryMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminMemberService {

    private final MemoryMemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberResponse generateAdmin() {
        if(memberRepository.findByUsername("admin").isPresent()) {
            throw new EntityAlreadyExistsException(ErrorCode.ADMIN_ALREADY_EXISTS);
        };
        List<Role> roles = new ArrayList<>();
        // 관리자 계정 초기화
        Member adminMember = Member.builder()
                .username("admin")
                .nickname("admin")
                .password(passwordEncoder.encode("admin"))
                .roles(roles)
                .build();
        adminMember.changeMemberRoleToAdmin();
        return MemberResponse.from(memberRepository.save(adminMember));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public MemberResponse grantAdminToMember(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));
        if(member.isAdmin()) throw new AccessDeniedException(ErrorCode.ADMIN_ROLE_ALREADY_EXISTS);
        member.changeMemberRoleToAdmin();
        return MemberResponse.from(memberRepository.save(member));
    }
}
