package com.whdcks3.portfolio.gory_server.data.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Data
public class ErrorResponse {
    private Integer errCode;
    private String message;
}
