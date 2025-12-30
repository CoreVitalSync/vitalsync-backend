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
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class MedicationEntity extends PanacheEntityBase {

    @Id
    @EqualsAndHashCode.Include
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    @ToString.Exclude // Evita loop e lazy loading acidental no log
    private UserEntity patient;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String dosage;

    @Column(columnDefinition = "TEXT")
    private String instructions;

    @Enumerated(EnumType.STRING)
    @Column(name = "frequency_type")
    private MedicationFrequency frequencyType;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    @OneToMany(mappedBy = "medication", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude //exclui listas do ToString
    private List<MedicationScheduleEntity> schedules = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at")
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