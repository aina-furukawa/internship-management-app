-- ログイン機能追加用（MySQL）
-- 実行: mysql -u root -p internship_db < scripts/login-migration.sql

USE internship_db;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL
);

-- user_id 列がなければ追加
SET @col_exists = (
    SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = 'internship_db'
      AND TABLE_NAME = 'applications'
      AND COLUMN_NAME = 'user_id'
);

SET @sql = IF(
    @col_exists = 0,
    'ALTER TABLE applications ADD COLUMN user_id BIGINT NULL',
    'SELECT ''user_id column already exists'' AS message'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- ログイン前の旧データ（user_id 未設定）は表示対象外のため削除
DELETE FROM applications WHERE user_id IS NULL;

ALTER TABLE applications MODIFY COLUMN user_id BIGINT NOT NULL;
