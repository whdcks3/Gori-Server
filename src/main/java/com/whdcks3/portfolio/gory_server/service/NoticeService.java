package com.whdcks3.portfolio.gory_server.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.whdcks3.portfolio.gory_server.data.dto.NoticeDetailDto;
import com.whdcks3.portfolio.gory_server.data.dto.NoticeSimpleDto;
import com.whdcks3.portfolio.gory_server.data.models.Notice;
import com.whdcks3.portfolio.gory_server.data.responses.DataResponse;
import com.whdcks3.portfolio.gory_server.repositories.NoticeRepository;

@Service
public class NoticeService {

    @Autowired
    NoticeRepository noticeRepository;

    public DataResponse notice(Notice notice, Pageable pageable) {

        Page<Notice> noticeList = noticeRepository.findAll(pageable);
        List<NoticeSimpleDto> noticeDtos = noticeList.getContent().stream().map(NoticeSimpleDto::toDto).toList();
        return new DataResponse(noticeList.hasNext(), noticeDtos);
    }

    public NoticeDetailDto noticeDetail(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId).orElseThrow();
        return NoticeDetailDto.toDto(notice);
    }
}
