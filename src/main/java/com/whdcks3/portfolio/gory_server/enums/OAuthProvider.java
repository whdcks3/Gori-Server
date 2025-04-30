package com.whdcks3.portfolio.gory_server.enums;

public enum OAuthProvider {
    KAKAO("카카오"),
    GOOGLE("구글"),
    FACEBOOK("페이스북"),
    NAVER("네이버");

    private final String displayName;

    OAuthProvider(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

}
