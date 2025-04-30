package com.whdcks3.portfolio.gory_server.data.models.report;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.annotations.DynamicInsert;
import com.whdcks3.portfolio.gory_server.common.Report;
import com.whdcks3.portfolio.gory_server.data.models.user.User;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "report_user")
@Getter
@Setter
@NoArgsConstructor
@DynamicInsert
public class ReportUser extends Report {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reproter_pid", nullable = false)
    private User reporterId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_pid", nullable = false)
    private User reportedId;

    String category;

    @Size(max = 500)
    private String content;

    public ReportUser(User reporterId, User reportedId, String category, String content) {
        this.reporterId = reporterId;
        this.reportedId = reportedId;
        this.category = category;
        this.content = content;
    }
}
