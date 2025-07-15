-- Create databases for each microservice
CREATE DATABASE IF NOT EXISTS user_db;
CREATE DATABASE IF NOT EXISTS expense_db;
CREATE DATABASE IF NOT EXISTS budget_db;

-- Grant privileges
GRANT ALL PRIVILEGES ON user_db.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON expense_db.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON budget_db.* TO 'root'@'%';
FLUSH PRIVILEGES;