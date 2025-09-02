// com/dbs/talentlink/repository/SpecialtyRepository.java
package com.dbs.talentlink.repository;

import com.dbs.talentlink.entity.Specialty;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Set;

public interface SpecialtyRepository extends JpaRepository<Specialty, Long> {
    Set<Specialty> findByNameIn(List<String> names);
}