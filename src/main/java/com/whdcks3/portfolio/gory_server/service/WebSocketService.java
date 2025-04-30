package com.whdcks3.portfolio.gory_server.service;

import org.springframework.stereotype.Service;

import com.whdcks3.portfolio.gory_server.data.models.user.User;
import com.whdcks3.portfolio.gory_server.exception.UsernameNotFoundException;
import com.whdcks3.portfolio.gory_server.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WebSocketService {

    private final UserRepository userRepository;

    // 메시지에 유저 닉네임 가져오기
    public User findByUsername(String username) {
        return userRepository.findByNickname(username)
                .orElseThrow(() -> new UsernameNotFoundException("User NotFound"));
    }
}
