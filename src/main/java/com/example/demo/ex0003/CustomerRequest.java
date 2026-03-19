package com.example.demo.ex0003;

import com.example.demo.validation.Item;

public class CustomerRequest {

    @Item("CustomerId")
    private String customerId;

    @Item("CustomerName")
    private String customerName;

    @Item("Email")
    private String email;

    @Item("Age")
    private String age;

    // getters/setters
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAge() { return age; }
    public void setAge(String age) { this.age = age; }
}