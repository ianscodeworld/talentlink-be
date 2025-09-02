-- Adds a flag to the users table to enforce password changes for new users.
ALTER TABLE users ADD COLUMN password_change_required BOOLEAN NOT NULL DEFAULT TRUE;