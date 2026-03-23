package com.example.demo.security;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserMasterUserDetailsService implements UserDetailsService {

    private final UserMasterMapper mapper;

    public UserMasterUserDetailsService(UserMasterMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var userMaster = mapper.findByUserName(username);
        if (userMaster == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(userMaster.getRole()));
        return User.withUsername(userMaster.getUserName())
                .password(userMaster.getPassword())
                .authorities(authorities)
                .build();
    }
}
