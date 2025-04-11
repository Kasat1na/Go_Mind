package com.example.gomind.activities;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gomind.R;
import com.example.gomind.SharedPrefManager;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        ImageView mainImageView = findViewById(R.id.imageView);
        ImageView ricochetImageView = findViewById(R.id.ricochetImageView);
        ImageView imgLeft = findViewById(R.id.img_left);
        ImageView imgRight = findViewById(R.id.img_right);

        // Получаем SharedPrefManager
        SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(this);
        boolean shouldShowPDF = sharedPrefManager.shouldShowPDFOnStart();

        // Устанавливаем начальное состояние для картинок
        mainImageView.setScaleX(10f);
        mainImageView.setScaleY(10f);
        mainImageView.setTranslationY(-1000f);

        ricochetImageView.setScaleX(10f);
        ricochetImageView.setScaleY(10f);
        ricochetImageView.setTranslationY(-1000f);

        // Анимация падения
        PropertyValuesHolder scaleXMain = PropertyValuesHolder.ofFloat("scaleX", 1f);
        PropertyValuesHolder scaleYMain = PropertyValuesHolder.ofFloat("scaleY", 1f);
        PropertyValuesHolder translationYMain = PropertyValuesHolder.ofFloat("translationY", 0f);

        ObjectAnimator mainAnimator = ObjectAnimator.ofPropertyValuesHolder(mainImageView, scaleXMain, scaleYMain, translationYMain);
        mainAnimator.setDuration(900);

        ObjectAnimator ricochetAnimator = ObjectAnimator.ofPropertyValuesHolder(ricochetImageView, scaleXMain, scaleYMain, translationYMain);
        ricochetAnimator.setDuration(900);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(mainAnimator, ricochetAnimator);

        mainAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                fadeInView(imgLeft);
                fadeInView(imgRight);
                animateRicochet(ricochetImageView);

                ricochetImageView.postDelayed(() -> {
                    Intent intent = shouldShowPDF ?
                            new Intent(SplashScreen.this, GreetingsActivity.class) :
                            new Intent(SplashScreen.this, AuthenticationActivity.class);

                    startActivity(intent);
                    finish();
                }, 1500);
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });

        animatorSet.start();
    }

    private void animateRicochet(ImageView ricochetImageView) {
        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat("scaleX", 0.6f, 1.2f, 1f);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat("scaleY", 0.6f, 1.2f, 1f);

        ObjectAnimator ricochetAnimator = ObjectAnimator.ofPropertyValuesHolder(ricochetImageView, scaleX, scaleY);
        ricochetAnimator.setDuration(900);
        ricochetAnimator.start();
    }

    private void fadeInView(View view) {
        view.setVisibility(View.VISIBLE);
        ObjectAnimator fadeInAnimator = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
        fadeInAnimator.setDuration(1500);
        fadeInAnimator.start();
    }
}
