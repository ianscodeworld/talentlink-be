package com.dbs.talentlink.repository;

import com.dbs.talentlink.entity.Candidate;
import com.dbs.talentlink.entity.InterviewAssignment;
import com.dbs.talentlink.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InterviewAssignmentRepository extends JpaRepository<InterviewAssignment, Long> {
    Optional<InterviewAssignment> findByCandidateIdAndInterviewRound(Long candidateId, int interviewRound);

    Page<InterviewAssignment> findByInterviewerIdAndIsCompletedFalse(Long interviewerId, Pageable pageable);

    Optional<InterviewAssignment> findByCandidateAndInterviewerAndIsCompletedFalse(Candidate candidate, User interviewer);

}