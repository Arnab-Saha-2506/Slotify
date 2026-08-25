package com.proj.slotify.repository;

import com.proj.slotify.entity.GoogleCalenderCredentialEntity;
import com.proj.slotify.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface GoogleCalenderCredentialRepository extends JpaRepository<GoogleCalenderCredentialEntity, String> {
    Optional<GoogleCalenderCredentialEntity> findByUser(UserEntity user);

    Optional<GoogleCalenderCredentialEntity> findByUserId(String userId);
}
