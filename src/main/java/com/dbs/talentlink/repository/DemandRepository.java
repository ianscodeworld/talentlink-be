package com.dbs.talentlink.repository;

import com.dbs.talentlink.entity.Demand;
import com.dbs.talentlink.entity.Specialty;
import com.dbs.talentlink.model.DemandStatus;
import org.springframework.data.domain.Page; // 新增 import
import org.springframework.data.domain.Pageable; // 新增 import
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface DemandRepository extends JpaRepository<Demand, Long> {

    // @Where 会自动处理 is_deleted=false, 无需改动
    Page<Demand> findByCreatedById(Long userId, Pageable pageable);

    // @Where 也会自动处理
    Page<Demand> findByStatus(DemandStatus status, Pageable pageable);

    // 对于自定义的 @Query, 我们需要手动加入 is_deleted=false 条件
    @Query("SELECT DISTINCT d FROM Demand d JOIN d.specialties s WHERE d.isDeleted = false AND d.status = :status AND s IN :specialties")
    Page<Demand> findByStatusAndSpecialtiesIn(
            @Param("status") DemandStatus status,
            @Param("specialties") Set<Specialty> specialties,
            Pageable pageable);

    // @Where 也会自动处理
    List<Demand> findByCreatedByIdAndStatusAndCandidatesIsEmpty(Long createdById, DemandStatus status);

}