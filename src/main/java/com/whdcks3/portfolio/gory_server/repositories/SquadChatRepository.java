package com.whdcks3.portfolio.gory_server.repositories;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.whdcks3.portfolio.gory_server.data.models.squad.SquadChat;

@Repository
public interface SquadChatRepository extends JpaRepository<SquadChat, Long> {
        List<SquadChat> findBySquadPidOrderByCreatedAtAsc(Long squadPid);

        // 최신 메시지 20개 조회(진입 시)
        @Query("SELECT c FROM SquadChat c WHERE c.squad.id = :squadId ORDER BY c.id DESC")
        List<SquadChat> findLatestChats(@Param("squadId") Long squadId, Pageable pageable);

        // 기준 ID 이전 메시지 조회 (무한 스크롤용)
        @Query("SELECT c FROM SquadChat c WHERE c.squad.id = :squadId AND c.id < :lastId ORDER BY c.id DESC")
        List<SquadChat> findPreviousChats(@Param("squadId") Long squadId, @Param("lastId") Long lastId,
                        Pageable pageable);

        // 오늘 날짜 메시지가 이미 존재하는지 확인 (날짜 라벨 중복 방지용)
        @Query("SELECT COUNT(c) > 0 FROM SquadChat c WHERE c.squad.id = :squadId AND c.type = 'DATE' AND FUNCTION('DATE', c.createdAt) = CURRENT_DATE")
        boolean existsTodayDateChat(@Param("squadId") Long squadId);
}
