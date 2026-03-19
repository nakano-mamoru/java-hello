package com.example.demo.common.entity;

import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/**
 * 顧客マスタのエンティティクラスです。
 */
public class Customer {



    /**
     * 顧客IDです。
     */
    @Size(max=10)
    private String customerId;


    /**
     * 顧客名です。
     */
    @Size(max=100)
    private String customerName;


    /**
     * メールアドレスです。
     */
    @Size(max=255)
    private String email;


    /**
     * 電話番号です。
     */
    @Size(max=20)
    private String phoneNumber;


    /**
     * 年齢です。
     */

    private Integer age;


    /**
     * 性別です。
     */
    @Size(max=1)
    private String gender;


    /**
     * 作成日時です。
     */

    private LocalDate createdAt;



    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }


    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }


    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }


    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }


    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }
}
