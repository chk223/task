package com.example.task.domain.member.service;

import com.example.task.domain.member.entity.Member;
import com.example.task.domain.member.repository.MemoryMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemoryMemberRepository memberRepository;

    public List<Member> findAllMember() {
        return memberRepository.findAll();
    }
}
