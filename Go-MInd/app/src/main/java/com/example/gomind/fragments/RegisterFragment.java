package com.example.gomind.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.gomind.R;
import com.example.gomind.api.RetrofitClient;
import com.example.gomind.sound.SoundManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterFragment extends Fragment {

    EditText edtNickname, edtEmail, edtPassword;
    TextView nicknameError, emailError, passwordError;
    Button btnRegister;
    private ImageView passwordToggleEye, passwordToggleEyeOpen;

    // Флаги, которые отслеживают, вводил ли пользователь данные
    private boolean nicknameEdited = false;
    private boolean emailEdited = false;
    private boolean passwordEdited = false;
    private SoundManager soundManager;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.registration_fragment, container, false);
        SoundManager soundManager = SoundManager.getInstance(getActivity());
        edtNickname = view.findViewById(R.id.edt_nickname);
        edtEmail = view.findViewById(R.id.edt_email);
        edtPassword = view.findViewById(R.id.edt_password);
        btnRegister = view.findViewById(R.id.register_btn);
        nicknameError = view.findViewById(R.id.nickname_error);
        emailError = view.findViewById(R.id.email_error);
        passwordError = view.findViewById(R.id.password_error);
        passwordToggleEye = view.findViewById(R.id.password_toggle_eye);
        passwordToggleEyeOpen = view.findViewById(R.id.password_toggle_eye_open);

        // Сделаем кнопку всегда кликабельной
        btnRegister.setEnabled(true);

        // Устанавливаем скрытие пароля по умолчанию
        edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        passwordToggleEye.setVisibility(View.VISIBLE);
        passwordToggleEyeOpen.setVisibility(View.GONE);

        // Добавляем обработку ввода текста для полей
        edtNickname.addTextChangedListener(new CustomTextWatcher(() -> {
            nicknameEdited = true;
            validateFields();
        }));

        edtEmail.addTextChangedListener(new CustomTextWatcher(() -> {
            emailEdited = true;
            validateFields();
        }));

        edtPassword.addTextChangedListener(new CustomTextWatcher(() -> {
            passwordEdited = true;
            validateFields();
        }));

        btnRegister.setOnClickListener(v -> {
            soundManager.playSound(); // Воспроизводим звук
            register();
        });

        // Обработчики кликов для переключения видимости пароля
        passwordToggleEye.setOnClickListener(v -> togglePasswordVisibility(true));
        passwordToggleEyeOpen.setOnClickListener(v -> togglePasswordVisibility(false));

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
        edtPassword.setSelection(edtPassword.length()); // Устанавливаем курсор в конец
    }

    private void validateFields() {
        String nickname = edtNickname.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        boolean isValid = true;

        // Валидация никнейма
        if (nicknameEdited) {
            if (nickname.isEmpty() || nickname.length() < 3) {
                nicknameError.setText("Никнейм должен содержать минимум 3 символа");
                nicknameError.setVisibility(View.VISIBLE);
                isValid = false;
            } else {
                nicknameError.setVisibility(View.GONE);
            }
        }

        // Валидация email
        if (emailEdited) {
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailError.setText("Введите корректный email");
                emailError.setVisibility(View.VISIBLE);
                isValid = false;
            } else {
                emailError.setVisibility(View.GONE);
            }
        }

        // Валидация пароля
        if (passwordEdited) {
            if (password.length() < 6) {
                passwordError.setText("Пароль должен содержать минимум 6 символов");
                passwordError.setVisibility(View.VISIBLE);
                isValid = false;
            } else {
                passwordError.setVisibility(View.GONE);
            }
        }

        btnRegister.setEnabled(isValid);
    }

    private void register() {
        RequestBody bodyString;

        JSONObject params = new JSONObject();
        try {
            params.put("nickname", Objects.requireNonNull(edtNickname.getText()).toString());
            params.put("email", Objects.requireNonNull(edtEmail.getText()).toString());
            params.put("password", Objects.requireNonNull(edtPassword.getText()).toString());

            bodyString = RequestBody.create(MediaType.parse("application/json"),
                    params.toString());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        Call<ResponseBody> call = RetrofitClient.getInstance(getActivity()).getAuthApi()
                .register(bodyString);

        Log.d("Запрос: ", call.toString());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    Log.d("Ответ: "," " + response.code());
                    if (response.code() == 200 || response.code() == 201) {
                        assert response.body() != null;
                        JSONObject obj = new JSONObject(response.body().string());

                        // если пользователь ввел корректные данные
                        if (obj.getString("status").equals("success")) {

                            // пускаем подтвердить почту
                            Bundle bundle = new Bundle();
                            bundle.putString("param", "register");
                            bundle.putString("login", params.getString("email"));
                            bundle.putString("password", params.getString("password"));

                            Toast.makeText(getActivity(), "Мы отпраили ссылку на Вашу почту", Toast.LENGTH_LONG).show();
                            btnRegister.setEnabled(false);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("Ответ", e.toString());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                btnRegister.setEnabled(true);
            }
        });

        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        Fragment fragment = new LoginFragment();

        transaction.replace(R.id.authentication_container, fragment);

        // Добавляем транзакцию в backstack, чтобы пользователь мог вернуться к предыдущему фрагменту
        transaction.addToBackStack(null);

        // Применяем изменения
        transaction.commit();
    }

    // Кастомный TextWatcher для отслеживания изменений текста
    private static class CustomTextWatcher implements TextWatcher {
        private final Runnable callback;

        public CustomTextWatcher(Runnable callback) {
            this.callback = callback;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            callback.run();
        }

        @Override
        public void afterTextChanged(Editable s) {}
    }
}
