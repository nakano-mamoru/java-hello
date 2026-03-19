package com.example.demo.common.entity;

import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 注文マスタのエンティティクラスです。
 */
public class Orders {



    /**
     * 注文IDです。
     */
    @Size(max=12)
    private String orderId;


    /**
     * 顧客IDです。
     */
    @Size(max=10)
    private String customerId;


    /**
     * 注文金額です。
     */

    private BigDecimal orderAmount;


    /**
     * 状態です。
     */
    @Size(max=20)
    private String status;


    /**
     * 作成日時です。
     */

    private LocalDate createdAt;



    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }


    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }


    public BigDecimal getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(BigDecimal orderAmount) {
        this.orderAmount = orderAmount;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }
}
