package com.whdcks3.portfolio.gory_server.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.whdcks3.portfolio.gory_server.data.models.squad.Squad;
import com.whdcks3.portfolio.gory_server.data.models.squad.SquadParticipant;
import com.whdcks3.portfolio.gory_server.data.models.squad.SquadParticipant.SquadParticipationStatus;
import com.whdcks3.portfolio.gory_server.data.models.user.EmailVerification;
import com.whdcks3.portfolio.gory_server.data.models.user.User;
import com.whdcks3.portfolio.gory_server.data.requests.SquadRequest;
import com.whdcks3.portfolio.gory_server.enums.Gender;
import com.whdcks3.portfolio.gory_server.enums.JoinType;
import com.whdcks3.portfolio.gory_server.enums.LockType;
import com.whdcks3.portfolio.gory_server.repositories.EmailVerificationRepository;
import com.whdcks3.portfolio.gory_server.repositories.SquadParticipantRepository;
import com.whdcks3.portfolio.gory_server.repositories.SquadRepository;
import com.whdcks3.portfolio.gory_server.repositories.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
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

    @Autowired
    private EmailVerificationRepository emailVerificationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Map<String, String> tokenMap = new HashMap<>();

    @BeforeEach
    void setUpUsers() throws Exception {
        tokenMap.clear();

        for (int i = 1; i <= 20; i++) {
            String email = "user" + i + "@test.com";
            String snsType = "kakao";
            String snsId = "kakao-" + i;
            String rawPassword = snsType + snsId;

            String gender = (i % 2 == 0) ? "M" : "F";

            int birthYear = 1950 + (i % 21);
            String birth = birthYear + "-01-01";

            String jsonBody = String.format("""
                    {
                        "email": "%s",
                        "snsType": "%s",
                        "snsId": "%s",
                        "name": "테스트유저%d",
                        "carrier": "SKT",
                        "phone": "010-1234-%04d",
                        "gender": "%s",
                        "birth": "%s",
                        "receiveEvent": "Y"
                    }
                    """, email, snsType, snsId, i, i, gender, birth);

            mockMvc.perform(post("/api/auth/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonBody))
                    .andExpect(status().isOk());

            User user = userRepository.findByEmail(email).orElseThrow();
            EmailVerification verification = emailVerificationRepository.findByUser(user)
                    .orElseThrow(() -> new RuntimeException("이메일 인증 정보 없음: " + email));
            verification.setVerified(true);
            emailVerificationRepository.save(verification);

            user.setLockType(LockType.NONE);
            user.setLockedUntil(null);
            user.setPassword(passwordEncoder.encode(rawPassword));
            user.setNickname("닉네임" + i);
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

    @BeforeEach
    void setCategory() throws Exception {

    }

    @Test
    @DisplayName("20명의 유저가 로그인 성공 후 토큰 발급 받음")
    void testTokenMapBuilt() {
        assertThat(tokenMap).hasSize(20);
        assertThat(tokenMap.get("user1@test.com")).startsWith("Bearer ");
        assertThat(tokenMap.get("user1@test.com")).isNotBlank();
    }

    @Test
    @DisplayName("user1이 모임 생성 후 user2가 참여")
    void testUser1CreatesSquadAndUser2Joins() throws Exception {
        String tokenUser1 = tokenMap.get("user1@test.com");

        String squadJson = String.format("""
                {
                    "title": "헬스 모임",
                    "category": "취미",
                    "description": "주 3회 운동하실 분 구해요.",
                    "regionMain": "서울",
                    "regionSub": "강남구",
                    "date": "%s",
                    "time": "10:30:00",
                    "genderRequirement": "ALL",
                    "minAge": 50,
                    "maxAge": 80,
                    "timeSpecified": "true",
                    "joinType": "DIRECT",
                    "maxParticipants": 5
                }
                """, LocalDate.now().plusDays(3));

        MvcResult result = mockMvc.perform(post("/api/squad/create")
                .header("Authorization", tokenUser1)
                .contentType("application/json")
                .content(squadJson))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        int pid = JsonPath.read(response, "$.pid");
        Long squadId = (long) pid;

        String tokenUser2 = tokenMap.get("user2@test.com");

        mockMvc.perform(post("/api/squad/" + squadId + "/join-or-token")
                .header("Authorization", tokenUser2))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("user2가 이미 참여한 모임(승인제 아님)에 다시 join-or-token 요청 -> chatToken 반환")
    void testUser2RejoinsAndGetsToken() throws Exception {
        String tokenUser1 = tokenMap.get("user1@test.com");

        String squadJson = String.format("""
                {
                    "title": "헬스 모임",
                    "category": "취미",
                    "description": "주 3회 운동하실 분 구해요.",
                    "regionMain": "서울",
                    "regionSub": "강남구",
                    "date": "%s",
                    "time": "10:30:00",
                    "genderRequirement": "ALL",
                    "minAge": 50,
                    "maxAge": 80,
                    "timeSpecified": "true",
                    "joinType": "DIRECT",
                    "maxParticipants": 5
                }
                """, LocalDate.now().plusDays(3));

        MvcResult result = mockMvc.perform(post("/api/squad/create")
                .header("Authorization", tokenUser1)
                .contentType("application/json")
                .content(squadJson))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        int pid = JsonPath.read(response, "$.pid");
        Long squadId = (long) pid;

        String tokenUser2 = tokenMap.get("user2@test.com");

        mockMvc.perform(post("/api/squad/" + squadId + "/join-or-token")
                .header("Authorization", tokenUser2))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/squad/" + squadId + "/join-or-token")
                .header("Authorization", tokenUser2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("joined"))
                .andExpect(jsonPath("$.chatToken").isString());
    }

    @Test
    @DisplayName("user3가 강퇴된 모임에 join-or-token 요청 시 403 반환")
    void testKickedUserCannotJoinOrGetToken() throws Exception {
        String tokenUser1 = tokenMap.get("user1@test.com");

        String squadJson = String.format("""
                {
                    "title": "헬스 모임",
                    "category": "취미",
                    "description": "주 3회 운동하실 분 구해요.",
                    "regionMain": "서울",
                    "regionSub": "강남구",
                    "date": "%s",
                    "time": "10:30:00",
                    "genderRequirement": "ALL",
                    "minAge": 50,
                    "maxAge": 80,
                    "timeSpecified": "true",
                    "joinType": "DIRECT",
                    "maxParticipants": 5
                }
                """, LocalDate.now().plusDays(3));

        MvcResult result = mockMvc.perform(post("/api/squad/create")
                .header("Authorization", tokenUser1)
                .contentType("application/json")
                .content(squadJson))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        int pid = JsonPath.read(response, "$.pid");
        Long squadId = (long) pid;

        String tokenUser2 = tokenMap.get("user3@test.com");

        mockMvc.perform(post("/api/squad/" + squadId + "/join-or-token")
                .header("Authorization", tokenUser2))
                .andExpect(status().isOk());

        User user3 = userRepository.findByEmail("user3@test.com").orElseThrow();
        Squad squad = squadRepository.findById(squadId).orElseThrow();
        SquadParticipant participant = squadParticipantRepository.findByUserAndSquad(user3, squad).orElseThrow();
        participant.setStatus(SquadParticipationStatus.KICKED_OUT);
        squadParticipantRepository.save(participant);

        mockMvc.perform(post("/api/squad/" + squadId + "/join-or-token")
                .header("Authorization", tokenUser2))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("user4가 모임 조건(성별) 불일치 시 join-or-token 실패")
    void testUser4JoinFailsDueToGenderConditionMismatch() throws Exception {
        String tokenUser1 = tokenMap.get("user1@test.com");

        String squadJson = String.format("""
                {
                    "title": "여성 독서 모임",
                    "category": "2여성만 참여 가능",
                    "description": "주 3회 운동하실 분 구해요.",
                    "regionMain": "서울",
                    "regionSub": "강남구",
                    "date": "%s",
                    "time": "10:30:00",
                    "genderRequirement": "FEMALE",
                    "minAge": 50,
                    "maxAge": 80,
                    "timeSpecified": "true",
                    "joinType": "DIRECT",
                    "maxParticipants": 5
                }
                """, LocalDate.now().plusDays(3));

        MvcResult result = mockMvc.perform(post("/api/squad/create")
                .header("Authorization", tokenUser1)
                .contentType("application/json")
                .content(squadJson))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        int pid = JsonPath.read(response, "$.pid");
        Long squadId = (long) pid;

        String tokenUser2 = tokenMap.get("user4@test.com");

        mockMvc.perform(post("/api/squad/" + squadId + "/join-or-token")
                .header("Authorization", tokenUser2))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("user4가 모임 조건(나이) 불일치 시 join-or-token 실패")
    void testUser4JoinFailsDueToAgeConditionMismatch() throws Exception {
        String tokenUser1 = tokenMap.get("user1@test.com");

        String squadJson = String.format("""
                {
                    "title": "여성 독서 모임",
                    "category": "2여성만 참여 가능",
                    "description": "주 3회 운동하실 분 구해요.",
                    "regionMain": "서울",
                    "regionSub": "강남구",
                    "date": "%s",
                    "time": "10:30:00",
                    "genderRequirement": "ALL",
                    "minAge": 50,
                    "maxAge": 60,
                    "timeSpecified": "true",
                    "joinType": "DIRECT",
                    "maxParticipants": 5
                }
                """, LocalDate.now().plusDays(3));

        MvcResult result = mockMvc.perform(post("/api/squad/create")
                .header("Authorization", tokenUser1)
                .contentType("application/json")
                .content(squadJson))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        int pid = JsonPath.read(response, "$.pid");
        Long squadId = (long) pid;

        String tokenUser2 = tokenMap.get("user4@test.com");

        mockMvc.perform(post("/api/squad/" + squadId + "/join-or-token")
                .header("Authorization", tokenUser2))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("user5가 승인제 모임에 참여 요청 시 pending 상태 반환")
    void testUser5JoinApprovalBaseSquad() throws Exception {
        String tokenUser1 = tokenMap.get("user1@test.com");

        String squadJson = String.format("""
                {
                    "title": "여성 독서 모임",
                    "category": "2여성만 참여 가능",
                    "description": "주 3회 운동하실 분 구해요.",
                    "regionMain": "서울",
                    "regionSub": "강남구",
                    "date": "%s",
                    "time": "10:30:00",
                    "genderRequirement": "ALL",
                    "minAge": 50,
                    "maxAge": 100,
                    "timeSpecified": "true",
                    "joinType": "APPROVAL",
                    "maxParticipants": 5
                }
                """, LocalDate.now().plusDays(3));

        MvcResult result = mockMvc.perform(post("/api/squad/create")
                .header("Authorization", tokenUser1)
                .contentType("application/json")
                .content(squadJson))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        int pid = JsonPath.read(response, "$.pid");
        Long squadId = (long) pid;

        String tokenUser2 = tokenMap.get("user5@test.com");

        mockMvc.perform(post("/api/squad/" + squadId + "/join-or-token")
                .header("Authorization", tokenUser2))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/squad/" + squadId + "/join-or-token")
                .header("Authorization", tokenUser2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("pending"))
                .andExpect(jsonPath("$.chatToken").doesNotExist());
    }

    @Test
    @DisplayName("user1이 user5 승인 후 -> user5  승인제 모임에 참여 요청 시 joined + chatToken 반환")
    void testUser5JoinApprovalThenRejoin() throws Exception {
        String tokenUser1 = tokenMap.get("user1@test.com");

        String squadJson = String.format("""
                {
                    "title": "여성 독서 모임",
                    "category": "2여성만 참여 가능",
                    "description": "주 3회 운동하실 분 구해요.",
                    "regionMain": "서울",
                    "regionSub": "강남구",
                    "date": "%s",
                    "time": "10:30:00",
                    "genderRequirement": "ALL",
                    "minAge": 50,
                    "maxAge": 100,
                    "timeSpecified": "true",
                    "joinType": "APPROVAL",
                    "maxParticipants": 5
                }
                """, LocalDate.now().plusDays(3));

        MvcResult result = mockMvc.perform(post("/api/squad/create")
                .header("Authorization", tokenUser1)
                .contentType("application/json")
                .content(squadJson))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        int pid = JsonPath.read(response, "$.pid");
        Long squadId = (long) pid;

        String tokenUser2 = tokenMap.get("user5@test.com");

        mockMvc.perform(post("/api/squad/" + squadId + "/join-or-token")
                .header("Authorization", tokenUser2))
                .andExpect(status().isOk());

        // User user5 = userRepository.findByEmail("user5@test.com").orElseThrow();
        // Squad squad = squadRepository.findById(squadId).orElseThrow();
        // SquadParticipant participant =
        // squadParticipantRepository.findByUserAndSquad(user5, squad).orElseThrow();

        User user5 = userRepository.findByEmail("user5@test.com").orElseThrow();

        mockMvc.perform(post("/api/squad/" + squadId + "/approve/" + user5)
                .header("Authorization", tokenUser1))
                .andExpect(status().isOk());

        // participant.setStatus(SquadParticipationStatus.JOINED);
        // squadParticipantRepository.save(participant);

        mockMvc.perform(post("/api/squad/" + squadId + "/join-or-token")
                .header("Authorization", tokenUser2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("joined"))
                .andExpect(jsonPath("$.chatToken").isString());
    }

    @Test
    @DisplayName("user6이 거절된 상태에서 join-or-token요청 시 실패")
    void testUser6RejectedCannotRejoin() throws Exception {
        String tokenUser1 = tokenMap.get("user1@test.com");

        String squadJson = String.format("""
                {
                    "title": "여성 독서 모임",
                    "category": "2여성만 참여 가능",
                    "description": "주 3회 운동하실 분 구해요.",
                    "regionMain": "서울",
                    "regionSub": "강남구",
                    "date": "%s",
                    "time": "10:30:00",
                    "genderRequirement": "ALL",
                    "minAge": 50,
                    "maxAge": 100,
                    "timeSpecified": "true",
                    "joinType": "APPROVAL",
                    "maxParticipants": 5
                }
                """, LocalDate.now().plusDays(3));

        MvcResult result = mockMvc.perform(post("/api/squad/create")
                .header("Authorization", tokenUser1)
                .contentType("application/json")
                .content(squadJson))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        int pid = JsonPath.read(response, "$.pid");
        Long squadId = (long) pid;

        String tokenUser2 = tokenMap.get("user6@test.com");

        mockMvc.perform(post("/api/squad/" + squadId + "/join-or-token")
                .header("Authorization", tokenUser2))
                .andExpect(status().isOk());

        // User user5 = userRepository.findByEmail("user6@test.com").orElseThrow();
        // Squad squad = squadRepository.findById(squadId).orElseThrow();
        // SquadParticipant participant =
        // squadParticipantRepository.findByUserAndSquad(user5, squad).orElseThrow();

        // participant.setStatus(SquadParticipationStatus.REJECTED);
        // squadParticipantRepository.save(participant);
        User user6 = userRepository.findByEmail("user6@test.com").orElseThrow();

        mockMvc.perform(post("/api/squad/" + squadId + "/reject/" + user6)
                .header("Authorization", tokenUser1))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/squad/" + squadId + "/join-or-token")
                .header("Authorization", tokenUser2))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("user7이 탈퇴 후 재참여 시 정상적으로 참여 및 토큰 발급")
    void testUser7LeaveAndRejoin() throws Exception {
        String tokenUser1 = tokenMap.get("user1@test.com");

        String squadJson = String.format("""
                {
                    "title": "여성 독서 모임",
                    "category": "2여성만 참여 가능",
                    "description": "주 3회 운동하실 분 구해요.",
                    "regionMain": "서울",
                    "regionSub": "강남구",
                    "date": "%s",
                    "time": "10:30:00",
                    "genderRequirement": "ALL",
                    "minAge": 50,
                    "maxAge": 100,
                    "timeSpecified": "true",
                    "joinType": "APPROVAL",
                    "maxParticipants": 5
                }
                """, LocalDate.now().plusDays(3));

        MvcResult result = mockMvc.perform(post("/api/squad/create")
                .header("Authorization", tokenUser1)
                .contentType("application/json")
                .content(squadJson))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        int pid = JsonPath.read(response, "$.pid");
        Long squadId = (long) pid;

        String tokenUser2 = tokenMap.get("user7@test.com");

        mockMvc.perform(post("/api/squad/" + squadId + "/join-or-token")
                .header("Authorization", tokenUser2))
                .andExpect(status().isOk());

        // user7@test.com이 모임을 떠나는 API 호출 필요
        mockMvc.perform(post("/api/squad" + squadId + "/leave")
                .header("Authorization", tokenUser2))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/squad/" + squadId + "/join-or-token")
                .header("Authorization", tokenUser2))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("user8이 방장에게 강퇴당한 후 재입장")
    void testUser8KickedSquad() throws Exception {
        String tokenUser1 = tokenMap.get("user1@test.com");

        String squadJson = String.format("""
                {
                    "title": "여성 독서 모임",
                    "category": "2여성만 참여 가능",
                    "description": "주 3회 운동하실 분 구해요.",
                    "regionMain": "서울",
                    "regionSub": "강남구",
                    "date": "%s",
                    "time": "10:30:00",
                    "genderRequirement": "ALL",
                    "minAge": 50,
                    "maxAge": 100,
                    "timeSpecified": "true",
                    "joinType": "APPROVAL",
                    "maxParticipants": 5
                }
                """, LocalDate.now().plusDays(3));

        MvcResult result = mockMvc.perform(post("/api/squad/create")
                .header("Authorization", tokenUser1)
                .contentType("application/json")
                .content(squadJson))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        int pid = JsonPath.read(response, "$.pid");
        Long squadId = (long) pid;

        String tokenUser2 = tokenMap.get("user8@test.com");

        mockMvc.perform(post("/api/squad/" + squadId + "/join-or-token")
                .header("Authorization", tokenUser2))
                .andExpect(status().isOk());

        User user8 = userRepository.findByEmail("user3@test.com").orElseThrow();

        mockMvc.perform(post("/api/squad/" + squadId + "/kick/" + user8)
                .header("Authorization", tokenUser2))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/squad/" + squadId + "/join-or-token")
                .header("Authorization", tokenUser2))
                .andExpect(status().isForbidden());
    }

}
