package com.example.gomind;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.example.gomind.model.Token;
import com.example.gomind.model.User;

import java.io.ByteArrayOutputStream;

public class SharedPrefManager {
    private static final String SHARED_PREF_NAME = "my_shared_prefs";
    private static final String VISITED_KEY = "IS_VISITED";
    private static final String AGREEMENT_ACCEPTED_KEY = "AGREEMENT_ACCEPTED";
    private static final String FIRST_RUN_KEY = "FIRST_RUN";


    private static SharedPrefManager mInstance;
    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;

    // В SharedPrefManager
    private static final String LANGUAGE_KEY = "LANGUAGE";

    public void saveLanguage(boolean isEnglish) {
        editor.putBoolean(LANGUAGE_KEY, isEnglish);
        editor.apply();
    }

    public boolean getLanguage() {
        return sharedPreferences.getBoolean(LANGUAGE_KEY, false); // По умолчанию русский
    }

    public boolean shouldShowPDFOnStart() {
        return !isAgreementAccepted(); // Показывать PDF, если соглашение не принято
    }
    private SharedPrefManager(Context context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }
    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }
    public static synchronized SharedPrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }
    // Проверяем, первый ли запуск
    public boolean isFirstRun() {
        return sharedPreferences.getBoolean(FIRST_RUN_KEY, true);
    }

    public void setFirstRunDone() {
        editor.putBoolean(FIRST_RUN_KEY, false);  // Не сбрасывать этот флаг при выходе.
        editor.apply();
    }

    public void setPDFShown() {
        editor.putBoolean(VISITED_KEY, true);
        editor.apply();
    }

    public boolean shouldShowPDF() {
        return !sharedPreferences.getBoolean(VISITED_KEY, false);
    }

    // --- Флаг принятия соглашения ---
    public void saveAgreementAccepted() {
        editor.putBoolean(AGREEMENT_ACCEPTED_KEY, true);
        editor.apply();
    }

    public boolean isAgreementAccepted() {
        return sharedPreferences.getBoolean(AGREEMENT_ACCEPTED_KEY, false);
    }

    // --- Сохранение и получение токена ---
    public void saveToken(Token token) {
        if (token != null) {
            editor.putString("token", token.getAccess_token());
            editor.putString("refreshToken", token.getRefresh_token());
            editor.apply();
        }
    }

    public Token getToken() {
        String accessToken = sharedPreferences.getString("token", null);
        String refreshToken = sharedPreferences.getString("refreshToken", null);

        if (accessToken == null || refreshToken == null) {
            return null; // Возвращаем null, если нет сохраненного токена
        }

        return new Token(accessToken, refreshToken);
    }

    public boolean isLoggedIn() {
        return sharedPreferences.contains("token") && getToken() != null;
    }

    public void saveUser(User user) {
        if (user != null) {
            editor.putString("email", user.getEmail());
            editor.putString("nickname", user.getNickname());
            editor.putInt("pears", user.getPears());
            editor.putInt("count", user.getCount());
            saveToken(user.getToken());
            editor.apply();
        }
    }

    public User getUser() {
        if (!sharedPreferences.contains("email")) {
            return null;
        }

        return new User(
                sharedPreferences.getString("email", null),
                sharedPreferences.getString("nickname", null),
                sharedPreferences.getInt("pears", 0),
                sharedPreferences.getInt("count", 0),
                getToken()
        );
    }

    public void saveImage(Bitmap bitmap) {
        if (bitmap != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] imageBytes = baos.toByteArray();
            String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            editor.putString("image", encodedImage);
            editor.apply();
        }
    }

    public Bitmap loadImage() {
        String encodedImage = sharedPreferences.getString("image", null);
        if (encodedImage != null) {
            byte[] imageBytes = Base64.decode(encodedImage, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        }
        return null;
    }

    // Очистка данных (например, при выходе из аккаунта)
    public void clear() {
        editor.clear();
        editor.apply();
    }
}
