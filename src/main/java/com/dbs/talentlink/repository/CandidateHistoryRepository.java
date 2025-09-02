package com.dbs.talentlink.repository;

import com.dbs.talentlink.entity.CandidateHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CandidateHistoryRepository extends JpaRepository<CandidateHistory, Long> {
    Page<CandidateHistory> findByCandidateId(Long candidateId, Pageable pageable);

}
