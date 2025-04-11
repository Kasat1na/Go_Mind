package com.example.gomind.model;

import com.google.gson.annotations.SerializedName;

public class TimeResponse {

    @SerializedName("message")
    private String message;

    @SerializedName("secondsLeft")
    private int secondsLeft;

    @SerializedName("minutesLeft")
    private int minutesLeft;

    // Пустой конструктор для десериализации


    // Геттеры и сеттеры
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getSecondsLeft() {
        return secondsLeft;
    }

    public void setSecondsLeft(int secondsLeft) {
        this.secondsLeft = secondsLeft;
    }

    public int getMinutesLeft() {
        return minutesLeft;
    }

    public void setMinutesLeft(int minutesLeft) {
        this.minutesLeft = minutesLeft;
    }

    @Override
    public String toString() {
        return "QuizResponse{" +
                "message='" + message + '\'' +
                ", secondsLeft=" + secondsLeft +
                ", minutesLeft=" + minutesLeft +
                '}';
    }
}