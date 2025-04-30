package com.whdcks3.portfolio.gory_server.security.jwt;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

// 인증되지 않은 사용자 접근 처리
@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {
    private static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class);

    @Override
    // Spring security 인증 예외
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {
        // 인증되지 않은 접근 시 에러 로그
        logger.error("Unauthorized error: {}", authException.getMessage());
        // JSON 형식으로 받음
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        final Map<String, Object> body = new HashMap<>();

        // 요청된 속성값 가져오기
        String failedType = (String) request.getAttribute("failedType");
        System.out.println("failedType: " + failedType);
        if (failedType != null) {
            if (failedType.equals("locked")) {
                String lockedTime = (String) request.getAttribute("lockedTime");
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                body.put("status", HttpServletResponse.SC_FORBIDDEN);
                body.put("error", "Account Locked");
                body.put("message", "계정이 잠겨있습니다. " + lockedTime + "에 잠금이 해제됩니다.");
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
                body.put("error", "Unauthorized");
                body.put("message", authException.getMessage());
            }
        }
        // response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // 기본 응답 생성
        // final Map<String, Object> body = new HashMap<>();
        // // HTTP 상태 코드(401)
        // body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        // // 에러 유형
        // body.put("error", "Unauthorized");
        // // 인증 실패 메시지
        // body.put("message", authException.getMessage());
        // 클라이언트가 접근한 경로
        body.put("path", request.getServletPath());
        // JSON을 직렬화
        final ObjectMapper mapper = new ObjectMapper();
        // body 객체를 JSON 형식으로 변환후 본문에 작성
        mapper.writeValue(response.getOutputStream(), body);

    }
}
