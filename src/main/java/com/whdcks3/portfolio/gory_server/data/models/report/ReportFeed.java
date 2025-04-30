package com.whdcks3.portfolio.gory_server.data.models.report;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.annotations.DynamicInsert;

import com.whdcks3.portfolio.gory_server.common.Report;
import com.whdcks3.portfolio.gory_server.data.models.feed.Feed;
import com.whdcks3.portfolio.gory_server.data.models.user.User;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "report_feed")
@Getter
@Setter
@NoArgsConstructor
@DynamicInsert
public class ReportFeed extends Report {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_pid", nullable = false)
    private User reporterId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_pid", nullable = false)
    private Feed feedId;

    private String category;

    @Size(max = 500)
    private String content;

    public ReportFeed(User reportedId, Feed feedId, String category, String content) {
        this.reporterId = reportedId;
        this.feedId = feedId;
        this.category = category;
        this.content = content;
    }
}
