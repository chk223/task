package com.example.task.domain.auth;


import com.example.task.domain.member.entity.Member;
import com.example.task.domain.member.entity.enums.Role;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class MemberDetail implements UserDetails {
    private final Member member;
    private final Collection<? extends GrantedAuthority> authorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public Long getId() {
        return member.getId();
    }

    public List<Role> getRoles() {
        return member.getRoles();
    }

    @Override
    public String getPassword() {
        return member.getPassword();
    }

    @Override
    public String getUsername() {
        return member.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {//계정 만료 여부
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {//계정 잠김 여부
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {//계정 만료 여부(토큰 사용 기간 만료)
        return true;
    }

    @Override
    public boolean isEnabled() {//계정 활성화 여부(탈퇴)
        return true;
    }
}
