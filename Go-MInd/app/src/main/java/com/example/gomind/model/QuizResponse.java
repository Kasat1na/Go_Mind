package com.example.gomind.model;

import com.google.gson.annotations.SerializedName;

public class QuizResponse {
    @SerializedName("timestamp")
    private String timestamp;

    @SerializedName("status")
    private int status;

    @SerializedName("message")
    private String message;

    @SerializedName("path")
    private String path;

    @SerializedName("method")
    private String method;

    @SerializedName("processingTime")
    private String processingTime;

    @SerializedName("user")
    private String user;

    @SerializedName("data")
    private Question data;

    @SerializedName("errors")
    private Object errors;

    @SerializedName("count")
    private int count;

    // Геттеры и сеттеры для всех полей

    public static class Question {
        @SerializedName("id")
        private int id;

        @SerializedName("text")
        private String text;

        @SerializedName("optionA")
        private String optionA;

        @SerializedName("optionB")
        private String optionB;

        @SerializedName("optionC")
        private String optionC;

        @SerializedName("optionD")
        private String optionD;

        @SerializedName("correctAnswer")
        private String correctAnswer;

        // Геттеры и сеттеры для всех полей

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getOptionA() {
            return optionA;
        }

        public void setOptionA(String optionA) {
            this.optionA = optionA;
        }

        public String getOptionB() {
            return optionB;
        }

        public void setOptionB(String optionB) {
            this.optionB = optionB;
        }

        public String getOptionC() {
            return optionC;
        }

        public void setOptionC(String optionC) {
            this.optionC = optionC;
        }

        public String getOptionD() {
            return optionD;
        }

        public void setOptionD(String optionD) {
            this.optionD = optionD;
        }

        public String getCorrectAnswer() {
            return correctAnswer;
        }

        public void setCorrectAnswer(String correctAnswer) {
            this.correctAnswer = correctAnswer;
        }
    }

    public Question getData() {
        return data;
    }

    public void setData(Question data) {
        this.data = data;
    }
}
