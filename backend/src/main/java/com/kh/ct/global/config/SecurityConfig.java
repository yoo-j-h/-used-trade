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
                        .requestMatchers(HttpMethod.POST,"/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST,"/api/emps").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/emps/checkId").permitAll()
                        .requestMatchers(HttpMethod.GET,  "/api/emps/empNo/preview").permitAll()
                        .requestMatchers(HttpMethod.POST,"/api/passwordCode/**").permitAll()

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
                        .requestMatchers("/api/super-admin/**").permitAll()
                        .requestMatchers("/api/file/download/**").permitAll()
                        .requestMatchers("/api/flight-schedules/**").authenticated()

                        .requestMatchers("/api/common/codes/**").permitAll()
                        .requestMatchers("/api/airlines").permitAll()
                        .requestMatchers("/api/airports").permitAll()
                        .requestMatchers("/api/super-admin/**").permitAll()
                        .requestMatchers("/api/file/download/**").permitAll()
                        .requestMatchers("/api/flight-schedules/**").authenticated()
                        .requestMatchers("/api/emp/**").authenticated()

                        //관리자 전용
                        .requestMatchers(HttpMethod.GET, "/api/members").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/members/search").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/members/**").hasRole("ADMIN")
                        .requestMatchers("/api/dashboard/admin/**").permitAll()
                        .requestMatchers("/api/admin/attendance/**").permitAll()  // 관리자 근태 관리 API
                        //나머지경로
                        .requestMatchers("/api/settings/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
        corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
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
