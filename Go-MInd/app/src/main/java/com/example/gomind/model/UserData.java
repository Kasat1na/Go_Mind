package com.example.gomind.model;

import com.google.gson.annotations.SerializedName;

public class UserData{

    @SerializedName("nickname")
    private String nickname;

    @SerializedName("email")
    private String email;

    @SerializedName("pears")
    private int pears;

    // Геттеры и сеттеры
    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getPears() {
        return pears;
    }

    public void setPears(int pears) {
        this.pears = pears;
    }

    @Override
    public String toString() {
        return "UserResponse{" +
                "nickname='" + nickname + '\'' +
                ", email='" + email + '\'' +
                ", pears=" + pears +
                '}';
    }
}
