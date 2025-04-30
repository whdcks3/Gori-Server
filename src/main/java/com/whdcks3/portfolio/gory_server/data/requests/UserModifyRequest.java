package com.whdcks3.portfolio.gory_server.data.requests;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserModifyRequest {

    private String introduction, nickname;

    MultipartFile profile;
}
