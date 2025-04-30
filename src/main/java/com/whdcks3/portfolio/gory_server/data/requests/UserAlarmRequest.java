package com.whdcks3.portfolio.gory_server.data.requests;

import com.whdcks3.portfolio.gory_server.enums.AlarmType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserAlarmRequest {
    private AlarmType alarmType;
    private Boolean alarmEnabled;
}
