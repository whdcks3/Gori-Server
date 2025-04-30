package com.whdcks3.portfolio.gory_server.data.models.feed;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.google.auto.value.AutoValue.Builder;
import com.whdcks3.portfolio.gory_server.common.BaseEntity;
import com.whdcks3.portfolio.gory_server.data.models.user.User;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "feed_comment")
@Getter
@Setter
@Builder
@NoArgsConstructor
@DynamicInsert
public class FeedComment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_pid", nullable = false)
    private Feed feed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_pid", nullable = false)
    private User user;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_pid", nullable = true)
    private FeedComment parentComment;

    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<FeedComment> childComments = new ArrayList<>();

    // 댓글의 댓글의 댓글은 없고
    // 원댓의 댓글까지만 존재..

    @lombok.Builder
    public FeedComment(User user, Feed feed, String content, FeedComment parentComment) {
        this.user = user;
        this.feed = feed;
        this.content = content;
        this.parentComment = parentComment;
    }

    public void addChildComment(FeedComment childComment) {
        childComments.add(childComment);
        childComment.setParentComment(this);
    }

    public void removeChildComment(FeedComment childComment) {
        childComments.remove(childComment);
        childComment.setParentComment(null);
    }

}
