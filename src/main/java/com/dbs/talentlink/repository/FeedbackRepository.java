package com.dbs.talentlink.repository;

import com.dbs.talentlink.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByCandidateIdOrderByInterviewRoundAsc(Long candidateId);
}