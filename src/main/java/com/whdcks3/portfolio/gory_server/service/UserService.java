package com.whdcks3.portfolio.gory_server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.api.pathtemplate.ValidationException;
import com.whdcks3.portfolio.gory_server.common.EmailUtils;
import com.whdcks3.portfolio.gory_server.data.models.user.EmailVerification;
import com.whdcks3.portfolio.gory_server.data.models.user.User;
import com.whdcks3.portfolio.gory_server.data.requests.UserAlarmRequest;
import com.whdcks3.portfolio.gory_server.data.requests.UserModifyRequest;
import com.whdcks3.portfolio.gory_server.exception.NicknameDuplicatedException;
import com.whdcks3.portfolio.gory_server.repositories.UserRepository;

import java.util.Random;
import java.util.UUID;

import javax.transaction.Transactional;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    EmailUtils emailUtils;

    @Autowired
    JavaMailSender mailSender;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    FileService fileService;

    @Autowired
    FeedService feedService;

    @Autowired
    SquadService squadService;

    @Autowired
    ChatroomService chatroomService;

    public String generateNickname() {
        String[] name1 = { "넉살좋은", "발랄한", "산뜻한", "아리따운", "적극적인", "착한", "훌륭한", "고요한", "낙천적인", "부드러운", "수려한", "어린",
                "정의로운", "청초한", "활동적인", "고운", "낭만적", "빼어난", "순한", "어여쁜", "조용한", "창의적인", "화사한", "근면한", "다정한", "밝은",
                "싱그러운", "엉뚱한", "재미있는", "침착한", "화끈한", "귀여운", "당당한", "배짱 있는", "선한", "예쁜", "정직한", "책임감 있는", "합리적인", "관대한",
                "든든한", "부지런한", "사교적인", "용감한", "존귀한", "차분한", "헌신적인", "깔끔한", "또렷한", "바른", "섬세한", "용맹한", "지혜로운", "친숙한",
                "활달한", "꾸준한", "다양한", "성실한", "우아한", "자애로운", "친절한", "호감가는", "긍정적인", "단호한", "순수한", "위대한", "잘 웃는", "쾌활한",
                "사랑스런", "겸손한", "대담한", "순진한", "용기있는", "자유로운", "카리스마 있는", "검소한", "담대한", "소신있는", "유능한", "지적인", "튼튼한",
                "공손한", "로맨틱한", "사려깊은", "유쾌한", "정숙한", "털털한", "기운찬", "믿음직한", "상냥한", "아름다운", "진취적인", "편안한", "놀라운", "명랑한",
                "생기있는", "여유로운", "평화로운", "넉넉한", "매력적인", "솔직한", "포근한", "느긋한", "맑은", "신중한", "멋진", "싹싹한", "반듯한" };
        String[] name2 = { "가람", "가론", "가온", "간새", "고운", "글지", "까미", "꽃담", "나봄", "나예", "난새", "노을", "늘봄", "나린", "난이",
                "너비", "너울", "나래", "느루", "노량", "다솜", "다슬", "다은", "다림", "든해", "닻별", "다흰", "다윈", "단미", "단춤", "둔치", "두리",
                "드레", "돌티", "라온", "라라", "라미", "루리", "마루", "모아", "미르", "모들", "무새", "몰개", "물꽃", "마녘", "별찌", "별하", "바오",
                "바림", "보미", "뵤뵤", "사품", "소리", "새나", "새론", "소미", "손갓", "슬아", "슬찬", "새라", "샛별", "슈룹", "손탁", "아띠", "윤슬",
                "아라", "아란", "아람", "아미", "아사", "아토", "이든", "운김", "으뜸", "은솔", "이플", "잎새", "외꽃", "알땀", "잔디", "진이", "자올",
                "주리", "지니", "자귀", "즈문", "차롱", "찬슬", "찬솔", "찬돛", "초롱", "초아", "큰솔", "토리", "티나", "타니", "핀아", "파니", "푸실",
                "팍내", "하랑", "하람", "해나", "햇덧", "하나", "한결", "한빛", "하제", "한별", "해윰", "한울", "힐조", "하름", "허양", "해참", "혼불" };

        Random random = new Random();
        String strName1 = name1[random.nextInt(name1.length)];
        String strName2 = name2[random.nextInt(name2.length)];
        System.out.println("생성된 닉네임 : " + strName1 + " " + strName2);
        return strName1 + " " + strName2;
    }

    public boolean hasUsername(User user) {
        return !user.getNickname().equals("");
    }

    public void updateNickname(User user, String nickname) {
        if (duplicationNickname(user.getPid(), nickname)) {
            throw new NicknameDuplicatedException();
        }
        user.setNickname(nickname);
        userRepository.save(user);
    }

    public boolean duplicationNickname(Long uid, String nickname) {
        return userRepository.existsByNicknameAndPidNot(nickname, uid);
    }

    public String limitNickname(String nickname) {
        String regex = "^[a-zA-Z0-9가-힣]{2,8}$";
        if (!nickname.matches(regex)) {
            throw new ValidationException("올바르지 않은 형식의 이름입니다");
        } else {
            return "올바른 형식의 이름입니다";
        }

    }

    public void sendEmailLink(String email) {
        EmailVerification verificationToken = new EmailVerification();
        String token = UUID.randomUUID().toString();
        verificationToken.setToken(token);
        String EmailLink = "https://gory-server.seojongchan-dev.com/api/user/find_account?token="
                + verificationToken.getToken();
        emailUtils.sendEmail(email, "이메일 인증", "메일을 클릭해주세요: " + EmailLink);
    }

    public void modifyPassword(String email, String snsType, String snsId, String password) {
        User user = userRepository.findByEmail(email).orElseThrow();
        String encoded = passwordEncoder.encode(password);
        user.setSnsId(snsId);
        user.setSnsType(snsType);
        user.setPassword(encoded);
        userRepository.save(user);
    }

    public void updateFcm(User user, String fcmToken) {
        user.setFcmToken(fcmToken);
        userRepository.save(user);
    }

    public void updateContent(User user, UserModifyRequest req) {
        user.setIntroduction(req.getIntroduction());
        userRepository.save(user);
    }

    public void updateNickname(User user, UserModifyRequest req) {
        user.setNickname(req.getNickname());
        userRepository.save(user);
    }

    public void updateProfile(User user, UserModifyRequest req) {
        deleteImage(user);
        saveProfileImage(user, req.getProfile());
    }

    private void saveProfileImage(User user, MultipartFile image) {
        String filename = fileService.upload(image);
        user.setImageUrl(filename);
    }

    private void deleteImage(User user) {
        if (user.getImageUrl().endsWith("avatar_placeholder.png")) {
            return;
        }
        fileService.delete(user.getImageUrl());
    }

    @Transactional
    public void withdrawal(User user) {
        feedService.deleteByUser(user);

        squadService.deleteByUser(user);

        chatroomService.deleteByUser(user);

        userRepository.delete(user);
    }

    public void alarmSet(User user, UserAlarmRequest req) {
        user.updateAlarmSetting(req.getAlarmType(), req.getAlarmEnabled());
    }

}
