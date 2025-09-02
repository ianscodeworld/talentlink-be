package com.dbs.talentlink.entity;

import com.dbs.talentlink.model.CandidateStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

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
    private Demand demand;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendor vendor;

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
    private List<Feedback> feedbacks = new ArrayList<>();

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