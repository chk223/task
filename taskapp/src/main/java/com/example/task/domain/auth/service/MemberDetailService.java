package com.example.task.domain.auth.service;

import com.example.task.common.exception.NotFoundException;
import com.example.task.common.exception.code.ErrorCode;
import com.example.task.domain.auth.MemberDetail;
import com.example.task.domain.member.entity.Member;
import com.example.task.domain.member.repository.MemoryMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberDetailService implements UserDetailsService {

    private final MemoryMemberRepository memberRepository;

    @Override
    public MemberDetail loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));
        return getMemberDetail(member);
    }

    public MemberDetail loadUserByUsername(Member member) throws UsernameNotFoundException {
        return getMemberDetail(member);
    }

    private static MemberDetail getMemberDetail(Member member) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (member.isAdmin()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        return new MemberDetail(member, authorities);
    }
}
