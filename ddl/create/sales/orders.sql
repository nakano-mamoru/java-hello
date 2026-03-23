CREATE TABLE sales.orders (
  OrderId VARCHAR(12) NOT NULL,
  CustomerId VARCHAR(10) NOT NULL,
  OrderAmount DECIMAL(10,2) NOT NULL,
  Status VARCHAR(20),
  CreatedAt DATETIME,
  PRIMARY KEY (OrderId)
);
