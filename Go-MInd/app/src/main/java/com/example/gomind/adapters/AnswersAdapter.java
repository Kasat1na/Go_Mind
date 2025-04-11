package com.example.gomind.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gomind.R;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class AnswersAdapter extends RecyclerView.Adapter<AnswersAdapter.AnswerViewHolder> {

    private List<String> answers; // Список ответов (A, B, C, D)

    public interface AnswerClickListener {
        void onClickListener(int answerId); // Передаёт выбранный ID ответа
    }

    private AnswerClickListener clickListener;
    private int selectedItemIndex = -1; // -1 означает, что ничего не выбрано

    public AnswersAdapter(List<String> answers, AnswerClickListener clickListener) {
        this.answers = answers;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public AnswerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quiz_item, parent, false);
        return new AnswerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnswerViewHolder holder, int position) {
        holder.answerButton.setText(answers.get(position));

        // Устанавливаем фон кнопки: выделенный или стандартный
        if (position == selectedItemIndex) {
            holder.answerButton.setBackgroundResource(R.drawable.auth_button);
        } else {
            holder.answerButton.setBackgroundResource(R.drawable.border_inside);
        }

        holder.answerButton.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition == RecyclerView.NO_POSITION) {
                return;
            }

            // Переключаем выбранный элемент
            if (currentPosition == selectedItemIndex) {
                selectedItemIndex = -1; // Сброс выбора
            } else {
                selectedItemIndex = currentPosition; // Установка нового выбора
            }

            clickListener.onClickListener(currentPosition + 1); // Передаём answerId (1-based)
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return answers.size();
    }

    public static class AnswerViewHolder extends RecyclerView.ViewHolder {
        MaterialButton answerButton;

        public AnswerViewHolder(@NonNull View itemView) {
            super(itemView);
            answerButton = itemView.findViewById(R.id.btn_long_answer);
        }
    }
}
