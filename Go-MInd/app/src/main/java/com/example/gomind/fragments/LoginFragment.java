package com.example.gomind.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gomind.R;
import com.example.gomind.SharedPrefManager;
import com.example.gomind.model.Token;
import com.example.gomind.activities.MainActivity;
import com.example.gomind.api.RetrofitClient;
import com.example.gomind.sound.SoundManager;
import com.example.gomind.validations.EmailValidator;
import com.example.gomind.validations.PasswordValidator;
import com.google.android.material.button.MaterialButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import okhttp3.Cookie;
import okhttp3.HttpUrl;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment {

    private EditText edtPassword, edtEmail;
    private MaterialButton enterBtn;
    private ImageView passwordToggleEye, passwordToggleEyeOpen;
    private TextView emailErrorText, passwordErrorText;
    private SoundManager soundManager;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_fragment, container, false);

        edtPassword = view.findViewById(R.id.edt_password);
        edtEmail = view.findViewById(R.id.edt_email);
        enterBtn = view.findViewById(R.id.enter_btn);
        passwordToggleEye = view.findViewById(R.id.password_toggle_eye);
        passwordToggleEyeOpen = view.findViewById(R.id.password_toggle_eye_open);
        emailErrorText = view.findViewById(R.id.email_error);
        passwordErrorText = view.findViewById(R.id.password_error);


        // Инициализация SoundManager
        soundManager = SoundManager.getInstance(getContext());

        enterBtn.setOnClickListener(v -> login());
        enterBtn.setEnabled(false); // Отключаем кнопку входа по умолчанию

        // Устанавливаем скрытый ввод пароля
        edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        // Переключение видимости пароля
        passwordToggleEye.setOnClickListener(v -> togglePasswordVisibility(true));
        passwordToggleEyeOpen.setOnClickListener(v -> togglePasswordVisibility(false));

        // Добавляем валидацию в реальном времени
        edtEmail.addTextChangedListener(emailTextWatcher);
        edtPassword.addTextChangedListener(passwordTextWatcher);

        return view;
    }

    private void togglePasswordVisibility(boolean isPasswordHidden) {
        if (isPasswordHidden) {
            passwordToggleEye.setVisibility(View.GONE);
            passwordToggleEyeOpen.setVisibility(View.VISIBLE);
            edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        } else {
            passwordToggleEye.setVisibility(View.VISIBLE);
            passwordToggleEyeOpen.setVisibility(View.GONE);
            edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
        edtPassword.setSelection(edtPassword.length());
    }

    private final TextWatcher emailTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            validateEmail();
            updateLoginButtonState();
        }

        @Override
        public void afterTextChanged(Editable s) {}
    };

    private final TextWatcher passwordTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            validatePassword();
            updateLoginButtonState();
        }

        @Override
        public void afterTextChanged(Editable s) {}
    };

    private void validateEmail() {
        String email = edtEmail.getText().toString().trim();
        if (email.isEmpty()) {
            emailErrorText.setText("Email обязателен для заполнения");
            emailErrorText.setVisibility(View.VISIBLE);
        } else if (!EmailValidator.isValidEmail(email)) {
            emailErrorText.setText("Введите корректный email");
            emailErrorText.setVisibility(View.VISIBLE);
        } else {
            emailErrorText.setVisibility(View.GONE);
        }
    }

    private void validatePassword() {
        String password = edtPassword.getText().toString().trim();
        if (password.isEmpty()) {
            passwordErrorText.setText("Пароль обязателен для заполнения");
            passwordErrorText.setVisibility(View.VISIBLE);
        } else if (!PasswordValidator.isValidPassword(password)) {
            passwordErrorText.setText("Пароль должен содержать от 6 до 20 символов");
            passwordErrorText.setVisibility(View.VISIBLE);
        } else {
            passwordErrorText.setVisibility(View.GONE);
        }
    }

    private void updateLoginButtonState() {
        boolean isEmailValid = EmailValidator.isValidEmail(edtEmail.getText().toString().trim());
        boolean isPasswordValid = PasswordValidator.isValidPassword(edtPassword.getText().toString().trim());

        enterBtn.setEnabled(isEmailValid && isPasswordValid);
    }


    private void login() {
        soundManager.playSound();
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (!EmailValidator.isValidEmail(email) || !PasswordValidator.isValidPassword(password)) {
            return;
        }

        try {
            JSONObject paramObject = new JSONObject();
            paramObject.put("email", email);
            paramObject.put("password", password);

            Call<ResponseBody> call = RetrofitClient.getInstance(getActivity()).getAuthApi()
                    .login(paramObject.toString());

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    if (response.code() == 200) {
                        Map<HttpUrl, List<Cookie>> cookieStore = RetrofitClient.cookieManager.getCookieStore();
                        HttpUrl url = HttpUrl.parse("https://www.gwork.press:8443/authentication/login");

                        if (cookieStore.containsKey(url)) {
                            List<Cookie> cookies = cookieStore.get(url);
                            String jwtCookie = null, refreshJwtCookie = null;

                            for (Cookie cookie : cookies) {
                                if ("jwt-cookie".equals(cookie.name())) {
                                    jwtCookie = cookie.value();
                                } else if ("refresh-jwt-cookie".equals(cookie.name())) {
                                    refreshJwtCookie = cookie.value();
                                }
                            }

                            SharedPrefManager.getInstance(getActivity()).saveToken(new Token(jwtCookie, refreshJwtCookie));
                        }

                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getActivity(), "Неверный email или пароль", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    Toast.makeText(getActivity(), "Ошибка подключения", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        soundManager.release();
    }

}
