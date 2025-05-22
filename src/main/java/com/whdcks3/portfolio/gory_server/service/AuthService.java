package com.whdcks3.portfolio.gory_server.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.whdcks3.portfolio.gory_server.common.EmailUtils;
import com.whdcks3.portfolio.gory_server.data.models.RandomCode;
import com.whdcks3.portfolio.gory_server.data.models.user.EmailVerification;
import com.whdcks3.portfolio.gory_server.data.models.user.User;
import com.whdcks3.portfolio.gory_server.data.requests.SignupRequest;
import com.whdcks3.portfolio.gory_server.enums.LockType;
import com.whdcks3.portfolio.gory_server.exception.ValidationException;
import com.whdcks3.portfolio.gory_server.repositories.EmailVerificationRepository;
import com.whdcks3.portfolio.gory_server.repositories.RandomCodeRepository;
import com.whdcks3.portfolio.gory_server.repositories.UserRepository;
import com.whdcks3.portfolio.gory_server.security.jwt.JwtUtils;
import com.whdcks3.portfolio.gory_server.security.service.CustomerUserDetailsServiceImpl;

import java.util.Random;

@Service
public class AuthService {

    @Autowired
    RandomCodeRepository randomCodeRepository;

    @Autowired
    EmailVerificationRepository emailVerificationRepository;

    @Autowired
    EmailUtils emailUtils;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CustomerUserDetailsServiceImpl customUserDetailsServ;

    @Autowired
    JwtUtils jwtUtils;

    public boolean signUp(SignupRequest req) {
        System.out.println("Start signing up!");
        try {
            String imageName = "avatar_placeholder.png";
            User user = new User(req, passwordEncoder.encode(req.getSnsType() + req.getSnsId()), imageName);
            userRepository.save(user);
            sendActivationEmail(user);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getLocalizedMessage());
            return false;
        }

        return true;
    }

    public Map<String, String> authenticate(String email, String snsType, String snsId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));
        sendActivationEmail(user);
        return getTokensByUser(user);
    }

    public Map<String, String> refreshTokens(String refreshToken) {
        String email = jwtUtils.extractEmail(refreshToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        return getTokensByUser(user);
    }

    public Map<String, String> getTokensByUser(User user) {
        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", jwtUtils.generateAccessToken(user.touserDetails()));
        tokens.put("refreshToken", jwtUtils.generateRefreshToken(user.getEmail()));
        return tokens;
    }

    public String getUserSnsType(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.get().getSnsType();
    }

    public boolean validateUser(String email, String code) {
        RandomCode randomCode = randomCodeRepository.findTopByEmailOrderByCreatedDesc(email);
        System.out.println(randomCode == null);
        if (randomCode == null) {
            throw new ValidationException(200, "이메일이 존재하지 않습니다.");
        }
        if (randomCode.isExpired()) {
            throw new ValidationException(201, "만료된 코드입니다.");
        }
        if (!randomCode.getCode().equals(code)) {
            throw new ValidationException(202, "일치하지 않은 코드입니다.");
        }

        return true;
    }

    public boolean shouldLock(String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        if (user.isWithinLockedTime()) {
            System.out.println("계정 잠금, 해제 시간 : " + user.getLockedUntil());
            throw new ValidationException("반복된 시도로 계정이 잠겨, " + user.getLockedUntil() + " 이후에 시도 가능합니다.");
        }

        LocalDateTime time = LocalDateTime.now();

        user.getAuthAttempts().removeIf(attempts -> attempts.isBefore(time.minusHours(1)));
        user.getAuthAttempts().add(time);

        if (user.getAuthAttempts().size() > 5) {
            user.tooManyAttempts(time.plusMinutes(30));
            System.out.println("계정이 30분 잠겼습니다.");
        } else {
            user.setLockType(LockType.NONE);
            user.setLockedUntil(null);
        }
        userRepository.save(user);
        return true;
    }

    public void resetPassword(String email, String rawPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 이메일입니다."));
        String encodedPassword = passwordEncoder.encode(rawPassword);
        user.setPassword(encodedPassword);
        userRepository.save(user);
        System.out.println("비밀번호 초기화 완료");
    }

    public boolean activateUser(String token) {
        return emailVerificationRepository.findByToken(token)
                .filter(verification -> verification.getExpiredAt().isAfter(LocalDateTime.now()))
                .map(verification -> {
                    verification.setVerified(true);
                    verification.getUser().setLockType(LockType.NONE);
                    verification.getUser().setLockedUntil(null);
                    emailVerificationRepository.save(verification);
                    userRepository.save(verification.getUser());
                    return true;
                })
                .orElse(false);
    }

    public void sendEmail(String email) {

        RandomCode existingCode = randomCodeRepository.findTopByEmailOrderByCreatedDesc(email);
        if (existingCode != null && !existingCode.isExpired()) {
            System.out.println("이미 코드가 존재합니다: " + existingCode.getCode());
        }

        String code = String.valueOf(Math.abs(new Random().nextInt()) % 999999);
        code = "*".repeat(6 - code.length()) + code;
        System.out.println("code:" + code);

        String title = "인증 코드";
        String body = "인증 코드는: " + code;

        emailUtils.sendEmail(email, title, body);

        System.out.println("생성 코드: " + code);

        RandomCode randomCode = new RandomCode(email, code);
        randomCodeRepository.save(randomCode);
    }

    public void resendActivationToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("찾을 수 없는 계정입니다."));
        if (user.getLockType().equals(LockType.NONE)) {
            throw new IllegalStateException("이미 활성화된 계정입니다.");
        }
        sendActivationEmail(user);
    }

    public void sendActivationEmail(User user) {
        String token = UUID.randomUUID().toString();
        EmailVerification verification = new EmailVerification(user, token, LocalDateTime.now().plusHours(24),
                false);
        emailVerificationRepository.save(verification);
        emailUtils.sendVerificationEmail(user.getEmail(), token);
    }
}
