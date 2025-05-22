package com.whdcks3.portfolio.gory_server.controllers;

import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.whdcks3.portfolio.gory_server.common.EmailUtils;
import com.whdcks3.portfolio.gory_server.data.requests.SignupRequest;
import com.whdcks3.portfolio.gory_server.data.responses.ErrorResponse;
import com.whdcks3.portfolio.gory_server.repositories.UserRepository;
import com.whdcks3.portfolio.gory_server.security.jwt.JwtUtils;
import com.whdcks3.portfolio.gory_server.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

// @CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Authentication", description = "회원 인증 및 회원가입 등")
@RestController
@RequestMapping("/api/auth")
public class AuthRestController {

    @Autowired
    EmailUtils emailUtils;

    @Autowired
    JavaMailSender mailsender;

    @Autowired
    AuthService authService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserRepository userRepository;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtils jwtUtills;

    @Operation(summary = "회원가입", description = "사용자 회원가입 API")
    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest req) {
        System.out.println("signup controller");
        System.out.println(req);
        if (!authService.signUp(req)) {
            return ResponseEntity.badRequest().body(new ErrorResponse(91, "회원 가입에 오류가 발생했습니다"));
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/signin")
    public ResponseEntity<Map<String, String>> authenticateUser(@RequestParam String email,
            @RequestParam String snsType, @RequestParam String snsId) {
        System.out.println(email + ", " + snsType + ", " + snsId);
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, snsType + snsId));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return ResponseEntity.ok(authService.authenticate(email, snsType, snsId));
    }

    @PostMapping("/repassword")
    public ResponseEntity<?> rePassword(@RequestParam String email, @RequestParam String rawPassword) {
        authService.resetPassword(email, rawPassword);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refresh(@RequestHeader("Authorization") String refreshToken) {
        refreshToken = refreshToken.replace("Bearer ", "");
        return ResponseEntity.ok(authService.refreshTokens(refreshToken));
    }

    @PostMapping("/resend-email")
    public ResponseEntity<?> resendActivationToken(@RequestParam String email) {
        authService.resendActivationToken(email);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/send")
    public ResponseEntity<?> sendEmail(@RequestParam String email) {
        authService.sendEmail(email);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validatingUser(@RequestParam String email, @RequestParam String code) {
        authService.validateUser(email, code);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/activate")
    public ResponseEntity<?> activateUser(@RequestParam String token) {
        return authService.activateUser(token) ? ResponseEntity.ok("Your account has been activated.")
                : ResponseEntity.badRequest().body("Invalid authentication token.");
    }

    @PostMapping("/multiauth")
    public ResponseEntity<?> multiAuthentication(@RequestParam String email) {
        authService.shouldLock(email);
        return ResponseEntity.ok().build();
    }
}
