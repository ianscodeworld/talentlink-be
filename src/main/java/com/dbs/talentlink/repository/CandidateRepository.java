package com.dbs.talentlink.repository;

import com.dbs.talentlink.entity.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import com.dbs.talentlink.entity.Specialty;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Long>, JpaSpecificationExecutor<Candidate> {
    /**
     * Finds candidates whose names contain the given string, ignoring case.
     * @param name The substring of the candidate name to search for.
     * @return A list of matching candidates.
     */
    List<Candidate> findByNameContainingIgnoreCase(String name);

    /**
     * Finds candidates with a similar name across all demands that share at least one specialty
     * with the specialties provided.
     * @param name The candidate name to search for (case-insensitive).
     * @param specialties The set of specialties to match against.
     * @return A list of potential duplicate candidates.
     */
    @Query("SELECT DISTINCT c FROM Candidate c " +
            "JOIN c.demand d " +
            "JOIN d.specialties s " +
            "WHERE lower(c.name) = lower(:name) AND s IN :specialties")
    List<Candidate> findDuplicatesInSimilarDemands(@Param("name") String name, @Param("specialties") Set<Specialty> specialties);

    @Query("SELECT c.status, COUNT(c.id) FROM Candidate c " +
            "WHERE c.demand.createdBy.id = :hmId AND c.demand.status = com.dbs.talentlink.model.DemandStatus.OPEN " +
            "GROUP BY c.status")
    List<Object[]> countCandidatesByStatusForHm(@Param("hmId") Long hmId);
}