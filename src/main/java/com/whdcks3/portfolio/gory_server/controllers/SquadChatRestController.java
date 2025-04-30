package com.whdcks3.portfolio.gory_server.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.whdcks3.portfolio.gory_server.service.ImageUploadService;
import com.whdcks3.portfolio.gory_server.service.S3FileService;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/squad/chat")
public class SquadChatRestController {
    private final ImageUploadService imageUploadService;
    private final S3FileService s3FileService;

    @PostMapping("/upload")
    public ResponseEntity<List<String>> uploadImages(@RequestParam("images") List<MultipartFile> images) {
        return ResponseEntity.ok(images.stream().map(s3FileService::uploadFile).collect(Collectors.toList()));
        // return ResponseEntity.ok(imageUploadService.uploadImages(images));
    }

}
