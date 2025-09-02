-- To ensure this script can be re-run, we must delete records in the correct order,
-- respecting foreign key constraints.

-- 1. Delete records from the tables that REFERENCE specialties
DELETE FROM user_specialties;
DELETE FROM demand_specialties;

-- 2. Now that no records reference specialties, we can safely clear the table
DELETE FROM specialties;


-- 3. Insert the updated list of specialties
INSERT INTO specialties (name) VALUES
                                   ('JAVA'),
                                   ('REACT'),
                                   ('ANGULAR'),
                                   ('QA'),
                                   ('BA'),
                                   ('FULLSTACK'),
                                   ('DEVOPS');