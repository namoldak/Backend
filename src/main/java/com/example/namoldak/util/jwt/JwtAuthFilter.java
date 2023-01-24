package com.example.namoldak.util.jwt;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// 기능 : JWT 필터
@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    public final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String token = jwtUtil.resolveToken(request);

        if(token != null) {
            if(jwtUtil.validateToken(token)){
                // 3. 토큰이 유효하다면 토큰에서 정보를 가져와 Authentication 에 세팅
                Claims info = jwtUtil.getUserInfoFromToken(token);
                setAuthentication(info.getSubject());
            }
        }
        // 4. 다음 필터로 넘어간다
        filterChain.doFilter(request, response);
    }

    public void setAuthentication(String loginId) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = jwtUtil.createAuthentication(loginId);
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }
}
