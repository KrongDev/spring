package com.geonlee.spring.config;

import com.geonlee.spring.security.filter.JwtAuthenticationFilter;
import com.geonlee.spring.security.filter.JwtExceptionFilter;
import com.geonlee.spring.shared.exception.handler.AccessDenialHandlerImpl;
import com.geonlee.spring.shared.exception.handler.AuthenticationEntryPointImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    //
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtExceptionFilter jwtExceptionFilter;
    private final AccessDenialHandlerImpl accessDenialHandler;
    private final AuthenticationEntryPointImpl authenticationEntryPoint;

    private static final String[] PERMIT_PATHS = {
            "/api/auth/sign-in",
            "/api/auth/sign-up",
    };

    private static final String[] ALLOW_ORIGINS = {
            "http://localhost:3000",
    };

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // csrf 보호 비활성화
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable) // X-Frame-Options 비활성화
                )
                // HTTP 기본 인증 비활성화
                .httpBasic(AbstractHttpConfigurer::disable)
                // cors 설정
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // 기본 폼 로그인 비활성화
                .formLogin(AbstractHttpConfigurer::disable)
                // 세션 관리 정책 설정 - 모든 요청을 토큰을 통해 인증하므로 세션을 생성하지 않는다.
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 요청별 접근 권한 설정
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(PathRequest.toH2Console()).permitAll()
                        .requestMatchers(PERMIT_PATHS).permitAll()
                        .anyRequest().authenticated()
                )
                // JWT 필터 추가
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                // JWT 예외 발생시 처리하는 필터 추가
                .addFilterBefore(jwtExceptionFilter, JwtAuthenticationFilter.class)
                // 예외 처리 설정
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler(accessDenialHandler)
                        .authenticationEntryPoint(authenticationEntryPoint))

                .build();
    }

    // CORS 정책 설정
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();
        // 모든 요청 헤더 서용
        config.addAllowedHeader("*");
        // 모든 HTTP 메소드 허용
        config.addAllowedMethod("*");
        // 특정 도메인에서만 요청 허용
        config.setAllowedOrigins(List.of(ALLOW_ORIGINS));
        // Authorization 헤더 접근 가능하도록 설정
        config.addExposedHeader(HttpHeaders.AUTHORIZATION);
        // 쿠키 포함 요청 허용
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 모든 경로에 대해 이 CORS 정책 적용
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
