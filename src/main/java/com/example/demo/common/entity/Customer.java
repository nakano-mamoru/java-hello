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
    private String CustomerId;


    /**
     * 顧客名です。
     */
    @Size(max=100)
    private String CustomerName;


    /**
     * メールアドレスです。
     */
    @Size(max=255)
    private String Email;


    /**
     * 電話番号です。
     */
    @Size(max=20)
    private String PhoneNumber;


    /**
     * 年齢です。
     */

    private Integer Age;


    /**
     * 性別です。
     */
    @Size(max=1)
    private String Gender;


    /**
     * 作成日時です。
     */

    private LocalDate CreatedAt;



    public String getCustomerId() {
        return CustomerId;
    }

    public void setCustomerId(String CustomerId) {
        this.CustomerId = CustomerId;
    }


    public String getCustomerName() {
        return CustomerName;
    }

    public void setCustomerName(String CustomerName) {
        this.CustomerName = CustomerName;
    }


    public String getEmail() {
        return Email;
    }

    public void setEmail(String Email) {
        this.Email = Email;
    }


    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String PhoneNumber) {
        this.PhoneNumber = PhoneNumber;
    }


    public Integer getAge() {
        return Age;
    }

    public void setAge(Integer Age) {
        this.Age = Age;
    }


    public String getGender() {
        return Gender;
    }

    public void setGender(String Gender) {
        this.Gender = Gender;
    }


    public LocalDate getCreatedAt() {
        return CreatedAt;
    }

    public void setCreatedAt(LocalDate CreatedAt) {
        this.CreatedAt = CreatedAt;
    }
}
