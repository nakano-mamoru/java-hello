package com.example.demo.security;

public class UserMaster {
    private String userId;
    private String userName;
    private String password;
    private String role;
    private String userCreatedAt;
    private String userUpdatedAt;

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }
    public String getUserCreatedAt() {
        return userCreatedAt;
    }
    public void setUserCreatedAt(String userCreatedAt) {
        this.userCreatedAt = userCreatedAt;
    }
    public String getUserUpdatedAt() {
        return userUpdatedAt;
    }
    public void setUserUpdatedAt(String userUpdatedAt) {
        this.userUpdatedAt = userUpdatedAt;
    }
}
