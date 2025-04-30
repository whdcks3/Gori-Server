package com.whdcks3.portfolio.gory_server.data.responses;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class CommonResponse {
    int statusCode;
    String message;
}
