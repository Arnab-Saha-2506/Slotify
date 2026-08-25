package com.proj.slotify.entity;

import com.proj.slotify.enums.AuthProvider;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserEntity extends BaseEntity {

    @Id
    @Column(length = 8, nullable = false, updatable = false)
    private String id;

    @Column(length = 31, nullable = false)
    private String name;

    @Column(length = 255, nullable = false, unique = true)
    private String email;

    @Column(length = 255, nullable = false)
    private String password;

    @Column(nullable = false)
    private String timezone;

    @Column(nullable = false)
    @org.hibernate.annotations.ColumnDefault("LOCAL")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private AuthProvider authProvider = AuthProvider.LOCAL;

    @OneToMany(mappedBy = "user")
    private List<AvailabilityEntity> availabilityEntityList;

    @OneToOne(mappedBy = "user")
    private GoogleCalenderCredentialEntity googleCalenderCredentialEntity;


}
