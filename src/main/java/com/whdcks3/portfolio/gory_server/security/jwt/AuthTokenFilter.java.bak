package com.whdcks3.portfolio.gory_server.security.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.whdcks3.portfolio.gory_server.security.service.CustomUserDetails;
import com.whdcks3.portfolio.gory_server.security.service.CustomerUserDetailsServiceImpl;

// OncePerRequestFilter requst중에서 필터링을 한번만 할수 있게 해줌
public class AuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    CustomerUserDetailsServiceImpl userDetailsServ;

    @Autowired
    JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // 요청 헤더에서 JWT 추출, "Bearer"로 시작하는 JWT를 추출
            String jwt = parseJwt(request);
            // JWT 유효성 검증
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                // JWT에서 사용자 이름 추출
                String email = jwtUtils.getUserNameFromJwtToken(jwt);
                // 사용자 정보 가져오기
                UserDetails userdetails = userDetailsServ.loadUserByUsername(email);
                // Username~Token은 스프링 시큐리티의 인증 객체로, 사용자 정보와 권한을 포함한다.
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userdetails, null, userdetails.getAuthorities());
                // 인증 관련 세부 정보를 추가하기 위함
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // 현재 요청의 인증 정보를 Security Context에 저장한다. 이후 요청은 인증된 사용자로 인식된다.
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) { // 예외처리
            System.err.println(e.getLocalizedMessage());
            System.err.println("인증 오류 발생!!! : " + e.getLocalizedMessage());
        }
        // 필터체인의 다음단계로 넘김
        filterChain.doFilter(request, response);
    }

    // JWT 추출 메서드
    public String parseJwt(HttpServletRequest req) {
        // 요청 헤더의 "Authroization"을 읽음
        String headerAuthorization = req.getHeader("Authorization");
        // 값이 존재하고 "Bearer " 로 시작하면 Bearer이후 부분을 반환
        if (StringUtils.hasText(headerAuthorization) && headerAuthorization.startsWith("Bearer ")) {
            return headerAuthorization.substring(7);
        }
        return null; // 없으면 null
    }
}
