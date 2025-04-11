package com.example.gomind.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.gomind.R;
import com.example.gomind.SharedPrefManager;
import com.example.gomind.api.QuestionAPI;
import com.example.gomind.api.RetrofitClient;
import com.example.gomind.sound.SoundManager;
import com.google.android.material.button.MaterialButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChooseModeFragment extends Fragment {
    private SoundManager soundManager;
    private QuestionAPI questionAPI;
    private boolean isEnglish;
    private SharedPrefManager sharedPrefManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_mode_fragment, container, false);

        soundManager = SoundManager.getInstance(getContext());
        questionAPI = RetrofitClient.getInstance(getActivity()).getQuestionAPI();
        sharedPrefManager = SharedPrefManager.getInstance(getContext());

        MaterialButton shortAnswersButton = view.findViewById(R.id.short_btn);
        MaterialButton longAnswersButton = view.findViewById(R.id.long_btn);
        MaterialButton ruButton = view.findViewById(R.id.ru_btn);
        MaterialButton enButton = view.findViewById(R.id.en_btn);

        // Загружаем язык из SharedPreferences
        isEnglish = sharedPrefManager.getLanguage();
        switchLanguage(isEnglish, shortAnswersButton, longAnswersButton, ruButton, enButton);

        ruButton.setOnClickListener(v -> {
            switchLanguage(false, shortAnswersButton, longAnswersButton, ruButton, enButton);
            sharedPrefManager.saveLanguage(false);
        });

        enButton.setOnClickListener(v -> {
            switchLanguage(true, shortAnswersButton, longAnswersButton, ruButton, enButton);
            sharedPrefManager.saveLanguage(true);
        });

        shortAnswersButton.setOnClickListener(v -> {
            soundManager.playSound();
            updateButtonStyle(shortAnswersButton, longAnswersButton);
            openQuizFragment("SHORT");
            fetchQuestions("SHORT");
        });

        longAnswersButton.setOnClickListener(v -> {
            soundManager.playSound();
            updateButtonStyle(longAnswersButton, shortAnswersButton);
            openQuizFragment("LONG");
            fetchQuestions("LONG");
        });

        return view;
    }

    private void switchLanguage(boolean english, MaterialButton shortBtn, MaterialButton longBtn, MaterialButton ruBtn, MaterialButton enBtn) {
        isEnglish = english;
        if (english) {
            shortBtn.setText("Short Answers");
            longBtn.setText("Long Answers");
            enBtn.setBackgroundResource(R.drawable.auth_button);
            ruBtn.setBackgroundResource(R.drawable.reg_button);
        } else {
            shortBtn.setText("Короткие ответы");
            longBtn.setText("Длинные ответы");
            ruBtn.setBackgroundResource(R.drawable.auth_button);
            enBtn.setBackgroundResource(R.drawable.reg_button);
        }
    }

    private void updateButtonStyle(MaterialButton activeButton, MaterialButton inactiveButton) {
        activeButton.setBackgroundResource(R.drawable.auth_button);
        activeButton.setTextColor(getResources().getColor(R.color.white));
        inactiveButton.setBackgroundResource(R.drawable.reg_button);
        inactiveButton.setTextColor(getResources().getColor(R.color.black));
    }

    private void openQuizFragment(String category) {
        QuizFragment quizFragment = new QuizFragment();
        Bundle args = new Bundle();
        args.putString("CATEGORY", category);
        args.putBoolean("IS_ENGLISH", isEnglish);
        quizFragment.setArguments(args);

        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_container, quizFragment)
                .addToBackStack(null)
                .commit();
    }

    private void fetchQuestions(String category) {
        Call<Void> call = questionAPI.submitAnswer(
                "jwt-cookie=" + "your_token",
                category, 0, 0);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("API", "Вопросы получены");
                } else {
                    Log.e("API", "Ошибка получения вопросов: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("API", "Ошибка сети: " + t.getMessage());
            }
        });
    }
}
