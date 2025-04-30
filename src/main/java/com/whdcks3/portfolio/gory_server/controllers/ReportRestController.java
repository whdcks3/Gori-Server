package com.whdcks3.portfolio.gory_server.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.whdcks3.portfolio.gory_server.data.requests.ReportRequest;
import com.whdcks3.portfolio.gory_server.service.ReportService;

@RestController
@RequestMapping("/api/report")
public class ReportRestController {

    @Autowired
    ReportService reportService;

    @PostMapping("/reportUser")
    @PreAuthorize("hasRole('User')")
    public ResponseEntity<?> reportUser(@RequestParam Long reporterId, @RequestParam Long reportedId,
            @ModelAttribute ReportRequest req) {
        reportService.reportUser(reporterId, reportedId, req);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reportSquad")
    @PreAuthorize("hasRole('User')")
    public ResponseEntity<?> reportSquad(@RequestParam Long reporterId, @RequestParam Long squadId,
            @ModelAttribute ReportRequest req) {
        reportService.reportSquad(reporterId, squadId, req);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reportFeed")
    @PreAuthorize("hasRole('User')")
    public ResponseEntity<?> reportFeed(@RequestParam Long reporterId, @RequestParam Long feedId,
            @ModelAttribute ReportRequest req) {
        reportService.reportFeed(reporterId, feedId, req);
        return ResponseEntity.ok().build();
    }
}
