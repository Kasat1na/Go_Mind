package com.example.gomind.model;
import com.google.gson.annotations.SerializedName;

public class RemainingTimeResponse {
    @SerializedName("timestamp")
    private String timestamp;

    @SerializedName("status")
    private int status;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private String data;

    // Геттеры и сеттеры
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getData() { return data; }
    public void setData(String data) { this.data = data; }
}
