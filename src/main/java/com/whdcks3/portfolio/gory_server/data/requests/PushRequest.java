package com.whdcks3.portfolio.gory_server.data.requests;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PushRequest {
    String title, body, link, fcmToken;
}
