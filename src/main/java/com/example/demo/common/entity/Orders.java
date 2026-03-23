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
    private String OrderId;


    /**
     * 顧客IDです。
     */
    @Size(max=10)
    private String CustomerId;


    /**
     * 注文金額です。
     */

    private BigDecimal OrderAmount;


    /**
     * 状態です。
     */
    @Size(max=20)
    private String Status;


    /**
     * 作成日時です。
     */

    private LocalDate CreatedAt;



    public String getOrderId() {
        return OrderId;
    }

    public void setOrderId(String OrderId) {
        this.OrderId = OrderId;
    }


    public String getCustomerId() {
        return CustomerId;
    }

    public void setCustomerId(String CustomerId) {
        this.CustomerId = CustomerId;
    }


    public BigDecimal getOrderAmount() {
        return OrderAmount;
    }

    public void setOrderAmount(BigDecimal OrderAmount) {
        this.OrderAmount = OrderAmount;
    }


    public String getStatus() {
        return Status;
    }

    public void setStatus(String Status) {
        this.Status = Status;
    }


    public LocalDate getCreatedAt() {
        return CreatedAt;
    }

    public void setCreatedAt(LocalDate CreatedAt) {
        this.CreatedAt = CreatedAt;
    }
}
