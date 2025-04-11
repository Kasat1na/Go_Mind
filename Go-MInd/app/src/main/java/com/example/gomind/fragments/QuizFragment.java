package com.example.gomind.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.gomind.model.Question;
import com.example.gomind.R;
import com.example.gomind.SharedPrefManager;
import com.example.gomind.adapters.AnswersAdapter;
import com.example.gomind.api.QuestionAPI;
import com.example.gomind.api.RetrofitClient;
import com.example.gomind.model.TimeResponse;
import com.example.gomind.sound.SoundManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuizFragment extends Fragment implements View.OnClickListener {

    private RecyclerView recyclerView;
    private AnswersAdapter answersAdapter;
    private List<String> answersList = new ArrayList<>();
    private Question question;
    private int selectedAnswerId = -1; // ID выбранного ответа
    private TextView txtQuestion;
    private Button answerBtn;
    private ImageView imgAds;
    private TextView remainingTimeTextView;
    private TextView txtPoints;
    private ScheduledExecutorService scheduler;
    private QuestionAPI questionAPI;

    private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());
    private int id = 0;
    private SoundManager soundManager;
    private Handler localTimerHandler = new Handler(Looper.getMainLooper());
    private Runnable localTimerRunnable;
    private int remainingTime = 60;
    private String category;
    private boolean isEnglish = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.quiz_fragment, container, false);
        soundManager = SoundManager.getInstance(getActivity());
        txtQuestion = view.findViewById(R.id.txt_question);
        imgAds = view.findViewById(R.id.img_add);
        remainingTimeTextView = view.findViewById(R.id.txt_timeAuction);
        txtPoints = view.findViewById(R.id.txt_points);
        answerBtn = view.findViewById(R.id.answer_btn);
        recyclerView = view.findViewById(R.id.question_list);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity())); // 2 столбца для вариантов

        if (getArguments() != null) {
            category = getArguments().getString("CATEGORY");
            isEnglish = getArguments().getBoolean("IS_ENGLISH", false); // По умолчанию false (русский)
        }


        // API запрос на получение вопроса
        questionAPI = RetrofitClient.getInstance(getActivity()).getQuestionAPI();


        answerBtn.setOnClickListener(v -> submitAnswer());
        // Обработчик нажатия кнопки отправки

        getPoints();
        getAdvertisements();
        getQuestion(); // Получаем первый вопрос

        return view;
    }

    private void getPoints() {

            Call<String> call = questionAPI.getPoints("jwt-cookie=" + SharedPrefManager.getInstance(getActivity()).getToken().getAccess_token() +
                    "; refresh-jwt-cookie=" + SharedPrefManager.getInstance(getActivity()).getToken().getRefresh_token());
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    String points = response.body();
//                Log.d("Баллы: ", points);
                    mainThreadHandler.post(() -> txtPoints.setText(points));
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.d("Ошибка времени", " ");
                }
            });

    }

    private void setTextQuestion(String questionText) {
        // Устанавливаем текст в TextView
        txtQuestion.setText(questionText);

        // Измеряем высоту текста
        txtQuestion.post(() -> {
            int maxHeight = (int) getResources().getDimension(R.dimen.max_text_height); // Максимальная высота в dp
            int textHeight = txtQuestion.getHeight();

            // Получаем родительский контейнер
            ViewGroup parent = (ViewGroup) txtQuestion.getParent();

            // Если родитель LinearLayout, используем LinearLayout.LayoutParams
            if (parent instanceof LinearLayout) {
                if (textHeight > maxHeight) {
                    txtQuestion.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                } else {
                    txtQuestion.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, (int) getResources().getDimension(R.dimen.default_text_height))); // 200dp
                }
            }
            // Если родитель RelativeLayout, используем RelativeLayout.LayoutParams
            else if (parent instanceof RelativeLayout) {
                if (textHeight > maxHeight) {
                    txtQuestion.setLayoutParams(new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
                } else {
                    txtQuestion.setLayoutParams(new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT, (int) getResources().getDimension(R.dimen.default_text_height))); // 200dp
                }
            }
        });
    }




    private void getAdvertisements() {

            Call<Integer> call = questionAPI.getAdvertisementsByCost("jwt-cookie=" + SharedPrefManager.getInstance(getActivity()).getToken().getAccess_token() +
                    "; refresh-jwt-cookie=" + SharedPrefManager.getInstance(getActivity()).getToken().getRefresh_token());
            call.enqueue(new Callback<Integer>() {
                @Override
                public void onResponse(Call<Integer> call, Response<Integer> response) {
                    if (response.body() != null) {
                        id = response.body();
                        Log.d("Ид ", " " + id);
                        String imageUrl = "https://www.gwork.press:8443/user/file-system-image-by-id/" + id;

                        // Загрузка изображения с закругленными углами
                        Glide.with(requireActivity())
                                .load(imageUrl)
                                .apply(RequestOptions.bitmapTransform(new RoundedCorners(26))) // Установите радиус закругления
                                .into(imgAds);
                    } else {
                        String imageUrl = "https://www.gwork.press:8443/user/file-system-image-by-id/" + 4;

                        // Загрузка изображения с закругленными углами
                        Glide.with(requireActivity())
                                .load(imageUrl)
                                .apply(RequestOptions.bitmapTransform(new RoundedCorners(26))) // Установите радиус закругления
                                .into(imgAds);
                    }
                }

                @Override
                public void onFailure(Call<Integer> call, Throwable t) {
                    t.printStackTrace();
                    Log.d("Ошибка ", " " + id);
                }
            });

    }

    private void getQuestion() {
        String language = isEnglish ? "ENGLISH" : "RUSSIAN"; // Выбираем язык


        Call<Question> call = questionAPI.getRandomQuestion(category, language);
        call.enqueue(new Callback<Question>() {
                            @Override
                            public void onResponse(Call<Question> call, Response<Question> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    question = response.body();
                                    txtQuestion.setText(question.getText());

                                    answersList.clear();
                                    answersList.add(question.getOptionA());
                                    answersList.add(question.getOptionB());
                                    answersList.add(question.getOptionC());
                                    answersList.add(question.getOptionD());
                                    selectedAnswerId = -1;

                                    AnswersAdapter.AnswerClickListener clickListener = answerId -> selectedAnswerId = answerId;
                                    answersAdapter = new AnswersAdapter(answersList, clickListener);
                                    recyclerView.setAdapter(answersAdapter);
                                } else {
                                    Log.e("Quiz", "Ошибка получения данных: " + response.code());
                                }
                            }

                            @Override
                            public void onFailure(Call<Question> call, Throwable t) {
                                Log.e("Quiz", "Ошибка: " + t.getMessage());
                            }
                        });
    }


    private void submitAnswer() {
        if (selectedAnswerId == -1) {
            return; // Если ответ не выбран, ничего не делаем
        }

        // Отправка выбранного ответа
        Call<Void> call = questionAPI.submitAnswer("jwt-cookie=" + SharedPrefManager.getInstance(getActivity()).getToken().getAccess_token() +
                ";refresh-jwt-cookie=" + SharedPrefManager.getInstance(getActivity()).getToken().getRefresh_token(), category, question.getId(), selectedAnswerId);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("Quiz", "Ответ успешно отправлен!");
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    getPoints(); // Обновляем баллы после отправки ответа
                    getQuestion(); // Получаем новый вопрос
                } else {
                    Log.e("Quiz", "Ошибка при отправке ответа. Код ответа: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("Quiz", "Ошибка: " + t.getMessage());
            }
        });
    }
    private void startPeriodicRequests() {
        scheduler = Executors.newScheduledThreadPool(1);
        Runnable task = () -> {
            Call<TimeResponse> call = questionAPI.getRemainingTime();
            call.enqueue(new Callback<TimeResponse>() {
                @Override
                public void onResponse(Call<TimeResponse> call, Response<TimeResponse> response) {
                    TimeResponse remainingTime = response.body();
                    mainThreadHandler.post(() -> remainingTimeTextView.setText(remainingTime.getMinutesLeft() + ": " + remainingTime.getSecondsLeft()));
                }

                @Override
                public void onFailure(Call<TimeResponse> call, Throwable t) {
                    Log.d("Ошибка времени", " ");
                    mainThreadHandler.post(() -> remainingTimeTextView.setText("Ошибка: " + t.getMessage()));
                }
            });
        };
        scheduler.scheduleAtFixedRate(task, 0, 1, TimeUnit.SECONDS);
    }

    private void stopPeriodicRequests() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
        localTimerHandler.removeCallbacks(localTimerRunnable);
    }

    @Override
    public void onPause() {
        super.onPause();
        stopPeriodicRequests();
    }




    @Override
    public void onResume() {
        super.onResume();
        startPeriodicRequests();
    }





    @Override
    public void onClick(View v) {

    }
}
