CREATE TABLE sales.customer (
  customer_id VARCHAR(10) NOT NULL,
  customer_name VARCHAR(100) NOT NULL,
  email VARCHAR(255) NOT NULL,
  phone_number VARCHAR(20),
  age INT,
  gender CHAR(1),
  created_at DATETIME,
  PRIMARY KEY (customer_id)
);
