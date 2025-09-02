-- Create specialties table
CREATE TABLE specialties (
                             id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                             name VARCHAR(100) NOT NULL UNIQUE
);

-- Create join table for users and specialties (Many-to-Many)
CREATE TABLE user_specialties (
                                  user_id BIGINT NOT NULL,
                                  specialty_id BIGINT NOT NULL,
                                  PRIMARY KEY (user_id, specialty_id),
                                  FOREIGN KEY (user_id) REFERENCES users(id),
                                  FOREIGN KEY (specialty_id) REFERENCES specialties(id)
);

-- Create join table for demands and specialties (Many-to-Many)
CREATE TABLE demand_specialties (
                                    demand_id BIGINT NOT NULL,
                                    specialty_id BIGINT NOT NULL,
                                    PRIMARY KEY (demand_id, specialty_id),
                                    FOREIGN KEY (demand_id) REFERENCES demands(id),
                                    FOREIGN KEY (specialty_id) REFERENCES specialties(id)
);