package com.whdcks3.portfolio.gory_server.service.interfaces;

import com.whdcks3.portfolio.gory_server.data.models.squad.Squad;

public interface ISquadFcm {
    void squadNewMemberJoined(Squad squad);

    void squadRefusePartipate(Squad squad);

    void squadBannedPartipate(Squad squad);

    // 30분에 한 번씩 실행
    // 날짜는 필수, 시간은 선택
    // 날짜만 있는 경우엔, 모임일 오전 8시 기준 24시간 전
    // 시간도 있을 경우엔, 모일 일시 기준 24시간 전
    void squad1DNotifying();

}
