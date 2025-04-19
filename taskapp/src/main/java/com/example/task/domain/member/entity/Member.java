package com.example.task.domain.member.entity;

import com.example.task.domain.member.entity.enums.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class Member {
    private Long id;
    private String username;
    private String password;
    private String nickname;
    private List<Role> roles;

    @Builder
    public Member(Long id, String username, String password, String nickname, List<Role> roles) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.roles = roles;
    }

    public void changeMemberRoleToAdmin() {
        this.roles.add(Role.Admin);
    }

    public boolean isAdmin() {
        return this.roles.stream()
                .anyMatch(role -> role == Role.Admin);
    }

    public void encodingPassword(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
    }
}
