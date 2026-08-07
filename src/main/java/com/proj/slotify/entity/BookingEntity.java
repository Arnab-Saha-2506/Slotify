package com.proj.slotify.entity;

import com.proj.slotify.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Entity
public class BookingEntity extends BaseEntity{
    @Id
    @Column(length = 8, nullable = false, updatable = false)
    private String bookingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private UserEntity owner;

    @Column(length = 30, nullable = false)
    private String guestName;

    @Column(length = 255, nullable = false)
    private String guestEmail;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;
}
