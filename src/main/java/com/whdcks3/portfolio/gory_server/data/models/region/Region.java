package com.whdcks3.portfolio.gory_server.data.models.region;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;

import com.google.auto.value.AutoValue.Builder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "region")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@DynamicInsert
@IdClass(RegionId.class)
public class Region {

    @Id
    @Column
    private String mainRegion;

    @Id
    @Column
    private String subRegion;

    private int position;

    public Region(String mainRegion, String subRegion) {
        this.mainRegion = mainRegion;
        this.subRegion = subRegion;
    }
}
