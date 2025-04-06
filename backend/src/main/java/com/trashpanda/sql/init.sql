-- DROP TABLE IF EXISTS usercommunities;
-- DROP TABLE IF EXISTS communities;
DROP TABLE IF EXISTS sharelist;
DROP TABLE IF EXISTS wantlist;
DROP TABLE IF EXISTS profiles;

CREATE TABLE profiles
(
    username  VARCHAR(100) PRIMARY KEY,
    firstname VARCHAR(100) NOT NULL,
    lastname  VARCHAR(100) NOT NULL,
    longitude NUMERIC,
    latitude  NUMERIC,
    password  VARCHAR(100) NOT NULL,
    radius    INTEGER,
    contact   VARCHAR(100) NOT NULL
);

-- Insert demo users that will produce matches
INSERT INTO profiles (username, password, firstname, lastname, longitude, latitude, radius, contact)
VALUES
    ('piyusha', 'password123', 'Piyusha', 'User', -123.1207, 49.2827, 10, 'demo@example.com'),
    ('avi', 'password123', 'Avi', 'One', -123.1217, 49.2837, 15, 'avi@example.com'),
    ('harper', 'password123', 'Harper', 'Two', -123.1227, 49.2847, 8, 'harper@example.com'),
    ('charity', 'password123', 'Charity', 'Away', -120.0000, 45.0000, 10, 'charity@example.com'),
    ('christine', 'meow1234', 'Christine', 'Doe', -123.1207, 49.2827, 10, 'christine@example.com');

CREATE TABLE sharelist
(
    username       VARCHAR(100),
    ingredient     VARCHAR(100),
    quantity       NUMERIC NOT NULL,
    units          VARCHAR(50) NOT NULL,
    expiration_date DATE,
    PRIMARY KEY (username, ingredient),
    CONSTRAINT fk_share_user FOREIGN KEY (username) REFERENCES profiles(username) ON DELETE CASCADE
);

-- Create share list entries for piyusha
INSERT INTO sharelist (username, ingredient, quantity, units, expiration_date)
VALUES
    ('piyusha', 'tomatoes', 5, 'UNIT', '2025-05-10'),
    ('piyusha', 'rice', 2, 'KG', '2025-08-15'),
    ('piyusha', 'pasta', 500, 'G', '2025-07-20'),
    
    -- Create share list entries for avi that match piyusha's wants
    ('avi', 'onions', 6, 'UNIT', '2025-05-15'),
    ('avi', 'garlic', 10, 'UNIT', '2025-06-01'),
    ('avi', 'flour', 1, 'KG', '2025-07-10'),
    
    -- Create share list entries for harper that match piyusha's wants
    ('harper', 'olive oil', 500, 'ML', '2025-08-20'),
    ('harper', 'salt', 250, 'G', '2026-01-15'),
    
    -- Create entries for charity user (should not match due to distance)
    ('charity', 'garlic', 5, 'UNIT', '2025-05-30'),
    
    -- Original data for Christine
    ('christine', 'sugar', 1, 'TSP', '2025-06-01'),
    ('christine', 'spinach', 4, 'CUP', '2025-05-15'),
    ('christine', 'rice', 500, 'G', '2025-07-01'),
    ('christine', 'olive oil', 1, 'L', '2025-08-10'),
    ('christine', 'bread', 8, 'SLICES', '2025-04-25');

CREATE TABLE wantlist
(
    username   VARCHAR(100),
    ingredient VARCHAR(100),
    quantity   NUMERIC NOT NULL,
    units      VARCHAR(50) NOT NULL,
    PRIMARY KEY (username, ingredient),
    CONSTRAINT fk_share_user FOREIGN KEY (username) REFERENCES profiles(username) ON DELETE CASCADE
);

INSERT INTO wantlist (username, ingredient, quantity, units)
VALUES
    -- Create want list entries for piyusha
    ('piyusha', 'onions', 3, 'UNIT'),
    ('piyusha', 'garlic', 2, 'UNIT'),
    ('piyusha', 'olive oil', 250, 'ML'),
    
    -- Create want list entries for avi that match piyusha's shares
    ('avi', 'tomatoes', 3, 'UNIT'),
    ('avi', 'pasta', 200, 'G'),
    
    -- Create want list entries for harper that match piyusha's shares
    ('harper', 'rice', 1, 'KG'),
    
    -- Want list for charity user
    ('charity', 'rice', 2, 'KG'),
    
    -- Original data 
    ('christine', 'sugar', 1, 'TSP'),
    ('avi', 'spinach', 4, 'CUP'),
    ('harper', 'olive oil', 1, 'L'),
    ('christine', 'bread', 8, 'SLICES');

-- CREATE TABLE communities
-- (
--     communityname VARCHAR(100) PRIMARY KEY,
--     description TEXT
-- );
--
-- INSERT INTO communities (communityname, description)
-- VALUES
--     ('UBC Garden Club', 'A community for gardening enthusiasts to share fresh produce.'),
--     ('test2', 'description2'),
--     ('test3', 'description3'),
--     ('test4', 'description4'),
--     ('test5', 'description5');
--
--
-- CREATE TABLE usercommunities
-- (
--     communityname VARCHAR(100),
--     username      VARCHAR(100),
--     PRIMARY KEY (communityname, username),
--     CONSTRAINT fk_uc_community FOREIGN KEY (communityname) REFERENCES communities(communityname) ON DELETE CASCADE,
--     CONSTRAINT fk_uc_user FOREIGN KEY (username) REFERENCES profiles(username) ON DELETE CASCADE
-- );
--
-- INSERT INTO usercommunities (communityname, username)
-- VALUES
--     ('UBC Garden Club', 'christine'),
--     ('UBC Garden Club', 'avi'),
--     ('test2', 'piyusha'),
--     ('test