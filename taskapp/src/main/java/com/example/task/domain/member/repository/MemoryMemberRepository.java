package com.example.task.domain.member.repository;

import com.example.task.domain.member.entity.Member;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class MemoryMemberRepository {
    Map<Long, Member> memberStorage = new HashMap<>();
    private static final AtomicLong ID_GENERATOR = new AtomicLong(0);

    public Member save(Member member) {
        if(member.getId() == null) {
            Long id = ID_GENERATOR.incrementAndGet();
            setIdWithReflection(member, id);
        }
        memberStorage.put(member.getId(),member);
        return member;
    };

    private void setIdWithReflection(Member member, Long id) {
        try {
            Field idField = Member.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(member, id);
        } catch (Exception e) {
            throw new RuntimeException("ID 설정 중 오류 발생", e);
        }
    }

    public Optional<Member> findById(Long id) {
        return Optional.of(memberStorage.get(id));
    };

    public Optional<Member> findByUsername(String username) {
        return memberStorage.values().stream()
                .filter(member -> Objects.equals(username, member.getUsername()))
                .findFirst();
    }

    public boolean existsByUsername(String username) {
        return memberStorage.values().stream()
                .anyMatch(member -> Objects.equals(username, member.getUsername()));
    }

    public List<Member> findAll() {
        return memberStorage.values().stream().toList();
    }

    public void deleteById(Long id) {
        memberStorage.remove(id);
    }
}
