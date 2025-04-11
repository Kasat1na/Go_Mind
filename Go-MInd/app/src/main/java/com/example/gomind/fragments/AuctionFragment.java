package com.example.gomind.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gomind.model.Auction;
import com.example.gomind.R;
import com.example.gomind.adapters.AuctionAdapter;
import com.example.gomind.api.QuestionAPI;
import com.example.gomind.api.RetrofitClient;
import com.example.gomind.sound.SoundManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuctionFragment extends Fragment {

    private RecyclerView recyclerView;
    private AuctionAdapter adapter;
    private List<Auction> auctionList = new ArrayList<>();
    private Button betBtn; // Добавляем кнопку
    private SoundManager soundManager;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Инфлейтинг разметки фрагмента
        View view = inflater.inflate(R.layout.auction_fragment, container, false);
        // Инициализация SoundManager
        soundManager = SoundManager.getInstance(getContext());
        // Инициализация RecyclerView
        recyclerView = view.findViewById(R.id.question_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Инициализация адаптера
        adapter = new AuctionAdapter(getContext(), auctionList);
        recyclerView.setAdapter(adapter);

        // Инициализация кнопки "Ставка"
        betBtn = view.findViewById(R.id.bet_btn);

        // Устанавливаем обработчик нажатия с воспроизведением звука
        betBtn.setOnClickListener(v -> {
            soundManager.playSound(); // Воспроизведение звука при нажатии
            navigateToUploadFragment();
        });
        // Получение данных из API
        loadAuctions();

        return view;
    }

    private void navigateToUploadFragment() {
        // Создаем объект UploadFragment
        Fragment uploadFragment = new UploadFragment();

        // Выполняем замену фрагмента
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_container, uploadFragment) // Убедитесь, что ID контейнера верный
                .addToBackStack(null) // Добавляем в BackStack для возможности вернуться назад
                .commit();
    }

    private void loadAuctions() {

        QuestionAPI auctionApi = RetrofitClient.getInstance(requireContext()).getQuestionAPI();
        Call<List<Auction>> call = auctionApi.getAuctions();

        call.enqueue(new Callback<List<Auction>>() {
            @Override
            public void onResponse(Call<List<Auction>> call, Response<List<Auction>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    auctionList.clear();
                    auctionList.addAll(response.body());
                    adapter.notifyDataSetChanged(); // Обновление данных в RecyclerView
                } else {
                    Log.e("Retrofit", "Ошибка ответа: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Auction>> call, Throwable t) {
                Log.e("Retrofit", "Ошибка запроса: " + t.getMessage());
            }
        });
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        soundManager.release();
    }
}
