package com.whdcks3.portfolio.gory_server.security.service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.whdcks3.portfolio.gory_server.data.models.user.User;

public class CustomUserDetails implements UserDetails {

    private static final long serialVersionUID = 1L;

    private Long pid;
    private String email;
    private String nickname;

    @JsonIgnore
    private String password;

    @JsonIgnore
    private String snsType;
    @JsonIgnore
    private String snsId;
    @JsonIgnore
    private String phone;
    @JsonIgnore
    private String name;
    @JsonIgnore
    private LocalDate birth;

    private Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(Long id, String email, String nickname, String password, String phone, String snsType,
            String snsId, String name, LocalDate birth,
            Collection<? extends GrantedAuthority> authorities) {
        this.pid = id;
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.phone = phone;
        this.snsType = snsType;
        this.snsId = snsId;
        this.name = name;
        this.birth = birth;
        this.authorities = authorities;
    }

    public static CustomUserDetails build(User user) {
        // List<SimpleGrantedAuthority> authorities = user.getAuthorities();
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(user.getRole().name());
        List<SimpleGrantedAuthority> authorities = List.of(authority);

        return new CustomUserDetails(
                user.getPid(),
                user.getEmail(),
                user.getNickname(),
                user.getPassword(),
                user.getPhone(),
                user.getSnsType(),
                user.getSnsId(),
                user.getName(),
                user.getBirth(),
                authorities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public Long getPid() {
        return pid;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        CustomUserDetails user = (CustomUserDetails) o;
        return Objects.equals(pid, user.pid);
    }

    public String getPhone() {
        return phone;
    }

    public String getNickname() {
        return nickname;
    }
}
