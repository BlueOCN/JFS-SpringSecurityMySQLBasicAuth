package com.blueocn.SecurityMySqlDatabase.data.repository;

import com.blueocn.SecurityMySqlDatabase.data.entity.AuthorityEntity;
import com.blueocn.SecurityMySqlDatabase.data.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthorityRepository extends JpaRepository<AuthorityEntity, Long> {
    Optional<AuthorityEntity> findByUsername(String username);
}
