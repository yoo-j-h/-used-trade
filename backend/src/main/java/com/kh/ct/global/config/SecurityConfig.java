package com.kh.ct.global.config;

import com.kh.ct.global.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        //인증없이 가능한 경우

                        ///api/auth/password
                        .requestMatchers(HttpMethod.POST,"/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST,"/api/auth/refresh").permitAll()
                        .requestMatchers(HttpMethod.POST,"/api/emps").permitAll()
                        .requestMatchers(HttpMethod.POST,"/api/auth/password/**").permitAll()
                        .requestMatchers(HttpMethod.POST,"/api/emps/findId").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/emps/checkId").permitAll()
                        .requestMatchers(HttpMethod.GET,  "/api/emps/empNo/preview").permitAll()
                        .requestMatchers("/api/emps/me/**").authenticated() // 내 정보 조회 등은 인증 필수 (와일드카드보다 우선순위 높음)
                        .requestMatchers(HttpMethod.GET, "/api/emps/*/airline").permitAll() // 임시: 테스트용 (나중에 authenticated()로 변경)
                        .requestMatchers(HttpMethod.POST,"/api/passwordCode/**").permitAll()

                        .requestMatchers("/api/chat").permitAll()
                        .requestMatchers(HttpMethod.POST,"/api/health/preview").permitAll()
                        .requestMatchers(HttpMethod.POST,"/api/health/save").permitAll()
                        .requestMatchers(HttpMethod.POST,"/api/airline-applications").permitAll()
                        .requestMatchers("/api/account-activation/**").permitAll()
                        .requestMatchers("/api/questions/**").permitAll()
                        //인증
                        .requestMatchers("/api/file/**").permitAll()


                        //슈퍼 관리자 전용
                        .requestMatchers("/api/super-admin/**").hasRole("SUPER_ADMIN")
                        .requestMatchers("/api/common/codes/**").permitAll()
                        .requestMatchers("/api/airlines").permitAll()
                        .requestMatchers("/api/airports").permitAll()
                        .requestMatchers("/api/file/download/**").permitAll()
                        
                        // 항공편 API: 일반 직원은 조회만 가능, 관리자는 모든 작업 가능
                        .requestMatchers(HttpMethod.GET, "/api/flight-schedules/sync").permitAll() // 동기화 엔드포인트는 인증 없이 접근 가능
                        .requestMatchers(HttpMethod.GET, "/api/flight-schedules").permitAll()
                        // .requestMatchers(HttpMethod.GET, "/api/flight-schedules/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/flight-schedules/**").hasAnyRole("AIRLINE_ADMIN", "SUPER_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/flight-schedules/**").hasAnyRole("AIRLINE_ADMIN", "SUPER_ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/flight-schedules/**").hasAnyRole("AIRLINE_ADMIN", "SUPER_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/flight-schedules/**").hasAnyRole("AIRLINE_ADMIN", "SUPER_ADMIN")
                        .requestMatchers("/api/emp/**").authenticated()

                        //관리자 전용
                        .requestMatchers(HttpMethod.GET, "/api/members").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/members/search").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/members/**").hasRole("ADMIN")
                        .requestMatchers("/api/dashboard/admin/**").permitAll()
                        .requestMatchers("/api/admin/attendance/**").permitAll()  // 관리자 근태 관리 API
                        .requestMatchers("/api/health/admin/**").permitAll() // 건강 관리자 API
                        // 알림 API: SSE 스트림은 EventSource 특성상 커스텀 헤더를 보낼 수 없어 permitAll 처리
                        .requestMatchers("/api/notifications/stream").permitAll() // SSE 연결 (컨트롤러에서 토큰 검증)
                        .requestMatchers("/api/notifications/**").authenticated() // 나머지 알림 API는 인증 필요
                        //나머지경로
                        .requestMatchers("/api/settings/**").permitAll()
                        .requestMatchers("/ws/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(Arrays.asList("http://localhost:5173", "http://localhost:5174"));
        corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.setAllowCredentials(true); //인증정보를 포함한 cors요청 허용
        corsConfiguration.setMaxAge(3600L); //1시간

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }



}
