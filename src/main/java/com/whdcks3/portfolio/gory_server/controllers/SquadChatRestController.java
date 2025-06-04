package com.whdcks3.portfolio.gory_server.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.whdcks3.portfolio.gory_server.data.models.squad.SquadChat;
import com.whdcks3.portfolio.gory_server.data.models.user.User;
import com.whdcks3.portfolio.gory_server.service.ImageUploadService;
import com.whdcks3.portfolio.gory_server.service.S3FileService;
import com.whdcks3.portfolio.gory_server.service.SquadChatService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/squad/{squadId}/chat")
public class SquadChatRestController {

    private final SquadChatService chatService;

    // 메시지 불러오기
    @GetMapping("/messages")
    public ResponseEntity<List<SquadChat>> getMessages(@AuthenticationPrincipal User user, @PathVariable Long squadId,
            @RequestParam(value = "before", required = false) Long beforeId) {
        List<SquadChat> result = (beforeId == null)
                ? chatService.loadRecentMessages(squadId)
                : chatService.loadPreviousMessages(squadId, beforeId);
        return ResponseEntity.ok(result);
    }

    // 텍스트 메시지 전송
    @PostMapping("/text")
    public ResponseEntity<SquadChat> sendTextMessage(@AuthenticationPrincipal User user, @PathVariable Long squadId,
            @RequestBody Map<String, String> body) {
        String message = body.get("message");
        SquadChat chat = chatService.sendTextMessage(squadId, user.getPid(), message);
        return ResponseEntity.ok(chat);
    }

    // 이미지 메시지 전송
    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SquadChat> sendImageMessage(@AuthenticationPrincipal User user, @PathVariable Long squadId,
            @RequestPart List<MultipartFile> images) {
        SquadChat chat = chatService.sendImageMessage(squadId, user.getPid(), images);
        return ResponseEntity.ok(chat);
    }
}
