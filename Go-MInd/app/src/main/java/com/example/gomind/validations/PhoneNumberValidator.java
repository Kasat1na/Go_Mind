package com.example.gomind.validations;

public class PhoneNumberValidator implements IValidator {
    @Override
    public String validate(CharSequence s) {

        String str = s.toString();

        return Helper.isNotEmptyStr(str) ?
                (Helper.validatePhoneNumber(str) ? ""
                        : "Номер телефона был введен некорректно!")
                : "Поле обязательно!";
    }
}

