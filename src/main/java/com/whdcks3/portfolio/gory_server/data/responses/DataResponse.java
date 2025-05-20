package com.whdcks3.portfolio.gory_server.data.responses;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@AllArgsConstructor
@ToString
public class DataResponse {
    private boolean hasNext;
    private List<?> list;
}
