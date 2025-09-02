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
    // 将返回类型从 List<Demand> 改为 Page<Demand>
    // 添加 Pageable 参数
    Page<Demand> findByCreatedById(Long userId, Pageable pageable);
    Page<Demand> findByStatus(DemandStatus status, Pageable pageable);
    @Query("SELECT DISTINCT d FROM Demand d JOIN d.specialties s WHERE s IN :userSpecialties OR 'FULLSTACK' IN (SELECT sp.name FROM Specialty sp WHERE sp IN :userSpecialties)")
    Page<Demand> findRelevantDemands(@Param("userSpecialties") Set<Specialty> userSpecialties, Pageable pageable);

    @Query("SELECT DISTINCT d FROM Demand d JOIN d.specialties s WHERE d.status = :status AND s IN :specialties")
    Page<Demand> findByStatusAndSpecialtiesIn(
            @Param("status") DemandStatus status,
            @Param("specialties") Set<Specialty> specialties,
            Pageable pageable);
}