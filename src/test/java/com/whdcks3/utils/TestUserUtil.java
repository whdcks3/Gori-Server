package com.whdcks3.utils;

import java.time.LocalDate;

import com.whdcks3.portfolio.gory_server.data.requests.SignupRequest;

public class TestUserUtil {
    public static SignupRequest createSignupRequest(int index) {
        SignupRequest req = new SignupRequest();
        req.setEmail("user" + index + "@test.com");
        req.setName("테스트유저" + index);
        req.setPhone("010-1234-12" + String.format("%02d", index));
        req.setCarrier("KT");
        req.setSnsType("kakao");
        req.setSnsId("kakao" + index);
        req.setGender(index % 2 == 0 ? "MALE" : "FEMALE");
        req.setBirth(LocalDate.of(1950 + index, 1, 1));
        req.setReceiveEvent("Y");
        return req;
    }
}
