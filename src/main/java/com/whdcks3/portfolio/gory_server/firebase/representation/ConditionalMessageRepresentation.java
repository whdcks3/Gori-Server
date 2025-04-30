package com.whdcks3.portfolio.gory_server.firebase.representation;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConditionalMessageRepresentation {
    private String condition;
    private String data;
}
