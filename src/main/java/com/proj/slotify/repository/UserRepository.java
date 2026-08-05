package com.proj.slotify.repository;

import com.proj.slotify.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, String> {

    Boolean existsByEmail(String email);

    UserEntity findByEmail(String email);
}
