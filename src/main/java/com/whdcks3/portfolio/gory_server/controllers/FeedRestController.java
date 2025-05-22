package com.whdcks3.portfolio.gory_server.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.whdcks3.portfolio.gory_server.data.models.user.User;
import com.whdcks3.portfolio.gory_server.data.requests.FeedCommentRequest;
import com.whdcks3.portfolio.gory_server.data.requests.FeedRequest;
import com.whdcks3.portfolio.gory_server.service.BlockService;
import com.whdcks3.portfolio.gory_server.service.FeedService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import org.springframework.web.bind.annotation.RequestMethod;

@RestController
@RequestMapping("/api/feed")
@CrossOrigin(origins = "*")
public class FeedRestController {

    @Autowired
    FeedService feedService;

    @Autowired
    BlockService blockService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> createFeed(@AuthenticationPrincipal User user, @ModelAttribute FeedRequest req) {
        feedService.createFeed(req, user);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getFeed(@AuthenticationPrincipal User user, @PathVariable Long id) {
        return ResponseEntity.ok().body(feedService.getFeed(user, id));
    }

    @GetMapping("/comment/{commentId}/replies")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getReplies(
            @AuthenticationPrincipal User user,
            @PathVariable Long commentId,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.ASC, size = 10) Pageable pageable) {

        return ResponseEntity.ok(feedService.getReplies(user, commentId, pageable));
    }

    @PutMapping("/modify/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> modifyFeed(@AuthenticationPrincipal User user, @PathVariable Long id,
            @ModelAttribute FeedRequest req) {
        feedService.updateFeed(req, user, id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> deleteFeed(@AuthenticationPrincipal User user, @PathVariable("id") Long fid) {
        feedService.deleteFeed(user, fid);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/like/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> likeFeed(@AuthenticationPrincipal User user, @PathVariable Long id) {
        return ResponseEntity.ok(feedService.processFeedLike(user.getPid(), id));
    }

    @Operation(summary = "나의 피드", security = @SecurityRequirement(name = "bearerAuth"))
    @RequestMapping(value = "/mine", method = RequestMethod.GET)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> mine(@AuthenticationPrincipal User user,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC, size = 10) Pageable pageable) {
        return ResponseEntity.ok(feedService.myFeeds(user.getPid(), pageable));
    }

    @RequestMapping(value = "/home", method = RequestMethod.GET)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> home(@AuthenticationPrincipal User user,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC, size = 10) Pageable pageable,
            @RequestParam(defaultValue = "전체") String category) {
        System.out.println("user: " + (user != null));
        return ResponseEntity.ok().body(feedService.homeFeed(user, pageable, category));
    }

    @PostMapping("/others/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> other(@AuthenticationPrincipal User user, @PathVariable Long id,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC, size = 10) Pageable pageable) {

        return ResponseEntity.ok(feedService.othersFeed(user.getPid(), id, pageable));
    }

    @PostMapping("/createcomment")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> createComment(@AuthenticationPrincipal User user, @ModelAttribute FeedCommentRequest req) {
        feedService.writeComment(user.getPid(), req);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/deletecomment/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> deleteComment(@PathVariable Long id,
            @AuthenticationPrincipal Long userId) {
        feedService.deleteComment(id, userId);
        return ResponseEntity.ok().build();
    }

}
