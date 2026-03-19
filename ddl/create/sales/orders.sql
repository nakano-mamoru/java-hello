CREATE TABLE sales.orders (
  order_id VARCHAR(12) NOT NULL,
  customer_id VARCHAR(10) NOT NULL,
  order_amount DECIMAL(10,2) NOT NULL,
  status VARCHAR(20),
  created_at DATETIME,
  PRIMARY KEY (order_id)
);
