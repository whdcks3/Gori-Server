package com.whdcks3.portfolio.gory_server.data.models.feed;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.whdcks3.portfolio.gory_server.common.BaseEntity;
import com.whdcks3.portfolio.gory_server.data.models.user.User;
import com.whdcks3.portfolio.gory_server.data.requests.FeedRequest;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "feed")
@Getter
@Setter
@NoArgsConstructor
@DynamicInsert
public class Feed extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_pid", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    private String category;

    @Column(columnDefinition = "TEXT", nullable = false)
    @Size(max = 5000)
    private String content;

    @OneToMany(mappedBy = "feed", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FeedImage> images;

    @JsonManagedReference
    @OneToMany(mappedBy = "feed", targetEntity = FeedComment.class, cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<FeedComment> comments = new ArrayList<>();

    @Column(nullable = false)
    private int likeCount = 0;

    @Column(nullable = false)
    private int viewCount = 0;

    @Column(nullable = false)
    private int commentCount = 0;

    @Column(nullable = false)
    private int reportCount = 0;

    @lombok.Builder
    public Feed(User user, FeedRequest req, List<FeedImage> images) {
        this.user = user;
        this.category = req.getCategory();
        this.content = req.getContent();
        this.likeCount = 0;
        this.viewCount = 0;
        this.reportCount = 0;
        this.images = new ArrayList<>();
    }

    public void update(FeedRequest req) {
        this.content = req.getContent();
        this.category = req.getCategory();
    }

    public void increaseLikeCount() {
        this.likeCount += 1;
    }

    public void decreaseLikeCount() {
        this.likeCount -= 1;
    }

    public void increaseViewCount() {
        this.viewCount += 1;
    }

    public void decreaseCommentCount() {
        this.commentCount -= 1;
    }

    public void increaseCommentCount() {
        this.commentCount += 1;
    }

    public void increaseReportCount() {
        this.reportCount++;
    }
}
