package com.whdcks3.portfolio.gory_server.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
                        "carrier": "SKT",
                        "phone": "010-1234-%04d",
                        "gender": "%s",
                        "birth": "%s",
                        "receiveEvent": "Y"
                        }
                    """, email, snsType, snsId, name, i, gender, birth);
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
    void dummyTest() {
        System.out.println("여기까지 들어옴");
    }

    @Test
    @DisplayName("25명의 유저가 로그인 성공 후 토큰 발급을 받음, 테스트주체인 user1 토큰 확인")
    void testTokenMapBuilt() {
        assertThat(tokenMap).hasSize(25);
        assertThat(tokenMap.get("user1@test.com")).startsWith("Bearer ");
        assertThat(tokenMap.get("user1@test.com")).isNotBlank();
    }

    @Test
    @DisplayName("user1이 정상적인 피드(텍스트O + 이미지O) 생성")
    void testUser1CreateFeed() throws Exception {
        createFeed(1).andExpect(status().isOk()).andReturn();
    }

    @Test
    @DisplayName("user1이 피드 수정")
    void testUser1ModifyFeed() throws Exception {
        MvcResult result = createFeed(1).andExpect(status().isOk()).andReturn();
        Long feedId = extractFeedId(result);
        modifyFeed(1, feedId).andExpect(status().isOk());
    }

    @Test
    @DisplayName("user1이 피드 삭제")
    void testUser1DeleteFeed() throws Exception {
        MvcResult result = createFeed(1).andExpect(status().isOk()).andReturn();
        Long feedId = extractFeedId(result);
        deleteFeed(1, feedId).andExpect(status().isOk());
    }

    @Test
    @DisplayName("user2이 정상적인 피드(텍스트O + 이미지X) 생성")
    void testUser2CreateFeed() throws Exception {
        createFeedNoImage(2).andExpect(status().isOk());
    }

    @Test
    @DisplayName("user3가 비정상적인 피드(categoryX) 생성")
    void testUser3FeedNoCategory() throws Exception {
        createFeedNoCategory(3).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("user4가 비정상적인 피드(contentX) 생성")
    void testUser4FeedNoContent() throws Exception {
        createFeedNoContent(4).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("user5가 이미지 최대 업로드수 초과")
    void testUser5FeedImageUploadExceeded() throws Exception {
        createFeedUploadLimit(5).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("user6가 잘못된 이미지 형식 업로드")
    void testUser6NotFoundImage() throws Exception {
        createFeedInvalidImage(6).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("user1이 자신을 포함한 user7,8,9의 전체 피드 목록을 조회하기")
    void testUser7GetFeedList() throws Exception {
        createFeed(1).andExpect(status().isOk());
        createFeed(7).andExpect(status().isOk());
        createFeed(8).andExpect(status().isOk());
        createFeed(9).andExpect(status().isOk());
        getFeedList(1).andExpect(status().isOk());
    }

    @Test
    @DisplayName("user10의 피드를 user11이 삭제 시도")
    void testUser10NotMineFeed() throws Exception {
        MvcResult result = createFeed(10).andExpect(status().isOk()).andReturn();
        Long feedId = extractFeedId(result);
        DeleteFeedNotMine(11, feedId).andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("user1의 자신만의 피드 조회")
    void testuser1FeedIsMine() throws Exception {
        getMyFeed(1).andExpect(status().isOk());
    }

    public ResultActions DeleteFeedNotMine(int userId, Long feedId) throws Exception {
        String token = getToken(11);
        ResultActions result = mockMvc.perform(delete("/api/feed/delete/{id}", feedId)
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON));
        return result;
    }

    public ResultActions getMyFeed(int id) throws Exception {
        String token = getToken(1);
        createFeedDefault(id, "내 피드1", "운동", null, null).andExpect(status().isOk());
        createFeedDefault(id, "내 피드2", "전체", null, null);
        createFeedDefault(id, "내 피드3", "취미", null, null);

        ResultActions result = mockMvc.perform(get("/api/feed/mine")
                .header("Authorization", token)
                .param("page", "0")
                .param("size", "10"));
        return result;
    }

    public ResultActions getFeedList(int id) throws Exception {
        String token = getToken(id);
        ResultActions result = mockMvc.perform(get("/api/feed/home")
                .param("category", "전체")
                .param("page", "0")
                .param("size", "10")
                .header("Authorization", token));
        return result;
    }

    public ResultActions createFeedInvalidImage(int id) throws Exception {
        String token = getToken(id);

        MockMultipartFile invalidFile = new MockMultipartFile(
                "addedImages",
                "test.txt",
                "image/jpeg",
                "not image".getBytes());
        MockMultipartHttpServletRequestBuilder builder = multipart("/api/feed/create");
        builder.file(invalidFile);
        builder.param("content", "잘못된 이미지 형식 테스트");
        builder.param("category", "전체");
        builder.header("Authorization", token);

        ResultActions result = mockMvc.perform(builder);
        return result;
    }

    public ResultActions createFeedUploadLimit(int id) throws Exception {
        String token = getToken(id);

        MockMultipartFile[] files = new MockMultipartFile[11];
        for (int i = 0; i < 11; i++) {
            files[i] = new MockMultipartFile(
                    "addedImages",
                    "test" + i + ".jpg",
                    "image/jpeg",
                    "imagetest".getBytes());
        }

        MockMultipartHttpServletRequestBuilder builder = multipart("/api/feed/create");

        builder.param("content", "이미지10장 초과시 오류");
        builder.param("category", "전체");
        builder.header("Authorization", token);

        for (MockMultipartFile file : files) {
            builder.file(file);
        }
        ResultActions result = mockMvc.perform(builder);
        return result;
    }

    public ResultActions createFeedNoContent(int id) throws Exception {
        String token = getToken(id);
        ResultActions result = mockMvc.perform(multipart("/api/feed/create")
                .param("category", "전체")
                .header("Authorization", token));
        return result;
    }

    public ResultActions createFeedNoImage(int id) throws Exception {
        String token = getToken(id);
        ResultActions result = mockMvc.perform(multipart("/api/feed/create")
                .param("content", "테스트 본문")
                .param("category", "전체")
                .header("Authorization", token));
        return result;
    }

    public ResultActions createFeedNoCategory(int id) throws Exception {
        String token = getToken(id);
        ResultActions result = mockMvc.perform(multipart("/api/feed/create")
                .param("content", "테스트 본문")
                .header("Authorization", token));
        return result;
    }

    public ResultActions createFeed(int id) throws Exception {
        String token = getToken(id);
        MockMultipartFile image1 = new MockMultipartFile("addedImages", "image1.jpg", "image/jpeg",
                "fake-image-content-1".getBytes());
        MockMultipartFile image2 = new MockMultipartFile("addedImages", "image2.jpg", "image/jpeg",
                "fake-image-content-2".getBytes());
        // MockMultipartFile dummy = new MockMultipartFile("dummy", "", "text/plain",
        // new byte[0]);

        ResultActions result = mockMvc.perform(multipart("/api/feed/create")
                .file(image1)
                .file(image2)
                .param("content", "테스트 본문")
                .param("category", "전체")
                .header("Authorization", token))
                .andExpect(status().isOk());
        return result;
    }

    public ResultActions createFeedDefault(int userId, String content, String category, MultipartFile image,
            String deletedImage) throws Exception {
        MockMultipartFile contentFile = new MockMultipartFile("addedImages", "test.jpg", "image/jpeg",
                "image dummy".getBytes());
        return mockMvc.perform(multipart("/api/feed/create")
                .file(contentFile)
                .param("content", content)
                .param("category", category)
                .header("Authorization", getToken(userId)));
    }

    public ResultActions modifyFeed(int userId, Long feedId) throws Exception {
        String token = getToken(userId);
        MockMultipartFile image1 = new MockMultipartFile("addedImages", "newImage1.jpg", "image/jpeg",
                "new-image-content-2".getBytes());
        MockMultipartFile image2 = new MockMultipartFile("addedImages", "newImage2.jpg", "image/jpeg",
                "new-image-content-2".getBytes());

        ResultActions result = mockMvc.perform(multipart("/api/feed/modify/" + feedId)
                .file(image1)
                .file(image2)
                .param("content", "수정된 본문")
                .param("category", "전체")
                .param("deletedImages", "oldImage1.jpg")
                .param("deletedImages", "oldImages2.jpg")
                .with(request -> {
                    request.setMethod("PUT");
                    return request;
                })
                .header("Authorization", token));
        return result;
    }

    public ResultActions deleteFeed(int userId, Long feedId) throws Exception {
        String token = getToken(userId);
        return mockMvc.perform(delete("/api/feed/delete/" + feedId)
                .header("Authorization", token));
    }

    public String getToken(int id) {
        return tokenMap.get("user" + id + "@test.com");
    }

    public Long extractFeedId(MvcResult result) throws Exception {
        String response = result.getResponse().getContentAsString();
        return Long.valueOf(JsonPath.read(response, "$.id").toString());
    }
}
