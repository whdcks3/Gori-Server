package com.whdcks3.portfolio.gory_server.data.models;

import javax.persistence.Column;
import javax.validation.constraints.Size;

import com.whdcks3.portfolio.gory_server.common.BaseEntity;

import lombok.Builder;

public class Feedback extends BaseEntity {

    private String namenickname;

    @Column(columnDefinition = "TEXT", nullable = false)
    @Size(max = 5000)
    private String content;

    @Builder
    public Feedback(com.whdcks3.portfolio.gory_server.data.models.user.User user, String content) {
        this.namenickname = String.format("%s(%s)", user.getName(), user.getNickname());
        this.content = content;
    }
}
