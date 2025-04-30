package com.whdcks3.portfolio.gory_server.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImageUploadService {
    private final FileService fileService;

    public List<String> uploadImages(List<MultipartFile> images) {
        return images.stream()
                .map(fileService::upload)
                .collect(Collectors.toList());
    }
}
