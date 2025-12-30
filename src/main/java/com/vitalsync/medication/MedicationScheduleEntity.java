package com.vitalsync.medication;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "medication_schedules")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class MedicationScheduleEntity extends PanacheEntityBase {

    @Id
    @EqualsAndHashCode.Include
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "medication_id", nullable = false)
    @ToString.Exclude
    private MedicationEntity medication;

    @Column(name = "scheduled_time", nullable = false)
    private LocalTime scheduledTime;

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