package com.whdcks3.portfolio.gory_server.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.whdcks3.portfolio.gory_server.data.models.feed.Feed;
import com.whdcks3.portfolio.gory_server.data.models.user.User;

@Repository
public interface FeedRepository extends JpaRepository<Feed, Long> {

    List<Feed> findAllByOrderByCreatedAtDesc();

    List<Feed> findAllByCategoryOrderByCreatedAtDesc(String category);

    Page<Feed> findAllByCategory(String category, Pageable pageable);

    Page<Feed> findAll(Pageable pageable);

    // 확정

    Page<Feed> findByCategoryAndUserNotIn(String category, Iterable<User> users,
            Pageable pageable);

    Page<Feed> findByUser(User user, Pageable pageable);

    Page<Feed> findByUserNotIn(Iterable<User> users, Pageable pageable);

    List<Feed> findAllByUser(User user);
}
