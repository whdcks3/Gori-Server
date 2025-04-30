package com.whdcks3.portfolio.gory_server.data.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SquadFilterRequest {

    private String category = "전체";
    private String regionMain = "전체";
    private String regionSub = "전체";
    private boolean recruitingOnly = false;
}
