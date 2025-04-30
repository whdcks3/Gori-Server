package com.whdcks3.portfolio.gory_server.data.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.annotations.DynamicInsert;

import com.google.auto.value.AutoValue.Builder;
import com.whdcks3.portfolio.gory_server.common.BaseEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "announce")
@Getter
@Setter
@Builder
@NoArgsConstructor
@DynamicInsert
public class Announce extends BaseEntity {
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    @Size(max = 5000)
    private String content;

    @lombok.Builder
    public Announce(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
