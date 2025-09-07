package com.dbs.talentlink.repository;

import com.dbs.talentlink.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.time.Instant;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByCandidateIdOrderByInterviewRoundAsc(Long candidateId);

    // --- 新增方法 ---
    boolean existsByCandidateIdAndInterviewerIdAndInterviewRound(Long candidateId, Long interviewerId, int interviewRound);

    @Query("SELECT f.interviewer, COUNT(f.id) FROM Feedback f " +
            "WHERE f.candidate.demand.createdBy.id = :hmId AND f.createdAt >= :since " +
            "GROUP BY f.interviewer " +
            "ORDER BY COUNT(f.id) DESC")
    List<Object[]> countFeedbackByInterviewerForHmSince(@Param("hmId") Long hmId, @Param("since") Instant since);

    List<Feedback> findTop10ByCandidateDemandCreatedByIdOrderByCreatedAtDesc(Long hmId);
}
