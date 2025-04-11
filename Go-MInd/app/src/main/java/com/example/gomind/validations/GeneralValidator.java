package com.example.gomind.validations;

import android.text.Editable;
import android.text.TextWatcher;

import com.google.android.material.textfield.TextInputLayout;

public class GeneralValidator {

    private Boolean state;
    IValidator iValidator;

    public Boolean getState() {
        return state;
    }

    public GeneralValidator(IValidator iValidator) {
        this.state = false;
        this.iValidator = iValidator;
    }

    // TextWatcher для поля "Введите пароль"
    public TextWatcher validate(TextInputLayout layout) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                layout.setHint("");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String error  = iValidator.validate(s.toString().trim());
                state = error.length() == 0;
                layout.setHelperText(error);
            }

            @Override
            public void afterTextChanged(Editable s) {
                layout.setHint("");
            }
        };
    }
}

