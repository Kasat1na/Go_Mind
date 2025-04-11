package com.example.gomind.model;

public class ApiResponse {
    private int status;
    private String message;
    private String path;
    private String method;
    private UserData data;

    private int count;

    // Геттеры и сеттеры
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public UserData getData() {
        return data;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setData(UserData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ApiResponse{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", path='" + path + '\'' +
                ", method='" + method + '\'' +
                ", data=" + data +
                ", count=" + count +
                '}';
    }
}