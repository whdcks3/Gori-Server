package com.whdcks3.portfolio.gory_server.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.whdcks3.portfolio.gory_server.data.models.user.User;
import com.whdcks3.portfolio.gory_server.service.BlockService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/block")
public class BlockRestController {

    private final BlockService blockService;

    // 사용자 차단
    @PostMapping("/{otherid}")
    public ResponseEntity<?> blockUser(@AuthenticationPrincipal User user, @PathVariable User blockId) {
        blockService.blockUser(user, blockId);
        return ResponseEntity.ok().build();
        // try {
        // blockService.blockUser(user, blockId);
        // } catch (ValidationException e) {
        // return ResponseEntity.ok().body(new CommonResponse(e.getStatusCode(),
        // e.getMessage()));
        // }
        // return ResponseEntity.ok().body(new CommonResponse(100, "성공"));
    }

    // 사용자 차단 해제
    @DeleteMapping("/{otherid}")
    public ResponseEntity<?> unBlockUser(@AuthenticationPrincipal User user, @PathVariable User otherId) {
        blockService.unblockUser(user, otherId);
        return ResponseEntity.ok().build();
        // try {
        // blockService.unblockUser(user, otherId);
        // } catch (ValidationException e) {
        // return ResponseEntity.ok().body(new CommonResponse(e.getStatusCode(),
        // e.getMessage()));
        // }
        // return ResponseEntity.ok().body(new CommonResponse(100, "성공"));
    }

    // 차단한 사용자 조회
    @GetMapping
    public ResponseEntity<?> getBlockedUsers(@AuthenticationPrincipal User user, @PathVariable User userId) {
        return ResponseEntity.ok(blockService.getBlockUsers(userId));
        // try {
        // blockService.getBlockUsers(userId);
        // } catch (ValidationException e) {
        // return ResponseEntity.ok().body(new CommonResponse(e.getStatusCode(),
        // e.getMessage()));
        // }
        // return ResponseEntity.ok().body(new CommonResponse(100, "성공"));
    }
}
