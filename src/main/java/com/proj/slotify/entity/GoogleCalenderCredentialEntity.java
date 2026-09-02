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
//    @MapsId
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserEntity user;

    @Column(columnDefinition = "TEXT")
    private String accessToken;

    @Column(columnDefinition = "TEXT")
    private String refreshToken;

    @Column(nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    private Boolean connected = false;

    @PrePersist
    protected void onClick(){
        if(this.id == null){
            this.id = java.util.UUID.randomUUID().toString();
        }
    }

}
