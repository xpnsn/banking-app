package com.safevault.security.service;

import com.safevault.security.dto.CustomUserDetails;
import com.safevault.security.dto.UserDto;
import com.safevault.security.entity.UserEntity;
import com.safevault.security.mapper.DtoMapper;
import com.safevault.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DtoMapper dtoMapper;

    @Override
    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            UserEntity user = userRepository.findByUsername(username);
            return new CustomUserDetails(
                    user.getId(),
                    user.getUsername(),
                    user.getPassword(),
                    user.getEmail(),
                    user.getName(),
                    user.getVerifiedEmailAddress()&&user.getVerifiedPhoneNumber(),
                    user.getRoles().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList())
            );
        } catch (UsernameNotFoundException e) {
            throw new UsernameNotFoundException(username);
        }
    }
}
