package com.example.gomind.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.gomind.R;
import com.example.gomind.SharedPrefManager;
import com.example.gomind.api.QuestionAPI;
import com.example.gomind.api.RetrofitClient;
import com.example.gomind.model.UserData;
import com.example.gomind.sound.SoundManager;
import com.google.android.material.button.MaterialButton;

import java.util.concurrent.ScheduledExecutorService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment implements View.OnClickListener {


    private EditText edtNick;
    private EditText edtEmail;
    private EditText edtPears;
    private TextView txtPoints;
    private ScheduledExecutorService scheduler;
    private final QuestionAPI questionAPI = RetrofitClient.getInstance(getActivity()).getQuestionAPI();
    private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());

    MaterialButton buypearsBtn;
    MaterialButton balanceBtn;
    MaterialButton addImageBtn;
    MaterialButton auctionBtn;
    MaterialButton exitBtn;
    private SoundManager soundManager;
    private boolean isEmailChangeRequested = false;
    // Добавляем поле для хранения старого email
    private String oldEmail;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_fragment, container, false);

        // Инициализируем элементы
        ImageView iconKrug = view.findViewById(R.id.icon_krug);
        ImageView iconCross = view.findViewById(R.id.icon_cross);
        ImageView iconDone = view.findViewById(R.id.icon_done);
        edtEmail = view.findViewById(R.id.edt_email);

        // Начальное состояние
        resetIcons(iconKrug, iconCross, iconDone);

        // Слушатель на иконку "krug"
        iconKrug.setOnClickListener(v -> {
            iconKrug.setVisibility(View.GONE);
            iconCross.setVisibility(View.VISIBLE);
            iconDone.setVisibility(View.VISIBLE);
            edtEmail.setFocusableInTouchMode(true);
            edtEmail.setFocusable(true);
            edtEmail.requestFocus();
        });

        // Слушатель на иконку "cross"
        iconCross.setOnClickListener(v -> {
            resetIcons(iconKrug, iconCross, iconDone);
        });

        iconDone.setOnClickListener(v -> {
            String emailText = edtEmail.getText().toString().trim();
            if (emailText.isEmpty()) {
                edtEmail.setError("Введите email!");
            } else if (emailText.equals(oldEmail)) {  // Проверка на совпадение
                edtEmail.setError("Новый email должен отличаться от старого!");
            } else {
                showEmailConfirmationDialog(emailText);
            }
        });

        // Инициализация SoundManager
        soundManager = SoundManager.getInstance(getContext());
        edtNick = view.findViewById(R.id.edt_bid);
        edtPears = view.findViewById(R.id.edt_pears);
        txtPoints = view.findViewById(R.id.txt_points);

        addImageBtn = view.findViewById(R.id.add_image_btn);
        auctionBtn = view.findViewById(R.id.auction_btn);
        balanceBtn = view.findViewById(R.id.money);
        buypearsBtn = view.findViewById(R.id.byu_fruits);
        exitBtn = view.findViewById(R.id.exit_btn);

        addImageBtn.setOnClickListener(this);
        balanceBtn.setOnClickListener(this);
        buypearsBtn.setOnClickListener(this);
        exitBtn.setOnClickListener(this);
        auctionBtn.setOnClickListener(this);

        getProfile();
        getPoints();

        return view;
    }


    // Метод для сброса иконок в начальное состояние
    private void resetIcons(ImageView iconKrug, ImageView iconCross, ImageView iconDone) {
        iconKrug.setVisibility(View.VISIBLE);
        iconCross.setVisibility(View.GONE);
        iconDone.setVisibility(View.GONE);
        edtEmail.clearFocus();
        edtEmail.setFocusable(false);
        edtEmail.setFocusableInTouchMode(false);
    }



    //message
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        edtEmail.setOnFocusChangeListener((v, hasFocus) -> {
//            if (!hasFocus) {
//                // Показываем иконку, если поле потеряло фокус
//                showEmailConfirmationDialog();
//            }
//        });
    }

    private void showEmailConfirmationDialog(String email) {
        if (isEmailChangeRequested) {
            return;
        }

        EmailConfirmationFragment dialogFragment = new EmailConfirmationFragment();
        dialogFragment.setEmail(email);
        dialogFragment.setEmailChangeListener(new EmailConfirmationFragment.OnEmailChangeListener() {
            @Override
            public void onConfirmEmailChange(String newEmail) {
                // Закрываем EmailConfirmationFragment перед добавлением нового
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                fragmentManager.popBackStack(); // Удаляет последний фрагмент из стека (EmailConfirmationFragment)

                // Запускаем EmailChangeFragment
                showEmailChangeFragment(newEmail);
                isEmailChangeRequested = true;
            }

            @Override
            public void onCancelEmailChange() {
                resetIcons(null, null, null);
            }
        });

        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.main_container, dialogFragment)
                .addToBackStack(null)
                .commit();
    }

    private void showEmailChangeFragment(String newEmail) {
        // Создаем и настраиваем фрагмент
        EmailChangeFragment emailChangeFragment = new EmailChangeFragment();

        Bundle args = new Bundle();
        args.putString("email", newEmail); // передача строки
        emailChangeFragment.setArguments(args);

        // Получаем менеджер фрагментов
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        // Добавляем фрагмент поверх текущего
        transaction.add(R.id.main_container, emailChangeFragment) // Используем add вместо replace
                .addToBackStack(null)  // Не обязательно, если нужно закрыть фрагмент при переходе
                .commit();
    }


    private void getPoints() {

        Call<String> call = questionAPI.getPoints("jwt-cookie=" + SharedPrefManager.getInstance(getActivity()).getToken().getAccess_token() +
                "; refresh-jwt-cookie=" + SharedPrefManager.getInstance(getActivity()).getToken().getRefresh_token());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                if(response.code()==403){

                }
                String points = response.body();
                mainThreadHandler.post(() -> txtPoints.setText(points));


            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("Ошибка времени", " ");
                // mainThreadHandler.post(() -> remainingTimeTextView.setText("Ошибка: " + t.getMessage()));

            }
        });
    }

    private void getProfile() {
        Call<UserData> call = RetrofitClient.getInstance(getActivity())
                .getUserAPI()
                .getProfile("jwt-cookie=" + SharedPrefManager.getInstance(getActivity()).getToken().getAccess_token() +
                        "; refresh-jwt-cookie=" + SharedPrefManager.getInstance(getActivity()).getToken().getRefresh_token());

        call.enqueue(new Callback<UserData>() {
            @Override
            public void onResponse(@NonNull Call<UserData> call, @NonNull Response<UserData> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserData user = response.body();
                    Log.e("Пользователь: ", response.body().toString());

                    // Обновление UI в основном потоке
                    requireActivity().runOnUiThread(() -> {
                        edtNick.setText(user.getNickname());
                        edtEmail.setText(user.getEmail());
                        edtPears.setText(String.valueOf(user.getPears()));
                        oldEmail = user.getEmail();  // Сохраняем старый email
                    });
                } else {
                    Log.e("Ошибка API", "Код ответа: " + response.code() + ", сообщение: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserData> call, @NonNull Throwable t) {
                Log.e("Ошибка сети", t.getMessage(), t);
            }
        });
    }

    @Override
    public void onClick(View v) {
        soundManager.playSound();
        int id = v.getId();

        if (id == R.id.add_image_btn) {
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
//            Fragment fragment = new UploadFragment();
//
//            transaction.replace(R.id.main_container, fragment);
            transaction.addToBackStack(null);

            transaction.commit();


        } else if (id == R.id.money) {
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
//            Fragment balanceFragment = new BalanceFragment();
//            transaction.replace(R.id.main_container, balanceFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        } else if (id == R.id.byu_fruits) {
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
//            Fragment buypearsFragment = new BuyPearsFragment();
//            transaction.replace(R.id.main_container, buypearsFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
        else if(id == R.id.auction_btn) {
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            Fragment auctionFragment = new AuctionFragment();
            transaction.replace(R.id.main_container, auctionFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }else if (id == R.id.exit_btn) {
            logout();
        }


    }

    private void logout() {
//        // Получаем SharedPreferences.Editor через SharedPrefManager
//        SharedPreferences.Editor editor = SharedPrefManager.getInstance(getActivity()).getSharedPreferences().edit();
//
//        // Удаляем данные, которые не касаются флага первого запуска
//        editor.remove("token");  // Пример удаления только токена
//        // Тут можешь очистить другие данные, которые нужно удалить
//        editor.apply();
        SharedPrefManager.getInstance(getActivity()).clear();

        // Завершаем активность
        getActivity().finishAffinity();
    }

    @Override
    public void onStop() {
        super.onStop();
        // Останавливаем планировщик, когда фрагмент больше не видим
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }
}
