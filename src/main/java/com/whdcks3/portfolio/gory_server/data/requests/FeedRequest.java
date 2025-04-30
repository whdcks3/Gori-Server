package com.whdcks3.portfolio.gory_server.data.requests;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotBlank;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedRequest {
    private String content;

    @NotBlank
    private String category;

    private List<MultipartFile> addedImages = new ArrayList<>();// []

    private List<String> deletedImages = new ArrayList<>();
}
