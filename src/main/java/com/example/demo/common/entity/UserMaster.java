package com.example.demo.common.entity;

import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/**
 * ユーザマスタのエンティティクラスです。
 */
public class UserMaster {



    /**
     * ユーザIDです。
     */
    @Size(max=50)
    private String UserId;


    /**
     * ユーザ名です。
     */
    @Size(max=100)
    private String UserName;


    /**
     * パスワードです。
     */
    @Size(max=255)
    private String Password;


    /**
     * 権限です。
     */
    @Size(max=50)
    private String Role;


    /**
     * ユーザ作成日時です。
     */

    private LocalDate UserCreatedAt;


    /**
     * ユーザ更新日時です。
     */

    private LocalDate UserUpdatedAt;



    public String getUserId() {
        return UserId;
    }

    public void setUserId(String UserId) {
        this.UserId = UserId;
    }


    public String getUserName() {
        return UserName;
    }

    public void setUserName(String UserName) {
        this.UserName = UserName;
    }


    public String getPassword() {
        return Password;
    }

    public void setPassword(String Password) {
        this.Password = Password;
    }


    public String getRole() {
        return Role;
    }

    public void setRole(String Role) {
        this.Role = Role;
    }


    public LocalDate getUserCreatedAt() {
        return UserCreatedAt;
    }

    public void setUserCreatedAt(LocalDate UserCreatedAt) {
        this.UserCreatedAt = UserCreatedAt;
    }


    public LocalDate getUserUpdatedAt() {
        return UserUpdatedAt;
    }

    public void setUserUpdatedAt(LocalDate UserUpdatedAt) {
        this.UserUpdatedAt = UserUpdatedAt;
    }
}
