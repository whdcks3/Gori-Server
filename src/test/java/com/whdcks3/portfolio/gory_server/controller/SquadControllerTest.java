package com.whdcks3.portfolio.gory_server.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class SquadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @WithMockUser(username = "testuser", roles = "USER")
    @Test
    void testCreateSquad() throws Exception {
        String requestBody = """
                {
                    "title": "오류만안나면돼",
                    "category": "취미",
                    "regionMain": "서울",
                    "regionSub": "강남구",
                    "description": "테스트입니다.",
                    "minAge": 20,
                    "maxAge": 30,
                    "date": "2025-06-25",
                    "time": "18:00:00",
                    "timeSpecified": true,
                    "joinType": "DIRECT",
                    "genderRequirement": "ALL",
                    "maxParticipants": 10
                }
                """;

        mockMvc.perform(post("/api/squad/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk());
    }

    @WithMockUser(roles = "USER")
    @Test
    void testModifySquad() throws Exception {
        mockMvc.perform(put("/api/squad/modify/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "title": "오류만안나면돼",
                            "category": "운동",
                            "regionMain": "서울",
                            "regionSub": "강남구",
                            "description": "수정 내용입니다.",
                            "minAge": 25,
                            "maxAge": 35,
                            "date": "2025-07-01",
                            "time": "19:00:00",
                            "timeSpecified": true,
                            "joinType": "APPROVAL",
                            "genderRequirement": "ALL",
                            "maxParticipants": 8
                        }
                        """))
                .andExpect(status().isOk());
    }

    @WithMockUser(roles = "USER")
    @Test
    void testDeleteSquad() throws Exception {
        mockMvc.perform(delete("/api/squad/delete/1")
                .param("isForcedDelete", "false"))
                .andExpect(status().isOk());
    }

    @WithMockUser(roles = "USER")
    @Test
    void testMySquads() throws Exception {
        mockMvc.perform(get("/api/squad/mine"))
                .andExpect(status().isOk());
    }

    @WithMockUser(roles = "USER")
    @Test
    void testHomeSquads() throws Exception {
        mockMvc.perform(get("/api/squad/home")
                .param("regionMain", "서울"))
                .andExpect(status().isOk());
    }

    @WithMockUser(roles = "USER")
    @Test
    void testDetailSquad() throws Exception {
        mockMvc.perform(get("/api/squad/1"))
                .andExpect(status().isOk());
    }

    @WithMockUser(roles = "USER")
    @Test
    void testCloseSquad() throws Exception {
        mockMvc.perform(put("/api/squad/1/close"))
                .andExpect(status().isOk());
    }

    @WithMockUser(roles = "USER")
    @Test
    void testApproveParticipant() throws Exception {
        mockMvc.perform(post("/api/squad/1/approve/2"))
                .andExpect(status().isOk());
    }

    @WithMockUser(roles = "USER")
    @Test
    void testRejectParticipant() throws Exception {
        mockMvc.perform(post("/api/squad/1/reject/2"))
                .andExpect(status().isOk());
    }

    @WithMockUser(roles = "USER")
    @Test
    void testKickOffParticipant() throws Exception {
        mockMvc.perform(post("/api/squad/1/kick/2"))
                .andExpect(status().isOk());
    }

    @WithMockUser(roles = "USER")
    @Test
    void testLeaveSquad() throws Exception {
        mockMvc.perform(post("/api/squad/1/leave"))
                .andExpect(status().isOk());
    }

    @WithMockUser(roles = "USER")
    @Test
    void testJoinOrGetToken() throws Exception {
        mockMvc.perform(post("/api/squad/1/join-or-token"))
                .andExpect(status().isOk());
    }

    @WithMockUser(roles = "USER")
    @Test
    void testGetParticipants() throws Exception {
        mockMvc.perform(get("/api/squad/1/participants"))
                .andExpect(status().isOk());
    }
}
