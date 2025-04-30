package com.whdcks3.portfolio.gory_server.data.dto;

import java.time.LocalDate;

import com.whdcks3.portfolio.gory_server.data.models.Notice;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NoticeSimpleDto {

    private Long pid;
    private String title;
    private LocalDate date;

    public static NoticeSimpleDto toDto(Notice notice) {
        Long pid = notice.getPid();
        String title = notice.getTitle();
        LocalDate date = notice.getCreatedAt().toLocalDate();

        return new NoticeSimpleDto(pid, title, date);
    }
}
