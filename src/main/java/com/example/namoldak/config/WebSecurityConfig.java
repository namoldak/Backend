package com.example.namoldak.config;

import com.example.namoldak.util.jwt.JwtAuthFilter;
import com.example.namoldak.util.GlobalResponse.SecurityExceptionFilter;
import com.example.namoldak.util.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Collections;


// 기능 : Spring Security 사용에 필요한 설정
@Configuration
@Slf4j
@RequiredArgsConstructor
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class WebSecurityConfig {
    private final JwtUtil jwtUtil;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        try {
            // CSRF 설정
            http.csrf().disable();

            http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

            http.httpBasic().disable()
                    .authorizeRequests()
                    .antMatchers("/auth/**").permitAll()
                    .antMatchers(HttpMethod.GET, "/posts/myPost").authenticated()
                    .antMatchers(HttpMethod.GET, "/**").permitAll()
                    .antMatchers("/ws-stomp").permitAll()
                    .antMatchers("/signal/**").permitAll()
                    .antMatchers("/signal").permitAll()
                    .anyRequest().authenticated()
                    .and()
                    .addFilterBefore(new JwtAuthFilter(jwtUtil),
                            UsernamePasswordAuthenticationFilter.class)
                    .addFilterBefore(new SecurityExceptionFilter(), JwtAuthFilter.class);
            http.cors();
        } catch (Exception e) {
            log.info(e.getMessage());
        }
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("http://localhost:3000");
        config.addAllowedOrigin("https://namoldak.com.s3.ap-northeast-2.amazonaws.com");
        config.addAllowedOrigin("https://namoldak.com");
        config.addAllowedOrigin("https://d3j37rx7mer6cg.cloudfront.net");
        // addExposedHeader : 프론트에서 헤더의 해당 값에 접근할 수 있게 허락해주는 옵션
        config.addExposedHeader(JwtUtil.ACCESS_TOKEN);
        config.addExposedHeader(JwtUtil.REFRESH_TOKEN);
        config.addExposedHeader(JwtUtil.KAKAO_TOKEN);
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        config.setAllowedOriginPatterns(Collections.singletonList("*"));
        config.setAllowCredentials(true);
//        config.setMaxAge(24*60*60L); // 쿠키 만료 시간 : 24시간
        config.validateAllowCredentials();
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}