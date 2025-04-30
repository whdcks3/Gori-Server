package com.whdcks3.portfolio.gory_server.security.jwt;

import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

import com.whdcks3.portfolio.gory_server.security.service.*;

@Component
public class JwtUtils {
    // JWT 작업에서 발생하는 문제 로깅
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    // JWT를 서명하고 검증하기 위한 비밀 키
    @Value("${gory.app.jwtSecret}")
    private String jwtSecret;

    // 토큰의 유효 기간을 설정
    @Value("${gory.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    // JWT 토큰 생성
    public String generateJwtToken(Authentication authentication) {
        CustomUserDetails userPrincipal = (CustomUserDetails) authentication.getPrincipal();

        return Jwts.builder()
                // 사용자 이름을 토큰의 주체로 설정
                .setSubject((userPrincipal.getUsername()))
                // 토큰발행 시각 설정
                .setIssuedAt(new Date())
                // 토큰 만료 시간을 현재 시간 + jwtExpirationMs로 설정
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs * 365))
                // 지정된 알고리즘과 비밀키로 토큰 서명
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                // JWT 문자열 반환
                .compact();
    }

    // JWT 사용자 이름 추출
    public String getUserNameFromJwtToken(String token) {
        // 객체를 반환하고, 서명 검증을 위한 비밀키 설정, JWT를 파싱하고 서명을 확인하여 토큰에 포함된 클레임을 가져오고,
        // 토큰에서 클레임 정보를 반환하고, 클레임 중에서 subject(주체:이름) 필드 값을 반환
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }

    // JWT 유효성 검증
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) { // 서명이 올바르지 않은 경우
            logger.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) { // 토큰의 형식이 올바르지 않은 경우
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) { // 토큰이 만료된 경우
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) { // 지원하지 않는 형식의 토큰일 경우
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) { // 토큰 문자열이 비어 있거나 null인 경우
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }
}
