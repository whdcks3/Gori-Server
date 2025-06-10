package com.whdcks3.portfolio.gory_server.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.whdcks3.portfolio.gory_server.data.models.RandomCode;
import com.whdcks3.portfolio.gory_server.data.models.user.User;
import com.whdcks3.portfolio.gory_server.repositories.RandomCodeRepository;
import com.whdcks3.portfolio.gory_server.repositories.UserRepository;
import com.whdcks3.portfolio.gory_server.service.AuthService;
import com.whdcks3.portfolio.gory_server.service.UserService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthRestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    @Autowired
    private RandomCodeRepository randomCodeRepository;

    @Autowired
    private UserRepository userRepository;

    private String token, activateToken;

    private static final Logger logger = LoggerFactory.getLogger(AuthRestControllerTest.class);

    private String nickname;

    private Map<String, User> users = new HashMap<>();
    private Map<String, Map<String, String>> tokens = new HashMap<>();

    @BeforeEach
    void setup() {
        // userRepository.deleteAll();
        User user = userRepository.findByEmail("sj012944@gmail.com").get();
        // activateToken = user.getActivationToken();
        nickname = user.getNickname();
        RandomCode code = randomCodeRepository.findByCode("sj012944@gmail.com");
        // authService.signUp(
        // new SignupRequest("sj012944@gmail.com", "kakao", "00000", "홍길동",
        // "01012341234", "M", "19970101", "true",
        // "", ""));

        // users.put("test1@test.com", testUser1);
        // users.put("test1@test.com", token);
    }

    @Test
    void testSignupNonExists() throws Exception {
        // {"email":"sj012944@gmail.com","snsType":"kakao","snsId":"00000","name":"홍길동","phone":"01012341234","phoneType":"SKT","birth":"19900101","gender":"M","receiveEvent":"true"}
        mockMvc.perform(post("/api/auth/signup")
                // .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        "{\"email\":\"sj012944@gmail.com\",\"snsType\":\"kakao\",\"snsId\":\"00000\",\"name\":\"홍길동\",\"phone\":\"01012341234\",\"phoneType\":\"SKT\",\"birth\":\"19900101\",\"gender\":\"M\",\"receiveEvent\":\"true\"}"))
                .andExpect(status().isOk());
        // .andExpect(jsonPath("$.nickname").exists());
    }

    @Test
    void testActivate() throws Exception {
        mockMvc.perform(get("/api/auth/activate?token=" + activateToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSend() throws Exception {
        mockMvc.perform(post("/api/auth/send")
                .param("email", "sj012944@gmail.com"))
                .andExpect(status().isOk());
    }

    @Test
    void testValidate() throws Exception {
        String email = "sj012944@gmail.com";
        RandomCode code = randomCodeRepository.findTopByEmailOrderByCreatedDesc(email);
        mockMvc.perform(post("/api/auth/validate")
                .param("email", email)
                .param("code", code.toString()))
                .andExpect(status().isOk())
                .andReturn();

    } // 아무코드 받아도 넘어감

    @Test
    void testMultiauth() throws Exception {
        mockMvc.perform(post("/api/auth/multiauth")
                .param("email", "sj012944@gmail.com"))
                .andExpect(status().isOk());
    }

    @Test
    void testUserName() throws Exception {
        mockMvc.perform(get("/api/user/generate_username"))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateNickName() throws Exception {
        mockMvc.perform(post("/api/user/update_nickname")
                .param("nickname", "??"))
                .andExpect(status().isOk());
    }

    @AfterEach
    void end() {
        // userRepository.findAll().get(0).getRoles().clear();
        // userRepository.deleteAll();
    }

}
