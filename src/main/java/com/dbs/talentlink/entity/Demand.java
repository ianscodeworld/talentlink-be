package com.dbs.talentlink.entity;

import com.dbs.talentlink.model.DemandStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "demands")
@SQLDelete(sql = "UPDATE demands SET is_deleted = true WHERE id = ?") // <-- 新增
@Where(clause = "is_deleted = false") // <-- 新增
public class Demand {

    // --- 新增字段 ---
    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private boolean isDeleted = false;

    @Column(name = "required_positions", nullable = false)
    @Builder.Default
    private int requiredPositions = 1;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "job_title", nullable = false)
    private String jobTitle;

    @Lob // For longer text, maps to TEXT type in MariaDB
    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "total_interview_rounds", nullable = false)
    private int totalInterviewRounds;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DemandStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", nullable = false)
    private User createdBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    @OneToMany(
            mappedBy = "demand",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private Set<Candidate> candidates;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "demand_specialties",
            joinColumns = @JoinColumn(name = "demand_id"),
            inverseJoinColumns = @JoinColumn(name = "specialty_id")
    )
    private Set<Specialty> specialties;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "squad_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Squad squad;
}