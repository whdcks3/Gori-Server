package com.whdcks3.portfolio.gory_server.data.requests;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SquadChatRequest {
    private Long pid;
    private String message;

    private List<MultipartFile> images = new ArrayList<>();
}
