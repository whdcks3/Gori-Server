package com.whdcks3.portfolio.gory_server.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.whdcks3.portfolio.gory_server.data.models.report.ReportUser;

@Repository
public interface ReportUserRepository extends JpaRepository<ReportUser, Long> {
}
