package com.vitalsync.medication;

import com.vitalsync.shared.enums.LogStatus;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "medication_logs")
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class MedicationLogEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    public UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medication_id", nullable = false)
    @ToString.Exclude
    public MedicationEntity medication;

    @Column(name = "expected_at", nullable = false)
    public LocalDateTime expectedAt;

    @Column(name = "taken_at")
    public LocalDateTime takenAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public LogStatus status;

    @CreationTimestamp
    public LocalDateTime createdAt;
}