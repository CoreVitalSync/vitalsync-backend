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
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class MedicationLogEntity extends PanacheEntityBase {

    @Id
    @EqualsAndHashCode.Include
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medication_id", nullable = false)
    @ToString.Exclude
    private MedicationEntity medication;

    @Column(name = "expected_at", nullable = false)
    private LocalDateTime expectedAt;

    @Column(name = "taken_at")
    private LocalDateTime takenAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LogStatus status;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @PrePersist
    protected void generateId() {
        if (this.id == null) {
            long timestamp = System.currentTimeMillis();
            UUID randomUuid = UUID.randomUUID();
            long msb = (timestamp << 16) | (0x7000L) | (randomUuid.getMostSignificantBits() & 0x0FFFL);
            long lsb = randomUuid.getLeastSignificantBits();
            this.id = new UUID(msb, lsb);
        }
    }
}