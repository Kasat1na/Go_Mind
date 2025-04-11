package com.example.gomind.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.gomind.R;
import com.google.android.material.button.MaterialButton;

public class EmailConfirmationFragment extends Fragment {

    private String email;
    private OnEmailChangeListener emailChangeListener;

    public interface OnEmailChangeListener {
        void onConfirmEmailChange(String newEmail);
        void onCancelEmailChange();
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setEmailChangeListener(OnEmailChangeListener listener) {
        this.emailChangeListener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.messagebox4, container, false);

        MaterialButton btnYes = view.findViewById(R.id.byu_fruits); // Да
        MaterialButton btnNo = view.findViewById(R.id.add); // Нет

        btnYes.setOnClickListener(v -> {
            if (emailChangeListener != null) {
                emailChangeListener.onConfirmEmailChange(email);
            }
            // Показываем EmailChangeFragment
            //showEmailChangeFragment();
        });

        btnNo.setOnClickListener(v -> {
            if (emailChangeListener != null) {
                emailChangeListener.onCancelEmailChange();
            }
            closeFragment();
        });

        return view;
    }

//    private void showEmailChangeFragment() {
//        // Создаем новый фрагмент и передаем туда email
//        EmailChangeFragment emailChangeFragment = new EmailChangeFragment();
//        emailChangeFragment.setEmail(email);
//
//        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
//        fragmentManager.beginTransaction()
//                .replace(R.id.main_container, emailChangeFragment) // Показываем EmailChangeFragment
//                .addToBackStack(null)
//                .commit();
//    }

    private void closeFragment() {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        fragmentManager.popBackStack(); // Закрытие фрагмента
    }
}
