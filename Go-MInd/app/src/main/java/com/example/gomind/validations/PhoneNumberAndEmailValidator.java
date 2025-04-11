package com.example.gomind.validations;

public class PhoneNumberAndEmailValidator implements IValidator {

    @Override
    public String validate(CharSequence s) {

        String str = s.toString();

        return Helper.isNotEmptyStr(str) ?
                (!Helper.validatePhoneNumber(str) ?
                        (!Helper.validate(str, Helper.VALID_EMAIL_ADDRESS_REGEX) ?
                                "Номер телефона или email был введен некорректно!"
                                : "")
                        : "")
                : "Поле обязательно!";
    }
}

