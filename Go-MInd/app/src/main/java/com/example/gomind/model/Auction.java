package com.example.gomind.model;

public class Auction {
    private int position;
    private int cost;
    private String nickname;
    private int fileDataId; // Добавляем поле для fileDataId

    // Геттеры и сеттеры
    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getFileDataId() {
        return fileDataId;
    }

    public void setFileDataId(int fileDataId) {
        this.fileDataId = fileDataId;
    }
}
