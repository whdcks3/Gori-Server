package com.whdcks3.portfolio.gory_server.data.requests;

import java.time.LocalDate;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {

    @Size(max = 50)
    @Email
    private String email;

    private String snsType;
    private String snsId;
    private String name;

    @NotNull(message = "통신사는 필수 입력 항목입니다")
    private String carrier;

    @Pattern(regexp = "010-\\d{4}-\\d{4}", message = "휴대폰 번호는 010-XXXX-XXXX 형식이어야 합니다.")
    private String phone;

    private String gender;

    @Past(message = "생년월일은 과거 날짜여야 합니다.(xxxx-xx-xx)")
    private LocalDate birth;

    private String receiveEvent;
    // @NotNull
    // private OAuthProvider oauthProvider;
}
