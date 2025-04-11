package com.example.gomind.model;

public class User {
    //long userId;
    private String email;
    private String nickname;
    private int pears;
    private int count;
    Token token;
    public User(String email, String nickname, int pears, int count, Token token) {
        this.email = email;
        this.nickname = nickname;
        this.pears = pears;
        this.count = count;
        this.token = token;
    }
//    public long getUserId() {
//        return userId;
//    }
public Token getToken() {
    return token;
}
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getPears() {
        return pears;
    }

    public void setPears(int pears) {
        this.pears = pears;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", nickname='" + nickname + '\'' +
                ", pears=" + pears +
                ", count=" + count +
                ", token=" + token +
                '}';
    }
}
