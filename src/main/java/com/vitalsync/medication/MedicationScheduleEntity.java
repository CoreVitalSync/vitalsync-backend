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
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class MedicationScheduleEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "medication_id", nullable = false)
    @ToString.Exclude
    private MedicationEntity medication;

    @Column(name = "scheduled_time", nullable = false)
    private LocalTime scheduledTime;
}