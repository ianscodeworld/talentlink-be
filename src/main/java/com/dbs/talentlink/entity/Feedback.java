package com.dbs.talentlink.entity;

import com.dbs.talentlink.model.Recommendation;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "feedbacks")
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob

    @Column(columnDefinition = "TEXT", nullable = false)
    private String evaluationText;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Recommendation recommendation;

    @Column(name = "interview_round", nullable = false)
    private int interviewRound;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Candidate candidate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interviewer_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User interviewer;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;
}