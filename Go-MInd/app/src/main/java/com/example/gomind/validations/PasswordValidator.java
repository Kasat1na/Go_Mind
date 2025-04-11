package com.example.gomind.validations;


public class PasswordValidator {
    // Метод для проверки валидности пароля
    public static boolean isValidPassword(String password) {
        // Проверка на пустое значение и длину пароля
        return password != null && !password.isEmpty() && password.length() >= 6 && password.length() <= 20;
    }
}
