-- MVP 1.6.1 Schema Changes for Squad Lifecycle

-- 1. SQUADS 表: 增加 status 字段用于生命周期管理
ALTER TABLE squads ADD COLUMN status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'COMPLETED', 'ON_HOLD'));

-- 2. SQUADS 表: 增加 is_deleted 字段用于软删除
ALTER TABLE squads ADD COLUMN is_deleted BOOLEAN NOT NULL DEFAULT FALSE;