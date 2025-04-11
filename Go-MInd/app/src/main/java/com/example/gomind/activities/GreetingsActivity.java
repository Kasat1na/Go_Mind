package com.example.gomind.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gomind.R;
import com.example.gomind.SharedPrefManager;
import com.example.gomind.sound.SoundManager;
import com.github.barteksc.pdfviewer.PDFView;

public class GreetingsActivity extends AppCompatActivity {
    private CheckBox confirmCheckBox;
    private SoundManager soundManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_greetings);

        SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(this);

        // Проверяем, нужно ли показывать PDF
        if (!sharedPrefManager.shouldShowPDFOnStart()) {
            startActivity(new Intent(this, AuthenticationActivity.class));
            finish();
            return;
        }

        // Инициализируем SoundManager
        soundManager = SoundManager.getInstance(this);

        PDFView pdfView = findViewById(R.id.pdfContainer);
        pdfView.fromAsset("agreement1.pdf")
                .defaultPage(0)
                .spacing(10) // Отступ между страницами (разрыв)
                .enableSwipe(true) // Включаем свайп
                .swipeHorizontal(false) // Вертикальное пролистывание
                .pageSnap(true) // Закрепление страницы
                .pageFling(true) // Быстрое перелистывание
                .fitEachPage(true) // Подгонка страницы под экран
                .load();
        confirmCheckBox = findViewById(R.id.confirmCheckBox);

        findViewById(R.id.confirmButton).setOnClickListener(v -> {
            soundManager.playSound();
            if (confirmCheckBox.isChecked()) {
                sharedPrefManager.saveAgreementAccepted();
                startActivity(new Intent(this, AuthenticationActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Необходимо поставить галочку для подтверждения", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
