-- Passwords are 'password' bcrypted. The hash is: $2a$10$e.ExV01V6B.f20.a1j3o1uJ1n.VL8xW5vR5.blGcB4Y4c8nAV2.5O
INSERT INTO users (email, password, name, role) VALUES
                                                    ('hiring.manager@talentlink.com', '$2a$10$e.ExV01V6B.f20.a1j3o1uJ1n.VL8xW5vR5.blGcB4Y4c8nAV2.5O', 'Ian The HM', 'HM'),
                                                    ('interviewer.jonh@talentlink.com', '$2a$10$e.ExV01V6B.f20.a1j3o1uJ1n.VL8xW5vR5.blGcB4Y4c8nAV2.5O', 'Jonh The Interviewer', 'INTERVIEWER'),
                                                    ('interviewer.jack@talentlink.com', '$2a$10$e.ExV01V6B.f20.a1j3o1uJ1n.VL8xW5vR5.blGcB4Y4c8nAV2.5O', 'Jack The Interviewer', 'INTERVIEWER');

INSERT INTO vendors (company_name, contact_name, email, mobile) VALUES
                                                                    ('Awesome Tech Talent', 'Alice', 'alice@awesometalent.com', '11112222'),
                                                                    ('HR Solutions Inc.', 'Bob', 'bob@hrsolutions.com', '33334444');