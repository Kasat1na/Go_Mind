package com.example.gomind.validations;

public class Helper {

    public static boolean isNotEmptyStr(String str) {
        return str != null && !str.trim().isEmpty();
    }

    public static boolean validate(String str, String regex) {
        return str.matches(regex);
    }

    public static boolean validatePhoneNumber(String phone) {
        // Реализовать валидацию номера телефона
        return phone.matches("^(\\+?[1-9]{1,4}[\\s-])?\\(?\\d{1,4}\\)?[\\s-]?\\d{1,4}[\\s-]?\\d{1,4}$");
    }

    public static final String VALID_EMAIL_ADDRESS_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";
    public static final String VALID_PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{8,}$";
    public static final String VALID_CAPITAL_LETTER_REGEX = "^[A-Z][a-zA-Z]*$";
}

