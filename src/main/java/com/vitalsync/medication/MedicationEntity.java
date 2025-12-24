package com.vitalsync.medication;

import com.vitalsync.shared.enums.MedicationFrequency;
import com.vitalsync.user.UserEntity;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "medications")
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class MedicationEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    public UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    @ToString.Exclude // Evita loop e lazy loading acidental no log
    public UserEntity patient;

    @Column(nullable = false)
    public String name;

    @Column(nullable = false)
    public String dosage;

    @Column(columnDefinition = "TEXT")
    public String instructions;

    @Enumerated(EnumType.STRING)
    @Column(name = "frequency_type")
    public MedicationFrequency frequencyType;

    @Column(name = "start_date")
    public LocalDate startDate;

    @Column(nullable = false)
    @Builder.Default
    public boolean active = true;

    @OneToMany(mappedBy = "medication", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Builder.Default
    @ToString.Exclude //exclui listas do ToString
    public List<MedicationScheduleEntity> schedules = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at")
    public LocalDateTime createdAt;
}