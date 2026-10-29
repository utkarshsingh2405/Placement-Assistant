-- Placement Assistant MySQL Initialization Script
-- Creates dedicated databases for Portal, Institute, and Placement services

CREATE DATABASE IF NOT EXISTS placement_portal CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS placement_institute CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS placement_placement CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Grant permissions to user
GRANT ALL PRIVILEGES ON placement_portal.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON placement_institute.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON placement_placement.* TO 'root'@'%';

FLUSH PRIVILEGES;
