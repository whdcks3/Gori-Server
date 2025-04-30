package com.whdcks3.portfolio.gory_server.data.models;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;

import com.google.auto.value.AutoValue.Builder;
import com.whdcks3.portfolio.gory_server.common.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "setting")
@Getter
@Setter
@Builder
@AllArgsConstructor
@DynamicInsert
public class Setting extends BaseEntity {
    String privacy, terms;

    public Setting() {
        privacy = "";
        terms = "";
    }
}
