package com.whdcks3.portfolio.gory_server.data.requests;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportRequest {
    private Long targetId;

    private String content;

    @NotBlank
    private String category;
}
