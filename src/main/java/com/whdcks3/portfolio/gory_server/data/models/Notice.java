package com.whdcks3.portfolio.gory_server.data.models;

import javax.persistence.Entity;

import com.whdcks3.portfolio.gory_server.common.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Notice extends BaseEntity {

    private String title;

    private String content;

}
