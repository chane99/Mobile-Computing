package com.example.breakingblock;

import org.jetbrains.annotations.NotNull;

public class UserAccount {
    private String emailId;
    private String password;
    private String idToken; //파이어베이스 고유 id
    private String displayName; // 이전에 nickname으로 사용되던 변수를 displayName으로 변경

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}