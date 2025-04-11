package com.example.gomind.validations;

import android.util.Patterns;

public class EmailValidator {
    // Метод для проверки валидности email
    public static boolean isValidEmail(String email) {
        // Проверка на пустое значение и формат email
        return email != null && !email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}

