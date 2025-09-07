-- MVP 1.7 Schema Changes for Maturity & Intelligence

-- 1. DEMANDS 表: 增加软删除标志和招聘 headcount
ALTER TABLE demands ADD COLUMN is_deleted BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE demands ADD COLUMN required_positions INT NOT NULL DEFAULT 1;

-- 2. CANDIDATES 表: 更新 status 的 CHECK 约束以包含 'WITHDRAWN'
-- 注意: 此处直接修改列定义以更新约束
ALTER TABLE candidates MODIFY status VARCHAR(50) NOT NULL DEFAULT 'SCREENING'
    CHECK (status IN (
    'SCREENING', 'INTERVIEW', 'PASSED_WAITING_FOR_INTERVIEW',
    'REJECTED', 'HIRED', 'FINALIST', 'ON_HOLD', 'WITHDRAWN'
    ));

-- 3. NOTIFICATIONS 表: 创建新表用于实时通知
CREATE TABLE notifications (
                               id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                               recipient_id BIGINT NOT NULL,
                               content TEXT NOT NULL,
                               link_url VARCHAR(255),
                               is_read BOOLEAN NOT NULL DEFAULT FALSE,
                               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               FOREIGN KEY (recipient_id) REFERENCES users(id)
);