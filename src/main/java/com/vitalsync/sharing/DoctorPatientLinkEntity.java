package com.vitalsync.sharing;

import com.vitalsync.shared.enums.LinkStatus;
import com.vitalsync.user.UserEntity;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "doctor_patient_links")
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class DoctorPatientLinkEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    @ToString.Exclude
    private UserEntity patient;

    @ManyToOne
    @JoinColumn(name = "doctor_id")
    @ToString.Exclude
    private UserEntity doctor;

    @Column(name = "invite_token", unique = true)
    private String inviteToken;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LinkStatus status;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "linked_at")
    private LocalDateTime linkedAt;
}