package com.whdcks3.portfolio.gory_server.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.whdcks3.portfolio.gory_server.data.models.RandomCode;

public interface RandomCodeRepository extends JpaRepository<RandomCode, Long> {

    RandomCode findTopByEmailOrderByCreatedDesc(String email);

    RandomCode findByCode(String code);

    void deleteByEmail(String email);
}
