package com.example.gomind.api;

import android.content.Context;
import android.util.Log;

import com.example.gomind.model.RefreshToken;
import com.example.gomind.SharedPrefManager;
import com.example.gomind.model.Token;


import okhttp3.Interceptor;
import okhttp3.Response;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;

import java.io.IOException;

public class AuthInterceptor implements Interceptor {
    private final SharedPrefManager sharedPrefManager;
    private final Context context; // Добавляем контекст
    private static boolean isRefreshing = false;  // Переменная для блокировки обновления токена
    private static Token newToken;  // Новый токен после обновления
    // Передаем контекст в конструктор
    public AuthInterceptor(Context context) {
        this.sharedPrefManager = SharedPrefManager.getInstance(context);
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();

        // Попробуем добавить токен в запрос
        Token token = sharedPrefManager.getToken();
        if (token != null && token.getAccess_token() != null) {
            originalRequest = originalRequest.newBuilder()
                    .header("Authorization", "Bearer " + token.getAccess_token())
                    .build();
        }

        // Отправляем запрос
        Response response = chain.proceed(originalRequest);

        // Если ошибка 403, обновляем токен
        if (response.code() == 403 && token != null) {
            Log.d("Токен ", " ");
            if (!isRefreshing) {
                Log.d("Токен 1", " ");
                synchronized (this) {  // Блокировка, чтобы только один поток выполнял запрос на обновление токена
                    if (!isRefreshing) {
                        Log.d("Токен 2 ", " ");
                        isRefreshing = true;  // Устанавливаем флаг, что обновление токена в процессе
                        refreshAuthToken();
                        isRefreshing = false;  // Снимаем блокировку
                    }
                }
            } else {
                while (isRefreshing) {  // Если обновление токена уже происходит, ждем
                    try {
                        Thread.sleep(100);  // Можно уменьшить время ожидания
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            if (newToken != null) {
                // Повторяем запрос с новым токеном
                originalRequest = originalRequest.newBuilder()
                        .header("Authorization", "Bearer " + newToken.getAccess_token())
                        .build();
                response.close();  // Закрываем старый ответ
                response = chain.proceed(originalRequest);  // Отправляем запрос с новым токеном
            }
        }

        return response;
    }

    private void refreshAuthToken() {
        Log.d("Токен 5", " ");
        Token currentToken = sharedPrefManager.getToken(); // получаем текущий токен из SharedPreferences
        RefreshToken refreshToken = new RefreshToken(currentToken.getRefresh_token());
        Call<Token> call = RetrofitClient.getInstance(context).getAuthApi().refreshToken(refreshToken);

        call.enqueue(new Callback<Token>() {

            @Override
            public void onResponse(Call<Token> call, retrofit2.Response<Token> response) {
                if (response.isSuccessful()) {
                    Token newToken = response.body();
                    // Используй новый токен
                    sharedPrefManager.saveToken(newToken);
                    Log.d("Новый токен: ", newToken.toString());
                } else {
                    // Ошибка сервера
                    System.out.println("Ошибка обновления токена: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                // Сетевая ошибка или ошибка сериализации
                t.printStackTrace();
            }
        });
    }

}