-- MVP 1.5 Schema Changes for TalentLink

-- 1. 更新 DEMANDS 表
-- 扩展 status 字段的 CHECK 约束以包含 'HIRED'
-- 注意: MariaDB/MySQL 中, 如果约束是内联定义的, 可能需要先查出约束名再删除
-- 为了脚本的健壮性，我们通常会先删除旧约束，再添加新约束。
-- 假设 V1 中的约束名为 demands_chk_1, candidates_chk_1 (根据数据库实际情况可能不同)
-- ALTER TABLE demands DROP CONSTRAINT demands_chk_1;
-- ALTER TABLE demands ADD CONSTRAINT demands_status_check CHECK (status IN ('OPEN', 'HIRED', 'CLOSED', 'ON_HOLD'));
-- 更简单的做法是直接修改列定义，如果数据库版本支持
ALTER TABLE demands MODIFY status VARCHAR(50) NOT NULL DEFAULT 'OPEN' CHECK (status IN ('OPEN', 'HIRED', 'CLOSED', 'ON_HOLD'));


-- 2. 更新 CANDIDATES 表
-- 扩展 status 字段的 CHECK 约束以包含 'FINALIST' 和 'ON_HOLD'
-- ALTER TABLE candidates DROP CONSTRAINT candidates_chk_1;
-- ALTER TABLE candidates ADD CONSTRAINT candidates_status_check CHECK (status IN ('SCREENING', 'INTERVIEW', 'PASSED_WAITING_FOR_INTERVIEW', 'REJECTED', 'HIRED', 'FINALIST', 'ON_HOLD'));
ALTER TABLE candidates MODIFY status VARCHAR(50) NOT NULL DEFAULT 'SCREENING' CHECK (status IN ('SCREENING', 'INTERVIEW', 'PASSED_WAITING_FOR_INTERVIEW', 'REJECTED', 'HIRED', 'FINALIST', 'ON_HOLD'));

-- 添加新的可空字段以丰富候选人信息
ALTER TABLE candidates ADD COLUMN gender VARCHAR(50) NULL;
ALTER TABLE candidates ADD COLUMN skillset TEXT NULL;
ALTER TABLE candidates ADD COLUMN seniority VARCHAR(255) NULL;
ALTER TABLE candidates ADD COLUMN related_working_experience TEXT NULL;
ALTER TABLE candidates ADD COLUMN onboarding_time VARCHAR(255) NULL;
ALTER TABLE candidates ADD COLUMN skill_highlights TEXT NULL;
ALTER TABLE candidates ADD COLUMN english_capability TEXT NULL;
ALTER TABLE candidates ADD COLUMN internal_interview_feedback TEXT NULL;
ALTER TABLE candidates ADD COLUMN online_coding_result TEXT NULL;

