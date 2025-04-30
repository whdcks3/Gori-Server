package com.whdcks3.portfolio.gory_server.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

import javax.servlet.http.HttpServletRequest;

import com.whdcks3.portfolio.gory_server.data.models.user.User;
import com.whdcks3.portfolio.gory_server.enums.LockType;
import com.whdcks3.portfolio.gory_server.repositories.UserRepository;

@Service
public class CustomerUserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    private HttpServletRequest request;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("loadUserByUsername: " + username);
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않은 사용자입니다"));
        if (user.getLockType().equals(LockType.MANY_ATTEMPTS)) {
            String lockedTime = user.getLockedUntil().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분 sss초"));
            request.setAttribute("failedType", "locked");
            request.setAttribute("lockedTime", lockedTime);
            throw new LockedException("계정이 잠겨있습니다. " + lockedTime + "에 잠금이 해제됩니다.");
        }
        if (user.getLockType().equals(LockType.EMAIL_AUTH)) {
            request.setAttribute("failedType", "inactive");
            throw new DisabledException("인증되지 않은 계정입니다. 메일을 확인하여 인증해주세요.");
        }
        return CustomUserDetails.build(user);
    }

}
