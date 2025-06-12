package com.safevault.security.mapper;

import com.safevault.security.dto.UserDto;
import com.safevault.security.entity.UserEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Function;

@Component
public class DtoMapper implements Function<UserEntity, UserDto> {

    @Override
    public UserDto apply(UserEntity userEntity) {
        return new UserDto(
                userEntity.getId(),
                userEntity.getUsername(),
                userEntity.getPhoneNumber(),
                userEntity.getName(),
                userEntity.getEmail(),
                userEntity.getRoles(),
                userEntity.getAccountIds().stream().toList()
        );
    }
}
