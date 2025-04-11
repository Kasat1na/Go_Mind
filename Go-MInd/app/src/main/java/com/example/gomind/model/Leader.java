package com.example.gomind.model;

public class Leader {

    private int position;
    private String nickname;
    private int points;

    // Геттеры и сеттеры
    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    @Override
    public String toString() {
        return "UserWithPoints{" +
                "position=" + position +
                ", nickname='" + nickname + '\'' +
                ", points=" + points +
                '}';
    }
}
