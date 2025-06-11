package com.whdcks3.portfolio.gory_server.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.whdcks3.portfolio.gory_server.data.models.squad.SquadParticipant;
import com.whdcks3.portfolio.gory_server.data.models.squad.SquadParticipant.SquadParticipationStatus;
import com.whdcks3.portfolio.gory_server.data.models.user.User;
import com.whdcks3.portfolio.gory_server.data.requests.SignupRequest;
import com.whdcks3.portfolio.gory_server.data.requests.SquadRequest;
import com.whdcks3.portfolio.gory_server.enums.Gender;
import com.whdcks3.portfolio.gory_server.enums.JoinType;
import com.whdcks3.portfolio.gory_server.enums.LockType;
import com.whdcks3.portfolio.gory_server.repositories.SquadParticipantRepository;
import com.whdcks3.portfolio.gory_server.repositories.SquadRepository;
import com.whdcks3.portfolio.gory_server.repositories.UserRepository;
import com.whdcks3.utils.TestUserUtil;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
public class SquadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SquadRepository squadRepository;

    @Autowired
    private SquadParticipantRepository squadParticipantRepository;

    private Map<String, String> tokenMap;

    @BeforeEach
    void setupUsers() throws Exception {
        tokenMap = new HashMap<>();

        for (int i = 1; i < 20; i++) {
            SignupRequest req = TestUserUtil.createSignupRequest(i);
            System.out.println("회원가입 JSON: " + objectMapper.writeValueAsString(req));

            mockMvc.perform(post("/api/auth/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isOk());

            User user = userRepository.findByEmail(req.getEmail()).orElseThrow();
            user.setLockType(LockType.NONE);
            user.setLockedUntil(null);
            userRepository.save(user);

            MvcResult result = mockMvc.perform(post("/api/auth/signin")
                    .param("email", req.getEmail())
                    .param("snsType", req.getSnsType())
                    .param("snsId", req.getSnsId()))
                    .andExpect(status().isOk())
                    .andReturn();

            String responseJson = result.getResponse().getContentAsString();
            String accessToken = JsonPath.read(responseJson, "$.accessToken");
            tokenMap.put("email", "Bearer " + accessToken);
        }
    }

    @Test
    @DisplayName("20명의 유저가 로그인 성공 후 토큰 발급 받음")
    void testTokenMapBuilt() {
        assertThat(tokenMap).hasSize(20);
        assertThat(tokenMap.get("user1@test.com")).startsWith("Bearer ");
        assertThat(tokenMap.get("user1@test.com")).isNotBlank();
    }

    @Test
    @DisplayName("user1이 모임을 생성하고 user2가 참여한다")
    void testUser1CreatesAndUser2JoinSquad() throws Exception {
        String user1Email = "user1@test.com";
        String token1 = tokenMap.get(user1Email);

        String user2Email = "user2@test.com";
        String token2 = tokenMap.get(user2Email);

        SquadRequest req = new SquadRequest();
        req.setTitle("헬스 모임");
        req.setCategory("취미");
        req.setDescription("주 3회 운동하실 분 구함");
        req.setDate(LocalDate.now().plusDays(3));
        req.setTime(LocalTime.of(10, 30, 0));
        req.setGenderRequirement(Gender.ALL);
        req.setMaxAge(80);
        req.setMinAge(50);
        req.setMaxParticipants(5);
        req.setJoinType(JoinType.DIRECT);
        req.setRegionMain("서울시");
        req.setRegionSub("강남구");
        req.setTimeSpecified(true);

        MvcResult result = mockMvc.perform(post("/api/squad")
                .header("Authorizatiion", token1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        Long squadId = JsonPath.read(responseJson, "$.pid");

        mockMvc.perform(post("/api/squad/" + squadId + "/join")
                .header("Authorization", token2))
                .andExpect(status().isOk());
    }

    // @BeforeEach
    // void setup() {
    // if (!userRepository.existsByEmail("sj012944@gmail.com")) {
    // User user = new User();
    // user.setEmail("sj012944@gmail.com");
    // user.setSnsType("구글");
    // user.setSnsId("tjwhdcks1234");
    // user.setNickname("오류안뜨길");
    // userRepository.save(user);
    // }

    // if (!userRepository.existsByEmail("sjmj01@naver.com")) {
    // User secondUser = new User();
    // secondUser.setEmail("sjmj01@naver.com");
    // secondUser.setSnsType("네이버");
    // secondUser.setSnsId("whdcks123");
    // secondUser.setNickname("두번째유저");
    // userRepository.save(secondUser);
    // }

    // if (!userRepository.existsByEmail("adwc123@gmail.com")) {
    // User thirdUser = new User();
    // thirdUser.setEmail("adwc123@gmail.com");
    // thirdUser.setSnsType("구글");
    // thirdUser.setSnsId("cadks1");
    // thirdUser.setNickname("세번째유저");
    // }

    // SquadParticipant participant = new SquadParticipant();
    // participant.setUser(null);
    // participant.setSquad(null);
    // participant.setStatus(SquadParticipationStatus.PENDING);
    // squadParticipantRepository.save(participant);
    // }

    // @WithMockUser(username = "sj012944@gmail.com", roles = "USER")
    // @Test
    // void testCreateSquad() throws Exception {
    // String requestBody = """
    // {
    // "title": "오류만안나면돼",
    // "category": "취미",
    // "regionMain": "서울",
    // "regionSub": "강남구",
    // "description": "테스트입니다.",
    // "minAge": 50,
    // "maxAge": 70,
    // "date": "2025-06-25",
    // "time": "18:00:00",
    // "timeSpecified": true,
    // "joinType": "DIRECT",
    // "genderRequirement": "ALL",
    // "maxParticipants": 10
    // }
    // """;

    // mockMvc.perform(post("/api/squad/create")
    // .contentType(MediaType.APPLICATION_JSON)
    // .content(requestBody))
    // .andExpect(status().isOk());
    // }

    // @WithMockUser(roles = "USER")
    // @Test
    // void testModifySquad() throws Exception {
    // mockMvc.perform(put("/api/squad/modify/1")
    // .contentType(MediaType.APPLICATION_JSON)
    // .content("""
    // {
    // "title": "오류만안나면돼",
    // "category": "운동",
    // "regionMain": "서울",
    // "regionSub": "강남구",
    // "description": "수정 내용입니다.",
    // "minAge": 50,
    // "maxAge": 70,
    // "date": "2025-07-01",
    // "time": "19:00:00",
    // "timeSpecified": true,
    // "joinType": "APPROVAL",
    // "genderRequirement": "ALL",
    // "maxParticipants": 8
    // }
    // """))
    // .andExpect(status().isOk());
    // }

    // @WithMockUser(roles = "USER")
    // @Test
    // void testDeleteSquad() throws Exception {
    // mockMvc.perform(delete("/api/squad/delete/1")
    // .param("isForcedDelete", "false"))
    // .andExpect(status().isOk());
    // }

    // @WithMockUser(roles = "USER")
    // @Test
    // void testMySquads() throws Exception {
    // mockMvc.perform(get("/api/squad/mine"))
    // .andExpect(status().isOk());
    // }

    // @WithMockUser(roles = "USER")
    // @Test
    // void testHomeSquads() throws Exception {
    // mockMvc.perform(get("/api/squad/home")
    // .param("regionMain", "서울"))
    // .andExpect(status().isOk());
    // }

    // @WithMockUser(roles = "USER")
    // @Test
    // void testDetailSquad() throws Exception {
    // mockMvc.perform(get("/api/squad/1"))
    // .andExpect(status().isOk());
    // }

    // @WithMockUser(roles = "USER")
    // @Test
    // void testCloseSquad() throws Exception {
    // mockMvc.perform(put("/api/squad/1/close"))
    // .andExpect(status().isOk());
    // }

    // @WithMockUser(roles = "USER")
    // @Test
    // void testApproveParticipant() throws Exception {
    // mockMvc.perform(post("/api/squad/1/approve/2"))
    // .andExpect(status().isOk());
    // }

    // @WithMockUser(roles = "USER")
    // @Test
    // void testRejectParticipant() throws Exception {
    // mockMvc.perform(post("/api/squad/1/reject/2"))
    // .andExpect(status().isOk());
    // }

    // @WithMockUser(roles = "USER")
    // @Test
    // void testKickOffParticipant() throws Exception {
    // mockMvc.perform(post("/api/squad/1/kick/2"))
    // .andExpect(status().isOk());
    // }

    // @WithMockUser(roles = "USER")
    // @Test
    // void testLeaveSquad() throws Exception {
    // mockMvc.perform(post("/api/squad/1/leave"))
    // .andExpect(status().isOk());
    // }

    // @WithMockUser(roles = "USER")
    // @Test
    // void testJoinOrGetToken() throws Exception {
    // mockMvc.perform(post("/api/squad/1/join-or-token"))
    // .andExpect(status().isOk());
    // }

    // @WithMockUser(roles = "USER")
    // @Test
    // void testGetParticipants() throws Exception {
    // mockMvc.perform(get("/api/squad/1/participants"))
    // .andExpect(status().isOk());
    // }
}
