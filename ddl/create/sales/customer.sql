CREATE TABLE sales.customer (
  CustomerId VARCHAR(10) NOT NULL,
  CustomerName VARCHAR(100) NOT NULL,
  Email VARCHAR(255) NOT NULL,
  PhoneNumber VARCHAR(20),
  Age INT,
  Gender CHAR(1),
  CreatedAt DATETIME,
  PRIMARY KEY (CustomerId)
);
