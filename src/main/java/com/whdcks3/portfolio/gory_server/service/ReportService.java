package com.whdcks3.portfolio.gory_server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.whdcks3.portfolio.gory_server.data.models.feed.Feed;
import com.whdcks3.portfolio.gory_server.data.models.report.ReportFeed;
import com.whdcks3.portfolio.gory_server.data.models.report.ReportSquad;
import com.whdcks3.portfolio.gory_server.data.models.report.ReportUser;
import com.whdcks3.portfolio.gory_server.data.models.squad.Squad;
import com.whdcks3.portfolio.gory_server.data.models.user.User;
import com.whdcks3.portfolio.gory_server.data.requests.ReportRequest;
import com.whdcks3.portfolio.gory_server.repositories.FeedRepository;
import com.whdcks3.portfolio.gory_server.repositories.ReportFeedRepository;
import com.whdcks3.portfolio.gory_server.repositories.ReportSquadRepository;
import com.whdcks3.portfolio.gory_server.repositories.ReportUserRepository;
import com.whdcks3.portfolio.gory_server.repositories.SquadRepository;
import com.whdcks3.portfolio.gory_server.repositories.UserRepository;

@Service
public class ReportService {

    @Autowired
    ReportSquadRepository reportSquadRepository;

    @Autowired
    ReportUserRepository reportUserRepository;

    @Autowired
    ReportFeedRepository reportFeedRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    SquadRepository squadRepository;

    @Autowired
    FeedRepository feedRepository;

    // 유저 신고
    public void reportUser(Long reporterId, Long reportedId, ReportRequest req) {
        User reporter = userRepository.findById(reporterId).orElseThrow();
        User reported = userRepository.findById(reportedId).orElseThrow();
        ReportUser report = new ReportUser(reporter, reported, req.getCategory(), req.getCategory());
        reported.increaseReportCount();
        reportUserRepository.save(report);
    }

    // 모임 신고
    public void reportSquad(Long reporterId, Long squadId, ReportRequest req) {
        User reporter = userRepository.findById(reporterId).orElseThrow();
        Squad squad = squadRepository.findById(squadId).orElseThrow();
        ReportSquad report = new ReportSquad(squad, reporter, req.getCategory(), req.getContent());
        squad.increaseReportCount();
        reportSquadRepository.save(report);
    }

    // 피드 신고
    public void reportFeed(Long reporterId, Long feedId, ReportRequest req) {
        User reporter = userRepository.findById(reporterId).orElseThrow();
        Feed feed = feedRepository.findById(feedId).orElseThrow();
        ReportFeed report = new ReportFeed(reporter, feed, req.getCategory(), req.getContent());
        feed.increaseReportCount();
        reportFeedRepository.save(report);
    }

}
