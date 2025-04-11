package com.example.gomind.adapters;

// Импорт необходимых библиотек
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.gomind.model.Auction;
import com.example.gomind.R;
import com.example.gomind.api.QuestionAPI;
import com.example.gomind.api.RetrofitClient;
import com.example.gomind.model.TimeResponse;
import com.google.android.material.button.MaterialButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Адаптер для RecyclerView
public class  AuctionAdapter extends RecyclerView.Adapter<AuctionAdapter.ViewHolder> {
    // Поля для хранения данных
    private List<Auction> auctions; // Список аукционов
    private Context context; // Контекст приложения
    private final QuestionAPI questionAPI; // API для получения данных
    private final Handler mainThreadHandler = new Handler(Looper.getMainLooper()); // Обработчик для основного потока

    // Конструктор класса адаптера
    public AuctionAdapter(Context context, List<Auction> auctions) {
        this.context = context; // Инициализация контекста
        this.auctions = auctions; // Инициализация списка аукционов
        this.questionAPI = RetrofitClient.getInstance(context).getQuestionAPI(); // Получение экземпляра API через Retrofit
    }

    // Создание и привязка макета элемента списка
    @NonNull
    @Override
    public AuctionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.auction_item, parent, false); // Загрузка разметки элемента
        return new ViewHolder(view); // Создание нового ViewHolder
    }

    // Привязка данных к элементам разметки
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Auction auction = auctions.get(position); // Получение текущего аукциона из списка

        // Установка данных из объекта аукциона в элементы интерфейса
        holder.nameBtn.setText(auction.getNickname()); // Установка имени пользователя
        holder.btn2.setText(String.valueOf(auction.getPosition())); // Установка позиции
        holder.txtPeaches.setText(String.valueOf(auction.getCost())); // Установка стоимости

        // Загрузка изображения через Glide с закруглением углов
        String imageUrl = "https://www.gwork.press:8443/user/file-system-image-by-id/" + auction.getFileDataId();
        Glide.with(context)
                .load(imageUrl) // Установка URL изображения
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(26))) // Применение закругленных углов
                .into(holder.imgAds); // Привязка изображения к ImageView

        // Настройка визуального стиля элемента списка в зависимости от его позиции
        if (position == 0) {
            // Настройки для первого элемента
            holder.imgClock1.setColorFilter(context.getResources().getColor(android.R.color.white));
            holder.txtTime.setTextColor(context.getResources().getColor(android.R.color.white));
            holder.line.setBackgroundColor(context.getResources().getColor(android.R.color.white));
            holder.line3.setBackgroundColor(context.getResources().getColor(android.R.color.white));
            holder.img5.setColorFilter(context.getResources().getColor(android.R.color.white));
            holder.txtPeaches.setTextColor(context.getResources().getColor(android.R.color.white));
            holder.nameBtn.setTextColor(context.getResources().getColor(R.color.soft_orange)); // Оранжевый цвет
            holder.btn2.setBackgroundResource(R.drawable.click2); // Специальный фон для кнопки
        } else {
            // Настройки для остальных элементов
            holder.imgClock1.setColorFilter(context.getResources().getColor(android.R.color.black));
            holder.txtTime.setTextColor(context.getResources().getColor(android.R.color.black));
            holder.line.setBackgroundColor(context.getResources().getColor(android.R.color.black));
            holder.line3.setBackgroundColor(context.getResources().getColor(android.R.color.black));
            holder.img5.setColorFilter(context.getResources().getColor(android.R.color.black));
            holder.txtPeaches.setTextColor(context.getResources().getColor(android.R.color.black));
            holder.nameBtn.setTextColor(context.getResources().getColor(android.R.color.black));
            holder.btn2.setBackgroundResource(R.drawable.noclick2); // Другой фон для кнопки
        }

        // Запуск таймера для обновления времени
        startTimer(holder.txtTime, auction);
    }

    // Возвращает количество элементов в списке
    @Override
    public int getItemCount() {
        return auctions.size(); // Возвращаем размер списка
    }

    // Метод для запуска таймера обновления времени
    private void startTimer(TextView txtTime, Auction auction) {
        final Handler handler = new Handler(Looper.getMainLooper()); // Создаем обработчик для основного потока
        handler.post(new Runnable() {
            @Override
            public void run() {
                getRemainingTime(txtTime, auction); // Обновляем оставшееся время
                handler.postDelayed(this, 1000); // Повторяем каждую секунду
            }
        });
    }

    // Получение оставшегося времени через API
    private void getRemainingTime(TextView txtTime, Auction auction) {
        Call<TimeResponse> call = questionAPI.getRemainingTime(); // Запрос к API
        call.enqueue(new Callback<TimeResponse>() {
            @Override
            public void onResponse(Call<TimeResponse> call, Response<TimeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    TimeResponse remainingTime = response.body(); // Получение времени из ответа
                    mainThreadHandler.post(() -> txtTime.setText(remainingTime.getMinutesLeft() + ": " + remainingTime.getSecondsLeft())); // Обновление текстового поля
                }
            }

            @Override
            public void onFailure(Call<TimeResponse> call, Throwable t) {
                Log.d("Ошибка времени", "Ошибка при получении времени: " + t.getMessage()); // Логирование ошибки
            }
        });
    }

    // ViewHolder для хранения ссылок на элементы разметки
    public static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialButton nameBtn, btn2; // Кнопки
        TextView txtPeaches, txtTime; // Текстовые поля
        ImageView imgAds, imgClock1, img5; // Изображения
        View line, line3; // Линии разделителей

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Привязка элементов разметки
            nameBtn = itemView.findViewById(R.id.name_btn);
            btn2 = itemView.findViewById(R.id.btn2);
            txtPeaches = itemView.findViewById(R.id.txt_peaches);
            imgAds = itemView.findViewById(R.id.img_add);
            imgClock1 = itemView.findViewById(R.id.img_clock1);
            txtTime = itemView.findViewById(R.id.txt_timeAuction); // Поле для оставшегося времени
            line = itemView.findViewById(R.id.line);
            line3 = itemView.findViewById(R.id.line3);
            img5 = itemView.findViewById(R.id.img5);
        }
    }
}
