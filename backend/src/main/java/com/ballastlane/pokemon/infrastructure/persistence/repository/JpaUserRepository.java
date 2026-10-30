package com.ballastlane.pokemon.infrastructure.persistence.repository;

import com.ballastlane.pokemon.domain.entity.User;
import com.ballastlane.pokemon.domain.repository.UserRepository;
import com.ballastlane.pokemon.infrastructure.persistence.mapper.UserMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaUserRepository implements UserRepository {

    private final JpaUserSpringRepo springRepo;
    private final UserMapper mapper;

    public JpaUserRepository(JpaUserSpringRepo springRepo, UserMapper mapper) {
        this.springRepo = springRepo;
        this.mapper = mapper;
    }

    @Override
    public User save(User user) {
        return mapper.toDomain(springRepo.save(mapper.toEntity(user)));
    }

    @Override
    public Optional<User> findById(UUID id) {
        return springRepo.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return springRepo.findByUsername(username).map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return springRepo.findByEmail(email).map(mapper::toDomain);
    }

    @Override
    public boolean existsByUsername(String username) {
        return springRepo.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return springRepo.existsByEmail(email);
    }
}
