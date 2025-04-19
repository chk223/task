package com.example.task.domain.auth.service;

import com.example.task.adaptor.auth.dto.request.LoginRequest;
import com.example.task.adaptor.auth.dto.request.SignupRequest;
import com.example.task.adaptor.auth.dto.response.LoginResponse;
import com.example.task.adaptor.auth.dto.response.SignupResponse;
import com.example.task.adaptor.auth.util.JwtUtil;
import com.example.task.common.exception.AccessDeniedException;
import com.example.task.common.exception.EntityAlreadyExistsException;
import com.example.task.common.exception.InvalidRequestException;
import com.example.task.common.exception.code.ErrorCode;
import com.example.task.domain.auth.MemberDetail;
import com.example.task.domain.auth.repository.RefreshTokenRepository;
import com.example.task.domain.member.entity.Member;
import com.example.task.domain.member.repository.MemoryMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemoryMemberRepository memberRepository;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public SignupResponse signup(SignupRequest request) {
        checkUsernameDuplicated(request);
        Member member = request.toEntity();
        member.encodingPassword(passwordEncoder);
        memberRepository.save(member);
        return SignupResponse.from(member);
    }

    private void checkUsernameDuplicated(SignupRequest request) {
        if(memberRepository.existsByUsername(request.username())) {
            throw new EntityAlreadyExistsException(ErrorCode.DUPLICATED_INFO);
        }
    }

    public LoginResponse login(LoginRequest request) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(request.username(), request.password());
        try{
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            String accessToken = jwtUtil.generateAccessToken(authentication);
            String refreshToken = jwtUtil.generateRefreshToken(authentication);
            Member member = ((MemberDetail) authentication.getPrincipal()).getMember();
            refreshTokenRepository.save(member.getId(),refreshToken);
            return new LoginResponse(accessToken);
        } catch (BadCredentialsException e) {
            throw new InvalidRequestException(ErrorCode.INVALID_CREDENTIALS);
        } catch (AuthenticationException e) {
            throw new InvalidRequestException(ErrorCode.AUTH_INFO_NOT_FOUND);
        } catch (Exception e) {
            throw new AccessDeniedException(ErrorCode.LOGIN_NOT_ACCEPTABLE);
        }
    }
}
