package com.whdcks3.portfolio.gory_server.data.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Data
public class JwtResponse {
    private String token;
    private String username;
}
