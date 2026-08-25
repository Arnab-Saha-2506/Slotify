package com.proj.slotify.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class GoogleCalenderCredentialEntity extends BaseEntity{
    @Id
    @Column(length = 8, nullable = false, updatable = false)
    private String id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private UserEntity user;

    @Column(length = 255, nullable = false)
    private String accessToken;

    @Column(length = 255)
    private String refreshToken;

    @Column(nullable = false)
    private Instant expiresAt;

}
