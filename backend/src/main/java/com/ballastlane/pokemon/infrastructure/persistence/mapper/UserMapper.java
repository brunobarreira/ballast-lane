package com.ballastlane.pokemon.infrastructure.persistence.mapper;

import com.ballastlane.pokemon.domain.entity.User;
import com.ballastlane.pokemon.infrastructure.persistence.entity.UserJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toDomain(UserJpaEntity e) {
        return new User(
            e.getId(), e.getUsername(), e.getEmail(), e.getPasswordHash(),
            User.Role.valueOf(e.getRole().name())
        );
    }

    public UserJpaEntity toEntity(User u) {
        UserJpaEntity e = new UserJpaEntity();
        e.setId(u.getId());
        e.setUsername(u.getUsername());
        e.setEmail(u.getEmail());
        e.setPasswordHash(u.getPasswordHash());
        e.setRole(UserJpaEntity.Role.valueOf(u.getRole().name()));
        return e;
    }
}
