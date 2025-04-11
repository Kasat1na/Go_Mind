package com.example.gomind.validations;

public class CapitalLetterValidator implements IValidator {
    @Override
    public String validate(CharSequence s) {
        String str = s.toString();

        return Helper.isNotEmptyStr(str) ?
                (!Helper.validate(str, Helper.VALID_CAPITAL_LETTER_REGEX) ?
                        "Только кирилица, с заглавной буквы!"
                        : "")
                : "Поле обязательно!";
    }
}

