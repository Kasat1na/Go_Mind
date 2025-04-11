package com.example.gomind.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.gomind.SharedPrefManager;
import com.example.gomind.adapters.LeadersAdapter;
import com.example.gomind.R;
import com.example.gomind.model.Leader;
import com.example.gomind.api.QuestionAPI;
import com.example.gomind.api.RetrofitClient;
import com.example.gomind.model.TimeResponse;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LeadersFragment extends Fragment {


    TextView txtTime;
    private ScheduledExecutorService scheduler;
    List<Leader> leaders;
    private final QuestionAPI questionAPI = RetrofitClient.getInstance(getActivity()).getQuestionAPI();
    private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());

    ImageView imgAds;
    int id = 0;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.leaders_fragment, container, false);
        txtTime = view.findViewById(R.id.txt_timeAuction);

        imgAds = view.findViewById(R.id.img_add);


        Call<Integer> call1 = questionAPI.getAdvertisementsByCost("jwt-cookie=" + SharedPrefManager.getInstance(getActivity()).getToken().getAccess_token() +
                "; refresh-jwt-cookie=" + SharedPrefManager.getInstance(getActivity()).getToken().getRefresh_token());
        call1.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {

                if(response.body() != null) {
                    id = response.body();
                    Log.d("Ид ", " " + id);
                    Glide.with(requireActivity())
                            .load("https://www.gwork.press:8443/user/file-system-image-by-id/" + id)
                            .apply(RequestOptions.bitmapTransform(new RoundedCorners(26)))
                            .into(imgAds);
                }
                else {

                    Glide.with(requireActivity())
                            .load("https://www.gwork.press:8443/user/file-system-image-by-id/" + 4)
                            .apply(RequestOptions.bitmapTransform(new RoundedCorners(26)))
                            .into(imgAds);
                }

            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                t.printStackTrace();
                Log.d("Ошибка ", " " + id);
            }
        });
        RecyclerView recyclerView = view.findViewById(R.id.question_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        // Создаём клиент Retrofit и интерфейс API



        // Выполняем запрос
        Call<List<Leader>> call = questionAPI.getUsersWithPoints();
        call.enqueue(new Callback<List<Leader>>() {
            @Override
            public void onResponse(Call<List<Leader>> call, Response<List<Leader>> response) {
                leaders = response.body();


                if(leaders!= null) {
                    LeadersAdapter adapter = new LeadersAdapter(leaders);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<Leader>> call, Throwable t) {
                t.printStackTrace();
            }
        });

        // Пример данных


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        startPeriodicRequests();

    }

    @Override
    public void onPause() {
        super.onPause();
        stopPeriodicRequests();
    }

    private void startPeriodicRequests() {
        scheduler = Executors.newScheduledThreadPool(1);
        Runnable task = () -> {
            Call<TimeResponse> call = questionAPI.getRemainingTime();
            call.enqueue(new Callback<TimeResponse>() {
                @Override
                public void onResponse(Call<TimeResponse> call, Response<TimeResponse> response) {

                    TimeResponse remainingTime = response.body();
                    mainThreadHandler.post(() -> txtTime.setText(remainingTime.getMinutesLeft() + ": " + remainingTime.getSecondsLeft()));

                }

                @Override
                public void onFailure(Call<TimeResponse> call, Throwable t) {
                    Log.d("Ошибка времени", " ");
                    mainThreadHandler.post(() -> txtTime.setText("Ошибка: " + t.getMessage()));

                }
            });
        };

        // Запускаем задачу каждую секунду
        scheduler.scheduleAtFixedRate(task, 0, 1, TimeUnit.SECONDS);
    }

    private void stopPeriodicRequests() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }

}
