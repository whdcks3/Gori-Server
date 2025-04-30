package com.whdcks3.portfolio.gory_server.repositories;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import com.whdcks3.portfolio.gory_server.data.models.Block;
import com.whdcks3.portfolio.gory_server.data.models.user.User;

@Repository
public interface BlockRespository extends JpaRepository<Block, Long> {
    List<Block> findByBlocker(User user);

    List<Block> findByBlocked(User other);

    Optional<Block> findByBlockerAndBlocked(User user, User other);

    boolean existsByBlockerAndBlocked(User user, User other);

    @Transactional
    @Modifying
    void deleteByBlockerAndBlocked(User user, User other);

    @Transactional
    @Modifying
    void deleteByBlocker(User user);

    @Transactional
    @Modifying
    void deleteByBlocked(User other);
}
