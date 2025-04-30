package com.whdcks3.portfolio.gory_server.data.dto;

import java.time.LocalDate;

import com.whdcks3.portfolio.gory_server.data.models.Notice;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NoticeDetailDto {

    private Long pid;
    private String title;
    private String content;
    private LocalDate date;

    public static NoticeDetailDto toDto(Notice notice) {
        Long pid = notice.getPid();
        String title = notice.getTitle();
        String content = notice.getContent();
        LocalDate date = notice.getCreatedAt().toLocalDate();

        return new NoticeDetailDto(pid, title, content, date);
    }
}
