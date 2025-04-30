package com.whdcks3.portfolio.gory_server.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    String upload(MultipartFile file);

    void delete(String fileName);
}
