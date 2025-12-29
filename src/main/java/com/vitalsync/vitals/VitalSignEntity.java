package com.vitalsync.vitals;

import com.vitalsync.shared.enums.VitalSignType;
import com.vitalsync.user.UserEntity;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "vital_signs")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class VitalSignEntity extends PanacheEntityBase {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @EqualsAndHashCode.Include
    public UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    @ToString.Exclude
    public UserEntity patient;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public VitalSignType type;

    @Column(name = "value_major", nullable = false, precision = 10, scale = 2)
    public BigDecimal valueMajor;

    @Column(name = "value_minor", precision = 10, scale = 2)
    public BigDecimal valueMinor;

    @Column(name = "measured_at", nullable = false)
    public LocalDateTime measuredAt;

    @Column(columnDefinition = "TEXT")
    public String notes;
}