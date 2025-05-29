package com.whdcks3.portfolio.gory_server.security.jwt;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    private static final String PRIVATE_KEY_PATH = "src/main/resources/keys/private_key.pem";
    private static final String PUBLIC_KEY_PATH = "src/main/resources/keys/public_key.pem";

    private static final long ACCESS_TOKEN_EXPIRATION = 3600000;
    private static final long REFRESH_TOKEN_EXPIRATION = 604800000;

    private PrivateKey privateKey;
    private PublicKey publicKey;

    public JwtUtils() {
        try {
            generateKeysIfAbsent();
            this.privateKey = loadPrivateKey();
            this.publicKey = loadPublicKey();
        } catch (Exception e) {
            throw new RuntimeException("키 로드 중 오류 발생: " + e.getMessage(), e);
        }
    }

    private PrivateKey loadPrivateKey() throws Exception {
        Path privateKeyPath = Paths.get(PRIVATE_KEY_PATH);
        if (!Files.exists(privateKeyPath)) {
            throw new IllegalStateException("개인 키 파일이 존재하지 않습니다: " + privateKeyPath);
        }
        byte[] keyBytes = Files.readAllBytes(privateKeyPath);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }

    private PublicKey loadPublicKey() throws Exception {
        Path publicKeyPath = Paths.get(PUBLIC_KEY_PATH);
        if (!Files.exists(publicKeyPath)) {
            throw new IllegalStateException("공개 키 파일이 존재하지 않습니다: " + publicKeyPath);
        }
        byte[] keyBytes = Files.readAllBytes(publicKeyPath);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }

    private void generateKeysIfAbsent() throws Exception {
        Path privateKeyPath = Paths.get(PRIVATE_KEY_PATH);
        Path publicKeyPath = Paths.get(PUBLIC_KEY_PATH);

        if (!Files.exists(privateKeyPath) || !Files.exists(publicKeyPath)) {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            Files.createDirectories(privateKeyPath.getParent());
            Files.write(privateKeyPath, keyPair.getPrivate().getEncoded());
            Files.write(publicKeyPath, keyPair.getPublic().getEncoded());

            System.out.println("RSA 키가 생성되었습니다.");
        }
    }

    public String generateAccessToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

    public String generateRefreshToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION))
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

    public Claims validateJwtToken(String authToken) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .build()
                    .parseClaimsJws(authToken)
                    .getBody();
        } catch (MalformedJwtException e) {
            throw new IllegalArgumentException("토큰 형식이 잘못되었습니다.");
        } catch (ExpiredJwtException e) {
            throw new IllegalArgumentException("토큰이 만료되었습니다.");
        } catch (UnsupportedJwtException e) {
            throw new IllegalArgumentException("지원되지 않는 토큰 형식입니다.");
        } catch (Exception e) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }
    }

    public String extractEmail(String token) {
        return validateJwtToken(token).getSubject();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = validateJwtToken(token);
        String username = claims.getSubject();

        List<SimpleGrantedAuthority> authorities = ((List<?>) claims.get("roles")).stream()
                .map(role -> new SimpleGrantedAuthority((String) role))
                .collect(Collectors.toList());

        return new UsernamePasswordAuthenticationToken(username, null, authorities);
    }
}
