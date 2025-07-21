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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.spring5.expression.Mvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.whdcks3.portfolio.gory_server.data.models.squad.Squad;
import com.whdcks3.portfolio.gory_server.data.models.squad.SquadParticipant;
import com.whdcks3.portfolio.gory_server.data.models.squad.SquadParticipant.SquadParticipationStatus;
import com.whdcks3.portfolio.gory_server.data.models.user.EmailVerification;
import com.whdcks3.portfolio.gory_server.data.models.user.User;
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
public class SquadRestControllerTest {

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
        MvcResult result = createSquadAprrovalDefault(1).andExpect(status().isOk()).andReturn();
        Long squadId = extractSquadId(result);
        joinOrGetToken(squadId, 2).andExpect(status().isOk());
    }

    @Test
    @DisplayName("user2가 이미 참여한 모임(승인제 아님)에 다시 join-or-token 요청 -> chatToken 반환")
    void testUser2RejoinsAndGetsToken() throws Exception {
        MvcResult result = createSquadDirectDefault(1).andExpect(status().isOk()).andReturn();
        Long squadId = extractSquadId(result);
        joinOrGetToken(squadId, 2).andExpect(status().isOk());
        joinOrGetTokenAndExpectJoined(squadId, 2);
    }

    @Test
    @DisplayName("user3가 강퇴된 모임에 join-or-token 요청 시 403 반환")
    void testKickedUserCannotJoinOrGetToken() throws Exception {
        MvcResult result = createSquadDirectDefault(1).andExpect(status().isOk()).andReturn();
        Long squadId = extractSquadId(result);
        joinOrGetToken(squadId, 3).andExpect(status().isOk());
        kickParticipant(squadId, 1, 3).andExpect(status().isOk());
        joinOrGetToken(squadId, 3).andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("user4가 모임 조건(성별) 불일치 시 join-or-token 실패")
    void testUser4JoinFailsDueToGenderConditionMismatch() throws Exception {
        MvcResult result = createSquadDefault(1, 50, 100, Gender.FEMALE, JoinType.DIRECT).andExpect(status().isOk())
                .andReturn();
        Long squadId = extractSquadId(result);
        joinOrGetToken(squadId, 4).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("user5가 모임 조건(나이) 불일치 시 join-or-token 실패")
    void testUser4JoinFailsDueToAgeConditionMismatch() throws Exception {
        MvcResult result = createSquadDefault(1, 50, 60, Gender.ALL, JoinType.DIRECT).andExpect(status().isOk())
                .andReturn();
        Long squadId = extractSquadId(result);
        joinOrGetToken(squadId, 5).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("user6이 승인제 모임에 참여 요청 시 pending 상태 반환")
    void testUser5JoinApprovalBaseSquad() throws Exception {
        MvcResult result = createSquadAprrovalDefault(1).andExpect(status().isOk()).andReturn();
        Long squadId = extractSquadId(result);
        joinOrGetToken(squadId, 6).andExpect(status().isOk());
        joinOrGetTokenAndExpectPending(squadId, 6);
    }

    @Test
    @DisplayName("user1이 user7 승인 후 -> user7  승인제 모임에 참여 요청 시 joined + chatToken 반환")
    void testUser7JoinApprovalThenRejoin() throws Exception {
        MvcResult result = createSquadAprrovalDefault(1).andExpect(status().isOk()).andReturn();
        ;
        Long squadId = extractSquadId(result);
        joinOrGetTokenAndExpectReqeustedNoToken(squadId, 7);
        joinOrGetTokenAndExpectPendingNoToken(squadId, 7);
        approveParticipant(squadId, 1, 7).andExpect(status().isOk());
        joinOrGetTokenAndExpectJoined(squadId, 7);
    }

    @Test
    @DisplayName("user8이 거절된 상태에서 join-or-token요청 시 실패")
    void testUser8RejectedCannotRejoin() throws Exception {
        MvcResult result = createSquadAprrovalDefault(1).andExpect(status().isOk()).andReturn();
        Long squadId = extractSquadId(result);
        joinOrGetToken(squadId, 8).andExpect(status().isOk());
        rejectParticipant(squadId, 1, 8).andExpect(status().isOk());
        joinOrGetToken(squadId, 8).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("user9이 탈퇴 후 재참여 시 정상적으로 참여 및 토큰 발급")
    void testUser9LeaveAndRejoin() throws Exception {
        MvcResult result = createSquadAprrovalDefault(1).andExpect(status().isOk()).andReturn();
        Long squadId = extractSquadId(result);
        joinOrGetToken(squadId, 9).andExpect(status().isOk());
        leavePariticipant(squadId, 9).andExpect(status().isOk());
        joinOrGetToken(squadId, 9).andExpect(status().isOk());
    }

    @Test
    @DisplayName("user10이 방장에게 강퇴당한 후 재입장")
    void testUser10KickedSquad() throws Exception {
        MvcResult result = createSquadAprrovalDefault(1).andExpect(status().isOk()).andReturn();
        Long squadId = extractSquadId(result);
        joinOrGetToken(squadId, 10).andExpect(status().isOk());
        kickParticipant(squadId, 1, 10).andExpect(status().isOk());
        joinOrGetToken(squadId, 10).andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("user1가 성별이 여성만있는 방에서 남성으로 수정하면 실패")
    void testUser1SquadOnlyFemale() throws Exception {
        MvcResult result = createSquadAprrovalDefault(1).andExpect(status().isOk()).andReturn();
        Long squadId = extractSquadId(result);
        joinOrGetToken(squadId, 11).andExpect(status().isOk());
        modifySquadMale(1, squadId).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("user12, 여성전용방에 여자만 있음(200ok)")
    void testUser12OnlyFemale() throws Exception {
        MvcResult result = createSquadAprrovalDefault(1).andExpect(status().isOk()).andReturn();
        Long squadId = extractSquadId(result);
        joinOrGetToken(squadId, 12).andExpect(status().isOk());
        modifySquadFeMale(1, squadId).andExpect(status().isOk());
    }

    @Test
    @DisplayName("user13,14,15,16 현재참가자>최대참가자로 수정하면 오류")
    void testUser13ExceededParticipant() throws Exception {
        MvcResult result = createSquadAprrovalDefault(1).andExpect(status().isOk()).andReturn();
        Long sqaudId = extractSquadId(result);
        joinOrGetToken(sqaudId, 13).andExpect(status().isOk());
        joinOrGetToken(sqaudId, 14).andExpect(status().isOk());
        joinOrGetToken(sqaudId, 15).andExpect(status().isOk());
        joinOrGetToken(sqaudId, 16).andExpect(status().isOk());
        modifySquadParticipants(1, sqaudId).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("user1이 user17(58세)가있는데 최소 나이를 60으로 수정시 실패")
    void testUser1SquadModifyAge() throws Exception {
        MvcResult result = createSquadAprrovalDefault(1).andExpect(status().isOk()).andReturn();
        Long squadId = extractSquadId(result);
        joinOrGetToken(squadId, 17).andExpect(status().isOk());
        modifySquadMinAge(1, squadId, 60).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("user1이 user2(72세)가 있는데 최대 나이를 70으로 수정시 실패")
    void testUser1SquadModifyMaxAge() throws Exception {
        MvcResult result = createSquadAprrovalDefault(1).andExpect(status().isOk()).andReturn();
        Long squadId = extractSquadId(result);
        joinOrGetToken(squadId, 2).andExpect(status().isOk());
        modifySquadMaxAge(1, squadId, 70).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("user1이 생성한 스쿼드에 다른 참여자가 있을 경우 참여 실패")
    void testUser1DeleteSquad() throws Exception {
        MvcResult result = createSquadAprrovalDefault(1).andExpect(status().isOk()).andReturn();
        Long squadId = extractSquadId(result);
        joinOrGetToken(squadId, 1).andExpect(status().isOk());
        deleteSquad(squadId, 1, false).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("user1이 생성한 스쿼드에 다른 참여자가 있을 경우에도 강제 삭제")
    void testUser12ForcedDeleteWithOtherParticipants() throws Exception {
        MvcResult result = createSquadAprrovalDefault(1).andExpect(status().isOk()).andReturn();
        Long squadId = extractSquadId(result);
        joinOrGetToken(squadId, 2).andExpect(status().isOk());
        deleteSquad(squadId, 1, true).andExpect(status().isOk());
    }

    // function
    void defaultTest() throws Exception {
        String tokenUser1 = tokenMap.get("user1@test.com");

        String squadJson = String.format("""
                {
                    "title": "여성 독서 모임",
                    "category": "여성만 참여 가능",
                    "description": "주 3회 운동하실 분 구해요.",
                    "regionMain": "서울",
                    "regionSub": "강남구",
                    "date": "%s",
                    "time": "10:30:00",
                    "genderRequirement": "ALL",
                    "minAge": 50,
                    "maxAge": 100,
                    "timeSpecified": "true",
                    "joinType": "DIRECT",
                    "maxParticipants": 10
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

        String tokenUser2 = tokenMap.get("user10@test.com");
        // test
        User user5 = userRepository.findByEmail("user6@test.com").orElseThrow();
        Squad squad = squadRepository.findById(squadId).orElseThrow();
        SquadParticipant participant = squadParticipantRepository.findByUserAndSquad(user5, squad).orElseThrow();

        participant.setStatus(SquadParticipationStatus.REJECTED);
        squadParticipantRepository.save(participant);

        mockMvc.perform(post("/api/squad/" + squadId + "join-or-token")
                .header("Authorization", tokenUser2))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/squad/delete" + squadId)
                .header("Authorization", tokenUser1))
                .andExpect(status().isBadRequest());
    }

    public String getToken(int id) {
        return tokenMap.get("user" + id + "@test.com");
    }

    public ResultActions createSquadDefault(int id, int minAge, int maxAge, Gender gender, JoinType joinType)
            throws Exception {
        return createSquad(id, "여성 독서 모임", "여성만 참여 가능", "주 3회 운동하실 분 구해요.", "서울", "강남구", LocalDate.now().plusDays(3),
                LocalTime.of(10, 0, 0), gender, minAge, maxAge, true, joinType, 5);
    }

    public ResultActions createSquadAprrovalDefault(int id) throws Exception {
        return createSquadDefault(id, 50, 100, Gender.ALL, JoinType.APPROVAL);
    }

    public ResultActions createSquadDirectDefault(int id) throws Exception {
        return createSquadDefault(id, 50, 100, Gender.ALL, JoinType.DIRECT);
    }

    public ResultActions createSquadDirectMin50Max100AllDirect(int id) throws Exception {
        return createSquadDefault(id, 50, 100, Gender.ALL, JoinType.DIRECT);
    }

    public ResultActions modifySquadDefault(int id, long squadId, int minAge, int maxAge, Gender gender,
            JoinType joinType)
            throws Exception {
        return modifySquad(id, squadId, "여성 독서 모임", "여성만 참여 가능", "주 3회 운동하실 분 구해요.", "서울", "강남구",
                LocalDate.now().plusDays(3),
                LocalTime.of(10, 0, 0), gender, minAge, maxAge, true, joinType, 5);
    }

    public ResultActions modifySquadGenderOrAge(int id, long squadId, Gender gender) throws Exception {
        return modifySquadDefault(id, squadId, 50, 100, gender, JoinType.APPROVAL);
    }

    public ResultActions createSquad(int id, String title, String category, String description,
            String regionMain, String regionSub, LocalDate date, LocalTime time, Gender genderRequirement, int minAge,
            int maxAge, boolean timeSpecified, JoinType joinType, int maxParticipants)
            throws Exception {
        String token = getToken(id);
        String request = String.format("""
                    {
                    "title": "%s",
                    "category": "%s",
                    "description": "%s",
                    "regionMain": "%s",
                    "regionSub": "%s",
                    "date": "%s",
                    "time": "%s",
                    "genderRequirement": "%s",
                    "minAge": %d,
                    "maxAge": %d,
                    "timeSpecified": "%s",
                    "joinType": "%s",
                    "maxParticipants": %d
                }
                """, title, category, description, regionMain, regionSub, date, time, genderRequirement, minAge, maxAge,
                timeSpecified, joinType, maxParticipants);

        ResultActions result = mockMvc.perform(post("/api/squad/create")
                .header("Authorization", token)
                .contentType("application/json")
                .content(request));
        return result;
    }

    public ResultActions modifySquad(int id, long squadId, String title, String category,
            String description,
            String regionMain, String regionSub, LocalDate date, LocalTime time, Gender genderRequirement, int minAge,
            int maxAge, boolean timeSpecified, JoinType joinType, int maxParticipants)
            throws Exception {
        String token = getToken(id);
        String request = String.format("""
                {
                "title": "%s",
                "category": "%s",
                "description": "%s",
                "regionMain": "%s",
                "regionSub": "%s",
                "date": "%s",
                "time": "%s",
                "genderRequirement": "%s",
                "minAge": %d,
                "maxAge": %d,
                "timeSpecified": "%s",
                "joinType": "%s",
                "maxParticipants": %d
                }
                """, title, category, description, regionMain, regionSub, date, time,
                genderRequirement, minAge, maxAge,
                timeSpecified, joinType, maxParticipants);

        ResultActions result = mockMvc.perform(put("/api/squad/modify/" + squadId)
                .header("Authorization", token)
                .contentType("application/json")
                .content(request));
        return result;
    }

    public ResultActions modifySquadMale(int userId, long squadId) throws Exception {
        return modifySquad(userId, squadId, "여성 독서 모임", "여성만 참여 가능", "주 3회 운동하실 분 구해요.", "서울", "강남구",
                LocalDate.now().plusDays(3), LocalTime.of(10, 0, 0), Gender.MALE, 50, 100, true, JoinType.APPROVAL, 5);
    }

    public ResultActions modifySquadFeMale(int userId, long squadId) throws Exception {
        return modifySquad(userId, squadId, "여성 독서 모임", "여성만 참여 가능", "주 3회 운동하실 분 구해요.", "서울", "강남구",
                LocalDate.now().plusDays(3), LocalTime.of(10, 0, 0), Gender.MALE, 50, 100, true, JoinType.APPROVAL, 5);
    }

    public ResultActions modifySquadParticipants(int userId, long squadId) throws Exception {
        return modifySquad(userId, squadId, "여성 독서 모임", "여성만 참여 가능", "주 3회 운동하실 분 구해요.", "서울", "강남구",
                LocalDate.now().plusDays(3), LocalTime.of(10, 0, 0), Gender.MALE, 50, 100, true, JoinType.APPROVAL, 4);
    }

    public ResultActions modifySquadMinAge(int userId, long squadId, int minAge) throws Exception {
        return modifySquadDefault(userId, squadId, minAge, 80, Gender.ALL, JoinType.APPROVAL);
    }

    public ResultActions modifySquadMaxAge(int userId, long squadId, int maxAge) throws Exception {
        return modifySquadDefault(userId, squadId, 50, maxAge, Gender.ALL, JoinType.APPROVAL);
    }

    public ResultActions joinOrGetToken(long squadPid, int userId) throws Exception {
        String token = getToken(userId);
        return mockMvc.perform(post("/api/squad/" + squadPid + "/join-or-token")
                .header("Authorization", token));
    }

    public void joinOrGetTokenAndExpectJoined(long squadPid, int userId) throws Exception {
        joinOrGetToken(squadPid, userId)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("joined"))
                .andExpect(jsonPath("$.chatToken").isString());
    }

    public void joinOrGetTokenAndExpectPending(long squadPid, int userId) throws Exception {
        joinOrGetToken(squadPid, userId)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("pending"));
    }

    public void joinOrGetTokenAndExpectPendingNoToken(long squadPid, int userId) throws Exception {
        joinOrGetToken(squadPid, userId)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("pending"));
    }

    public void joinOrGetTokenAndExpectReqeustedNoToken(long squadPid, int userId) throws Exception {
        joinOrGetToken(squadPid, userId)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("requested"));
    }

    public ResultActions deleteSquad(long squadPid, int creatorId, boolean isForced) throws Exception {
        String token = getToken(creatorId);
        return mockMvc.perform(delete("/api/squad/delete/" + squadPid)
                .header("Authorization", token)
                .param("isForcedDelete", String.valueOf(isForced)));
    }

    public Long extractSquadId(MvcResult result) throws Exception {
        String response = result.getResponse().getContentAsString();
        return Long.valueOf(JsonPath.read(response, "$.pid").toString());
    }

    public ResultActions kickParticipant(long squadPid, int hostUser, int targetUser) throws Exception {
        String hostToken = getToken(hostUser);
        long targetPid = userRepository.findByEmail("user" + targetUser + "@test.com").orElseThrow().getPid();

        return mockMvc.perform(post("/api/squad/" + squadPid + "/kick/" + targetPid)
                .header("Authorization", hostToken));
    }

    public ResultActions approveParticipant(long squadPid, int hostUser, int targetUser) throws Exception {
        String hostToken = getToken(hostUser);
        long targetPid = userRepository.findByEmail("user" + targetUser + "@test.com").orElseThrow().getPid();

        return mockMvc.perform(post("/api/squad/" + squadPid + "/approve/" + targetPid)
                .header("Authorization", hostToken));
    }

    public ResultActions rejectParticipant(long squadPid, int hostUser, int targetUser) throws Exception {
        String hostToken = getToken(hostUser);
        long targetPid = userRepository.findByEmail("user" + targetUser + "@test.com").orElseThrow().getPid();

        return mockMvc.perform(post("/api/squad/" + squadPid + "/reject/" + targetPid)
                .header("Authorization", hostToken));
    }

    public ResultActions leavePariticipant(long squadPid, int hostUser) throws Exception {
        String hostToken = getToken(hostUser);

        return mockMvc.perform(post("/api/squad/" + squadPid + "/leave")
                .header("Authorization", hostToken));
    }
}
