package com.example.gomind.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.gomind.model.ApiResponse;
import com.example.gomind.R;
import com.example.gomind.SharedPrefManager;
import com.example.gomind.api.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmailChangeFragment extends Fragment {

    private TextView emailConfirmationText;
    private String email;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            email = getArguments().getString("email"); // Получение email из аргументов
        }

        if (email != null) {
            requestEmailChange(email);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.code_email, container, false);

        emailConfirmationText = view.findViewById(R.id.email_confirmation_text);
        Button btnOk = view.findViewById(R.id.btn_ok);

        // Обработчик нажатия кнопки "ОК"
        btnOk.setOnClickListener(v -> {
            logout(); // Очищаем токены
            navigateToLogin(); // Переход на экран авторизации
        });

        return view;
    }

    private void requestEmailChange(String newEmail) {
        Call<ApiResponse> call = RetrofitClient.getInstance(getActivity())
                .getUserAPI()
                .requestEmailChange(newEmail);

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getStatus() == 200) {
                        emailConfirmationText.setText("Email успешно подтвержден!");
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // Ошибки сети не изменяют текст
            }
        });
    }

    private void logout() {
        // Удаляем только токены, не затрагивая другие настройки
        SharedPreferences.Editor editor = SharedPrefManager.getInstance(getActivity()).getSharedPreferences().edit();
        editor.remove("token");
        editor.remove("refreshToken");
        editor.apply();
    }

    private void navigateToLogin() {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_container, new LoginFragment()); // Убедись, что ID контейнера правильный
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
