-- V1__Initial_schema.sql (MariaDB Compatible)

CREATE TABLE users (
                       id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       name VARCHAR(255),
                       role VARCHAR(50) NOT NULL CHECK (role IN ('HM', 'INTERVIEWER')),
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE vendors (
                         id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                         company_name VARCHAR(255) NOT NULL UNIQUE,
                         contact_name VARCHAR(255),
                         email VARCHAR(255),
                         mobile VARCHAR(50)
);

CREATE TABLE demands (
                         id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                         job_title VARCHAR(255) NOT NULL,
                         description TEXT,
                         total_interview_rounds INT NOT NULL DEFAULT 3,
                         status VARCHAR(50) NOT NULL DEFAULT 'OPEN' CHECK (status IN ('OPEN', 'CLOSED', 'ON_HOLD')),
                         created_by_id BIGINT NOT NULL,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                         FOREIGN KEY (created_by_id) REFERENCES users(id)
);

CREATE TABLE candidates (
                            id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                            name VARCHAR(255) NOT NULL,
                            resume_summary TEXT,
                            current_interview_round INT NOT NULL DEFAULT 1,
                            status VARCHAR(50) NOT NULL DEFAULT 'SCREENING' CHECK (status IN ('SCREENING', 'INTERVIEW', 'PASSED_WAITING_FOR_INTERVIEW', 'REJECTED', 'HIRED')),
                            demand_id BIGINT NOT NULL,
                            vendor_id BIGINT NOT NULL,
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                            FOREIGN KEY (demand_id) REFERENCES demands(id),
                            FOREIGN KEY (vendor_id) REFERENCES vendors(id)
);

CREATE TABLE feedbacks (
                           id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                           evaluation_text TEXT NOT NULL,
                           recommendation VARCHAR(50) NOT NULL CHECK (recommendation IN ('PASS', 'FAIL')),
                           interview_round INT NOT NULL,
                           candidate_id BIGINT NOT NULL,
                           interviewer_id BIGINT NOT NULL,
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           UNIQUE KEY uq_feedback (candidate_id, interviewer_id, interview_round),
                           FOREIGN KEY (candidate_id) REFERENCES candidates(id),
                           FOREIGN KEY (interviewer_id) REFERENCES users(id)
);

CREATE TABLE interview_assignments (
                                       id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                       candidate_id BIGINT NOT NULL,
                                       interviewer_id BIGINT NOT NULL,
                                       interview_round INT NOT NULL,
                                       is_completed BOOLEAN DEFAULT FALSE,
                                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                       UNIQUE KEY uq_assignment (candidate_id, interview_round),
                                       FOREIGN KEY (candidate_id) REFERENCES candidates(id),
                                       FOREIGN KEY (interviewer_id) REFERENCES users(id)
);

CREATE TABLE candidate_history (
                                   id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                   action_type VARCHAR(50) NOT NULL,
                                   details TEXT,
                                   candidate_id BIGINT NOT NULL,
                                   created_by_id BIGINT NOT NULL,
                                   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                   FOREIGN KEY (candidate_id) REFERENCES candidates(id),
                                   FOREIGN KEY (created_by_id) REFERENCES users(id)
);