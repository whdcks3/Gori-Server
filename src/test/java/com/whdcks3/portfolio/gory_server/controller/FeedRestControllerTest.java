package com.whdcks3.portfolio.gory_server.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeEditor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import com.jayway.jsonpath.JsonPath;
import com.whdcks3.portfolio.gory_server.data.models.user.EmailVerification;
import com.whdcks3.portfolio.gory_server.data.models.user.User;
import com.whdcks3.portfolio.gory_server.enums.LockType;
import com.whdcks3.portfolio.gory_server.repositories.EmailVerificationRepository;
import com.whdcks3.portfolio.gory_server.repositories.FeedRepository;
import com.whdcks3.portfolio.gory_server.repositories.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class FeedRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FeedRepository feedRepository;

    @Autowired
    private EmailVerificationRepository emailVerification;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Map<String, String> tokenMap = new HashMap<>();

    @BeforeEach
    void setUpUsers() throws Exception {
        tokenMap.clear();

        for (int i = 1; i <= 25; i++) {
            String email = "user" + i + "@test.com";
            String snsType = "kakao";
            String snsId = "kakao-" + i;
            String name = "testUser-" + i;
            String rawPassword = snsType + snsId;
            String gender = (i % 2 == 0) ? "M" : "F";
            int birthYear = 1950 + (i % 25);
            String birth = birthYear + "-01-01";
            String jsonBody = String.format("""
                    {
                        "email": "%s",
                        "snsType": "%s",
                        "snsId": "%s",
                        "name": "%s",
                        "carrier": "LG",
                        "phone": "010-1234-%04d",
                        "gender": "%s"
                        "birth": "%s",
                        "receiveEvent": "Y"
                        }
                    """, email, snsType, snsId, name, rawPassword, gender, birthYear, birth);
            mockMvc.perform(post("/api/auth/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonBody))
                    .andExpect(status().isOk());

            User user = userRepository.findByEmail(email).orElseThrow();
            EmailVerification verification = emailVerification.findByUser(user)
                    .orElseThrow(() -> new RuntimeException("이메일 인증 정보가 없습니다: " + email));
            verification.setVerified(true);
            emailVerification.save(verification);

            user.setLockType(LockType.NONE);
            user.setLockedUntil(null);
            user.setPassword(passwordEncoder.encode(rawPassword));
            user.setNickname("닉네임 " + i);
            userRepository.save(user);

            MvcResult result = mockMvc.perform(post("/api/auth/signin")
                    .param("email", email)
                    .param("snsType", snsType)
                    .param("snsId", snsId))
                    .andExpect(status().isOk())
                    .andReturn();

            String response = result.getResponse().getContentAsString();
            String accessToken = JsonPath.read(response, "$.accessToken");
            tokenMap.put(email, "Bearer " + accessToken);
        }
    }

    @Test
    @DisplayName("25명의 유저가 로그인 성공 후 토큰 발급을 받음, 테스트주체인 user1 토큰 확인")
    void testTokenMapBuilt() {
        assertThat(tokenMap).hasSize(25);
        assertThat(tokenMap.get("user1@test.com")).startsWith("Bearer ");
        assertThat(tokenMap.get("user1@test.com")).isNotBlank();
    }

    @Test
    @DisplayName("user1이 피드 생성")
    void testUser1CreateFeed() throws Exception {

    }

    public ResultActions createFeed(int id, String content, String Category, ) throws Exception {

    }
}
