package com.whdcks3.portfolio.gory_server.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.whdcks3.portfolio.gory_server.data.models.user.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByPhone(String phone);

    Optional<User> findBySnsTypeAndSnsId(String snsType, String snsId);

    Optional<User> findBySnsType(String snsType);

    Optional<User> findByEmailAndSnsType(String email, String snsType);

    Boolean existsByEmail(String email);

    Boolean existsByPhone(String phone);

    Boolean existsByPhoneAndEmail(String email, String phone);

    Boolean existsBySnsTypeAndSnsId(String snsType, String snsId);

    Boolean existsByEmailAndSnsType(String email, String snsType);

    Boolean existsByEmailAndSnsTypeAndSnsId(String email, String snsType, String snsId);

    Boolean existsByPidNotAndNickname(Long pid, String nickname);

    Boolean existsByNickname(String nickname);

    Boolean existsByNicknameAndPidNot(String nickname, Long pid);

    Optional<User> findByNickname(String nickname);

}
