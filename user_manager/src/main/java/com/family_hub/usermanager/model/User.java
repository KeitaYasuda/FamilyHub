package com.familyhub.usermanager.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {

    @Id
    private String username;  // ユニークなユーザID

    private String passwordHash;

    private String email;

    // シンプルな実装例として、カンマ区切りのロール文字列
    private String roles;

    // コンストラクタ、getter/setter を追加
    public User() {}

    public User(String username, String passwordHash, String email, String roles) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
        this.roles = roles;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPasswordHash() {
        return passwordHash;
    }
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getRoles() {
        return roles;
    }
    public void setRoles(String roles) {
        this.roles = roles;
    }
}
