package com.whdcks3.portfolio.gory_server.common;

import java.time.LocalDate;
import java.time.Period;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.whdcks3.portfolio.gory_server.enums.Role;
import com.whdcks3.portfolio.gory_server.security.service.CustomUserDetails;

public class Utils {
    public static int calculateAge(LocalDate dob) {
        LocalDate curDate = LocalDate.now();
        if ((dob != null) && (curDate != null)) {
            return Period.between(dob, curDate).getYears();
        } else {
            return 0;
        }
    }

    static public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || AnonymousAuthenticationToken.class.isAssignableFrom(authentication.getClass())) {
            return false;
        }
        return authentication.isAuthenticated();
    }

    static public Role getRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || AnonymousAuthenticationToken.class.isAssignableFrom(authentication.getClass())) {
            return Role.USER;
        }
        Set<String> roles = authentication.getAuthorities().stream()
                .map(r -> r.getAuthority()).collect(Collectors.toSet());
        String role = roles.iterator().next();
        return Role.USER.name().equals(role) ? Role.USER : Role.ADMIN;
    }

    static public String getNickname() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || AnonymousAuthenticationToken.class.isAssignableFrom(authentication.getClass())) {
            return "";
        }
        return ((CustomUserDetails) authentication.getPrincipal()).getNickname();
    }

    static public Long getPid() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || AnonymousAuthenticationToken.class.isAssignableFrom(authentication.getClass())) {
            return -1L;
        }
        return ((CustomUserDetails) authentication.getPrincipal()).getPid();
    }

    static public boolean checkLockedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || AnonymousAuthenticationToken.class.isAssignableFrom(authentication.getClass())) {
            return false;
        }
        return ((CustomUserDetails) authentication.getPrincipal()).isAccountNonLocked();
    }
}
