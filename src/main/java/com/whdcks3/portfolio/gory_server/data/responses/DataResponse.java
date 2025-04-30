package com.whdcks3.portfolio.gory_server.data.responses;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class DataResponse {
    private boolean hasNext;
    private List<?> list;
}
