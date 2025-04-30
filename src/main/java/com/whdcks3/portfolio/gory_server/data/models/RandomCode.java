package com.whdcks3.portfolio.gory_server.data.models;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class RandomCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pid;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String code;

    @CreationTimestamp
    @Column(nullable = false)
    @ColumnDefault("current_timestamp")
    private LocalDateTime created;

    public RandomCode(String email, String code) {
        this.email = email;
        this.code = code;
    }

    public boolean isExpired() {
        // return true;
        return created.plusMinutes(10).isBefore(LocalDateTime.now());
    }

    public boolean isEnabled(boolean Account) {
        return false;
    }
}
