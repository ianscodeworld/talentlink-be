package com.dbs.talentlink.entity;

import com.dbs.talentlink.model.CandidateStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "candidates")
public class Candidate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String resumeSummary;

    @Column(name = "current_interview_round", nullable = false)
    private int currentInterviewRound;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CandidateStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "demand_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Demand demand;

    // --- 新增字段 ---
    @Column(name = "vendor_name")
    private String vendorName;

    @Column(name = "total_rounds_override")
    private Integer totalRoundsOverride;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    @OneToMany(
            mappedBy = "candidate",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private Set<Feedback> feedbacks;

    private String gender;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String skillset;

    private String seniority;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String relatedWorkingExperience;

    private String onboardingTime;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String skillHighlights;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String englishCapability;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String internalInterviewFeedback;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String onlineCodingResult;

}