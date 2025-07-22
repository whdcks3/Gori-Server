package com.whdcks3.portfolio.gory_server.data.requests;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedRequest {

    @NotBlank
    private String content;

    @NotBlank
    private String category;

    @Size(max = 10, message = "이미지는 최대 10개까지 업로드 가능합니다.")
    private List<MultipartFile> addedImages = new ArrayList<>();// []

    private List<String> deletedImages = new ArrayList<>();
}
