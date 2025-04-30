package com.whdcks3.portfolio.gory_server.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.whdcks3.portfolio.gory_server.data.models.report.ReportSquad;

@Repository
public interface ReportSquadRepository extends JpaRepository<ReportSquad, Long> {

}
