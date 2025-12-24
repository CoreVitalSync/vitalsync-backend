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
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class MedicationScheduleEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    public UUID id;

    @ManyToOne
    @JoinColumn(name = "medication_id", nullable = false)
    @ToString.Exclude
    public MedicationEntity medication;

    @Column(name = "scheduled_time", nullable = false)
    public LocalTime scheduledTime;
}