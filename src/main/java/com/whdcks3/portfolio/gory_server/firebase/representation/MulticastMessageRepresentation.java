package com.whdcks3.portfolio.gory_server.firebase.representation;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MulticastMessageRepresentation {
    private String title;
    private String message;
    private String data;
    private List<String> registrationTokens;
}
