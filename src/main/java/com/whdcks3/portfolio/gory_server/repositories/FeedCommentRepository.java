package com.whdcks3.portfolio.gory_server.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.whdcks3.portfolio.gory_server.data.models.feed.FeedComment;
import com.whdcks3.portfolio.gory_server.data.models.user.User;

@Repository
public interface FeedCommentRepository extends JpaRepository<FeedComment, Long> {
    Optional<FeedComment> findByParentCommentPidAndPid(Long feedCommentId, Long uid);

    Page<FeedComment> findByParentCommentPid(Long parentId, Pageable pageable);

    List<FeedComment> findAllByUser(User user);
}
