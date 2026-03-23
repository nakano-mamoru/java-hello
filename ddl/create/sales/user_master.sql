CREATE TABLE sales.user_master (
  UserId VARCHAR(50) NOT NULL,
  UserName VARCHAR(100) NOT NULL,
  Password VARCHAR(255) NOT NULL,
  Role VARCHAR(50) NOT NULL,
  UserCreatedAt DATETIME,
  UserUpdatedAt DATETIME,
  PRIMARY KEY (UserId)
);
