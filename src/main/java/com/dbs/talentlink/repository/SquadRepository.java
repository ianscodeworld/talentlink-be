package com.dbs.talentlink.repository;

import com.dbs.talentlink.entity.Squad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.dbs.talentlink.model.SquadStatus; // 新增
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

@Repository
public interface SquadRepository extends JpaRepository<Squad, Long> {
    /**
     * Fetches all squads with their demands, specialties, and candidates to avoid N+1 issues.
     */
    @Query("SELECT DISTINCT s FROM Squad s " +
            "LEFT JOIN FETCH s.demands d " +
            "LEFT JOIN FETCH d.specialties " +
            "LEFT JOIN FETCH d.candidates")
    List<Squad> findAllWithDetails();

    // 查询特定状态、未删除的 Squads
    Page<Squad> findByStatusAndIsDeletedFalse(SquadStatus status, Pageable pageable);

    // 查询所有未删除的 Squads
    Page<Squad> findByIsDeletedFalse(Pageable pageable);

    // (用于Dashboard) 查询所有活跃、未删除的 Squads 并预加载关联数据
    @Query("SELECT DISTINCT s FROM Squad s " +
            "LEFT JOIN FETCH s.demands d " +
            "LEFT JOIN FETCH d.specialties " +
            "LEFT JOIN FETCH d.candidates " +
            "WHERE s.status = com.dbs.talentlink.model.SquadStatus.ACTIVE AND s.isDeleted = false")
    List<Squad> findAllActiveWithDetails();
}