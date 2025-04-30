package com.whdcks3.portfolio.gory_server.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.whdcks3.portfolio.gory_server.data.models.feed.Feed;
import com.whdcks3.portfolio.gory_server.data.models.feed.FeedLike;
import com.whdcks3.portfolio.gory_server.data.models.user.User;

@Repository
public interface FeedLikeRepository extends JpaRepository<FeedLike, Long> {
    boolean existsByFeedAndUser(Feed feed, User user);

    Optional<FeedLike> findByFeedAndUser(Feed feed, User user);

    List<FeedLike> findAllByUser(User user);

    List<FeedLike> findAllByFeed(Feed feed);
}
