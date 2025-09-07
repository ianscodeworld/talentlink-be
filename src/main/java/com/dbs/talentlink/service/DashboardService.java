// com/dbs/talentlink/service/DashboardService.java
package com.dbs.talentlink.service;

import com.dbs.talentlink.dto.*;
import com.dbs.talentlink.entity.Squad;
import com.dbs.talentlink.entity.User;
import com.dbs.talentlink.model.DemandStatus;
import com.dbs.talentlink.repository.CandidateRepository;
import com.dbs.talentlink.repository.DemandRepository;
import com.dbs.talentlink.repository.FeedbackRepository;
import com.dbs.talentlink.repository.SquadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final CandidateRepository candidateRepository;
    private final DemandRepository demandRepository;
    private final FeedbackRepository feedbackRepository;
    private final SquadRepository squadRepository; // 新增依赖

    @Transactional(readOnly = true)
    public HmDashboardResponse getHmDashboardSummary(User currentUser) {
        Long hmId = currentUser.getId();

        // 1. 获取候选人漏斗 (逻辑不变)
        Map<String, Long> candidateFunnel = candidateRepository.countCandidatesByStatusForHm(hmId).stream()
                .collect(Collectors.toMap(row -> ((Enum<?>) row[0]).name(), row -> (Long) row[1]));

        // 2. 获取面试官周报 (逻辑不变)
        Instant sevenDaysAgo = Instant.now().minus(7, ChronoUnit.DAYS);
        List<InterviewerWorkloadDto> weeklyWorkload = feedbackRepository.countFeedbackByInterviewerForHmSince(hmId, sevenDaysAgo).stream()
                .map(row -> new InterviewerWorkloadDto(((User) row[0]).getId(), ((User) row[0]).getName(), (Long) row[1]))
                .collect(Collectors.toList());

        // 3. 获取无候选人的需求 (逻辑不变)
        List<SimpleDemandDto> demandsWithoutCandidates = demandRepository.findByCreatedByIdAndStatusAndCandidatesIsEmpty(hmId, DemandStatus.OPEN).stream()
                .map(SimpleDemandDto::fromEntity)
                .collect(Collectors.toList());

        // 4. 获取近期反馈 (逻辑不变)
        List<FeedbackResponse> recentFeedbacks = feedbackRepository.findTop10ByCandidateDemandCreatedByIdOrderByCreatedAtDesc(hmId).stream()
                .map(FeedbackResponse::fromEntity)
                .collect(Collectors.toList());

        // 5. --- 新增：获取并计算 Squad 摘要信息 ---
        List<Squad> allSquads = squadRepository.findAllActiveWithDetails();
        List<SquadSummaryDto> squadsSummary = new ArrayList<>();
        for (Squad squad : allSquads) {
            int totalPositions = squad.getDemands().size();
            int filledPositions = (int) squad.getDemands().stream().filter(d -> d.getStatus() == DemandStatus.HIRED).count();

            // 新增 Dashboard 过滤条件：只包括未完成招聘的 Squad
            if (filledPositions >= totalPositions) {
                continue; // 如果所有职位都满了，则不在 Dashboard 显示
            }

            double progress = (totalPositions == 0) ? 0.0 : (double) filledPositions / totalPositions;

            // 计算 roleBreakdown
            Map<String, Long> demandsPerSpecialty = squad.getDemands().stream()
                    .flatMap(d -> d.getSpecialties().stream())
                    .collect(Collectors.groupingBy(s -> s.getName(), Collectors.counting()));

            Map<String, Long> hiredPerSpecialty = squad.getDemands().stream()
                    .filter(d -> d.getStatus() == DemandStatus.HIRED)
                    .flatMap(d -> d.getSpecialties().stream())
                    .collect(Collectors.groupingBy(s -> s.getName(), Collectors.counting()));

            String roleBreakdown = demandsPerSpecialty.entrySet().stream()
                    .map(entry -> String.format("%s: %d/%d",
                            entry.getKey(),
                            hiredPerSpecialty.getOrDefault(entry.getKey(), 0L),
                            entry.getValue()))
                    .collect(Collectors.joining(", "));

            squadsSummary.add(SquadSummaryDto.builder()
                    .squadId(squad.getId())
                    .squadName(squad.getName())
                    .totalPositions(totalPositions)
                    .filledPositions(filledPositions)
                    .progressPercentage(progress)
                    .roleBreakdown(roleBreakdown)
                    .build());
        }

        // 6. 组装并返回
        return HmDashboardResponse.builder()
                .squadsSummary(squadsSummary) // 添加新数据
                .candidateFunnel(candidateFunnel)
                .weeklyInterviewerWorkload(weeklyWorkload)
                .demandsWithoutCandidates(demandsWithoutCandidates)
                .recentFeedbacks(recentFeedbacks)
                .build();
    }
}