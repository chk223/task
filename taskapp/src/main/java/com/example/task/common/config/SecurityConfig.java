package com.example.task.common.config;

import com.example.task.adaptor.auth.filter.JwtFilter;
import com.example.task.adaptor.auth.util.JwtUtil;
import com.example.task.common.ApiResponse;
import com.example.task.common.exception.code.ErrorCode;
import com.example.task.domain.auth.service.MemberDetailService;
import com.example.task.domain.member.repository.MemoryMemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final MemoryMemberRepository memberRepository;
    private final MemberDetailService memberDetailService;
    private final JwtUtil jwtUtil;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "auth/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "admin/users").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**",
                                "/swagger-resources/**", "/webjars/**").permitAll()
                        .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exceptionHandler -> exceptionHandler
                        .authenticationEntryPoint((req, res, authEx) -> {
                            res.setStatus(ErrorCode.INVALID_TOKEN.getStatus().value());
                            res.setContentType("application/json");
                            res.setCharacterEncoding("UTF-8");
                            ApiResponse<?> response = ApiResponse.failWithErrorCode(
                                    ErrorCode.INVALID_TOKEN.getStatus(),
                                    ErrorCode.INVALID_TOKEN.getMessage(),
                                    ErrorCode.INVALID_TOKEN.getCode()).getBody();
                            res.getWriter().write(objectMapper.writeValueAsString(response));
                        })
                        .accessDeniedHandler((req, res, accessEx) -> {
                            res.setStatus(ErrorCode.ACCESS_DENIED.getStatus().value());
                            res.setContentType("application/json");
                            res.setCharacterEncoding("UTF-8");
                            ApiResponse<?> response = ApiResponse.failWithErrorCode(
                                    ErrorCode.ACCESS_DENIED.getStatus(),
                                    ErrorCode.ACCESS_DENIED.getMessage(),
                                    ErrorCode.ACCESS_DENIED.getCode()).getBody();
                            res.getWriter().write(objectMapper.writeValueAsString(response));
                        })
                )
                .addFilterBefore(new JwtFilter(memberRepository, memberDetailService, jwtUtil),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 허용할 origin 설정
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:8080",
                "https://localhost:8080"
        ));

        // 허용할 HTTP 메서드 설정
        configuration.setAllowedMethods(
                Arrays.asList(
                        HttpMethod.GET.name(),
                        HttpMethod.POST.name(),
                        HttpMethod.PATCH.name(),
                        HttpMethod.PUT.name(),
                        HttpMethod.DELETE.name(),
                        HttpMethod.OPTIONS.name()
                )
        );

        // 허용할 헤더 설정
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "Refresh-Token",
                "Access-Control-Allow-Origin",
                "Access-Control-Allow-Credentials",
                "X-Auth-Token"
        ));

        // 인증 정보 포함 설정
        configuration.setAllowCredentials(true);

        // 모든 경로에 대해 CORS 설정 적용
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}