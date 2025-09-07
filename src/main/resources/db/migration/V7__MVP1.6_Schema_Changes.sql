-- MVP 1.6 Schema Changes for TalentLink

-- 1. USERS 表: 增加软删除标志
ALTER TABLE users ADD COLUMN is_active BOOLEAN NOT NULL DEFAULT TRUE;

-- 2. CANDIDATES 表: 字段变更
-- 首先，我们需要删除指向 vendors 表的外键约束
-- 注意: 这个约束的名称可能因数据库自动生成而不同。您可能需要通过 'SHOW CREATE TABLE candidates;' 来确认它的确切名称。
ALTER TABLE candidates DROP FOREIGN KEY candidates_ibfk_2;

-- 然后，删除旧的 vendor_id 列
ALTER TABLE candidates DROP COLUMN vendor_id;

-- 接着，添加新的 vendor_name 字符串列
ALTER TABLE candidates ADD COLUMN vendor_name VARCHAR(255) NULL AFTER resume_summary;

-- 最后，为候选人增加一个可覆盖面试总轮数的字段
ALTER TABLE candidates ADD COLUMN total_rounds_override INT NULL;


-- 3. SQUADS 表: 创建一个全新的表来组织需求
CREATE TABLE squads (
                        id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(255) NOT NULL UNIQUE,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 4. DEMANDS 表: 增加与 SQUAD 表的关联外键
ALTER TABLE demands ADD COLUMN squad_id BIGINT NULL;
ALTER TABLE demands ADD CONSTRAINT fk_demands_squads FOREIGN KEY (squad_id) REFERENCES squads(id);