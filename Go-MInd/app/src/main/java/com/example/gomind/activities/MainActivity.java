package com.example.gomind.activities;

import static ru.yoomoney.sdk.kassa.payments.Checkout.createTokenizationResult;
import static ru.yoomoney.sdk.kassa.payments.Checkout.createTokenizeIntent;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

//import com.example.gomind.PaymentsModule;
import com.example.gomind.model.ApiResponse;
import com.example.gomind.R;
import com.example.gomind.SharedPrefManager;
import com.example.gomind.Utils;
import com.example.gomind.api.RetrofitClient;
import com.example.gomind.fragments.ChooseModeFragment;
import com.example.gomind.fragments.LeadersFragment;
import com.example.gomind.fragments.LoginFragment;
import com.example.gomind.fragments.ProfileFragment;
import com.example.gomind.model.UserData;
import com.example.gomind.sound.SoundManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Currency;
import java.util.HashSet;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Response;
import ru.rustore.sdk.billingclient.RuStoreBillingClient;
import ru.yoomoney.sdk.kassa.payments.TokenizationResult;
import ru.yoomoney.sdk.kassa.payments.checkoutParameters.Amount;
import ru.yoomoney.sdk.kassa.payments.checkoutParameters.PaymentMethodType;
import ru.yoomoney.sdk.kassa.payments.checkoutParameters.PaymentParameters;
import ru.yoomoney.sdk.kassa.payments.checkoutParameters.SavePaymentMethod;


public class MainActivity extends AppCompatActivity {

    Fragment fragment;
    FragmentManager fragmentManager;
    private SoundManager soundManager;

    // RuStoreBillingClient billingClient = YourDependencyInjection.getBillingClient();

   // private static final RuStoreBillingClient billingClient = PaymentsModule.provideRuStorebillingClient();

    public MainActivity() {
        super(R.layout.activity_main);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
       // billingClient.onNewIntent(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
         //   billingClient.onNewIntent(getIntent());
            Log.d("Клиент: ", "Мейн");
        }
// Инициализация SoundManager
        soundManager = SoundManager.getInstance(this);
        // Проверяем, вошел ли пользователь
        if (SharedPrefManager.getInstance(this).isLoggedIn()) {

            loadMainFragment();
        } else {
            // Если не вошел, открываем LoginFragment
            openLoginFragment();
        }

        // Настроим навигацию
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setItemIconTintList(null);
        Objects.requireNonNull(bottomNavigationView.getMenu().getItem(0).getIcon()).setTint(R.drawable.home);

        // Установка слушателя
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Определяем действия в зависимости от выбранного элемента
                soundManager.playSound();
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    fragment = new LeadersFragment();
                    fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.main_container, fragment)
                            .commit();
                } else if (id == R.id.nav_quiz) {
                    fragment = new ChooseModeFragment();
                    fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.main_container, fragment)
                            .commit();
                } else if (id == R.id.nav_profile) {
                    fragment = new ProfileFragment();
                    fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.main_container, fragment)
                            .commit();
                }
                return true;
            }
        });

        // Первоначальная загрузка главного фрагмента
        fragment = new LeadersFragment();
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.main_container, fragment)
                .commit();
    }

//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        billingClient.onNewIntent(intent);
//    }

    private void loadMainFragment() {
        // Загружаем основной экран
        Fragment fragment = new LeadersFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.main_container, fragment)
                .commit();
    }
    private void openLoginFragment() {
        // Загружаем экран логина
        Fragment fragment = new LoginFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.main_container, fragment)
                .commit();
    }

    public void startTokenize() {
        // Пример токенизации
        PaymentParameters paymentParameters = new PaymentParameters(
                new Amount(BigDecimal.TEN, Currency.getInstance("RUB")),
                "Груша",
                "Внутренняя валюта",
                "live_NDk2NjAwpCrWyn1RSQ4Q4ZMwglUB0C_DaFQQ3yypP7Y",
                "496600",
                SavePaymentMethod.OFF,
                new HashSet<>(Arrays.asList(PaymentMethodType.BANK_CARD))
        );

        Intent intent = createTokenizeIntent(this, paymentParameters);
        startActivityForResult(intent, 345);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 345) {
            switch (resultCode) {
                case RESULT_OK:
                    if (data != null) {
                        TokenizationResult result = createTokenizationResult(data);
                        Log.d("токен", result.toString());
                    }
                    break;
                case RESULT_CANCELED:
                    Log.d("Ошибка токена", "");
                    break;
            }
        }
    }

    public void getProfile() {
        // Получаем профиль пользователя с использованием сохраненного токена
        Call<UserData> call = RetrofitClient.getInstance(this)
                .getUserAPI()
                .getProfile("jwt-cookie=" + SharedPrefManager.getInstance(this).getToken().getAccess_token() +
                        "; refresh-jwt-cookie=" + SharedPrefManager.getInstance(this).getToken().getRefresh_token());

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response<UserData> response = call.execute();
                    if (response.isSuccessful() && response.body() != null) {
                        Utils.user = response.body();
                        Log.d("API", "Пользователь получен: " + Utils.user);
                    } else {
                        Log.e("Ошибка API", "Код ответа: " + response.code());
                    }
                } catch (IOException e) {
                    Log.e("Ошибка сети", e.getMessage(), e);
                }
            }
        }).start();
    }
}
