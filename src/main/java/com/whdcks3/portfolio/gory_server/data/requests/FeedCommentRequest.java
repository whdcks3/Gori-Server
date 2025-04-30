package com.whdcks3.portfolio.gory_server.data.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedCommentRequest {
    private Long feedPid;
    private String content;
    private Long feedCommentPid;
}
