package com.whdcks3.portfolio.gory_server.enums;

public enum Gender {
    ALL("누구나"), MALE("남성만"), FEMALE("여성만");

    private String gender;

    private Gender(String gender) {
        this.gender = gender;
    }

    public String getName() {
        return gender;
    }
}
