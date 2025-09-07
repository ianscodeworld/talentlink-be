package com.dbs.talentlink.service;

import com.dbs.talentlink.dto.CandidateResponse;
import com.dbs.talentlink.entity.Candidate;
import com.dbs.talentlink.repository.CandidateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final CandidateRepository candidateRepository;

    @Transactional(readOnly = true)
    public Page<CandidateResponse> searchCandidates(String query, Pageable pageable) {
        if (!StringUtils.hasText(query)) {
            return Page.empty(pageable);
        }

        Specification<Candidate> spec = (root, q, cb) -> {
            String lowercaseQuery = "%" + query.toLowerCase() + "%";

            // --- 核心修改点 ---
            // 对 name (VARCHAR) 使用 lower()
            var namePredicate = cb.like(cb.lower(root.get("name")), lowercaseQuery);
            // 对 skillset (TEXT/CLOB) 直接使用 like，不使用 lower()
            var skillsetPredicate = cb.like(root.get("skillset"), lowercaseQuery);

            return cb.or(namePredicate, skillsetPredicate);
        };

        return candidateRepository.findAll(spec, pageable)
                .map(CandidateResponse::fromEntity);
    }
}