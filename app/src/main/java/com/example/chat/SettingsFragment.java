package com.example.chat;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;

public class SettingsFragment extends Fragment {
    private Switch themeSwitch;
    private Button logoutButton;

    public SettingsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        themeSwitch = view.findViewById(R.id.theme_switch);
        logoutButton = view.findViewById(R.id.logout_button);

        // Dark Mode Toggle
        SharedPreferences prefs = getActivity().getSharedPreferences("settings", 0);
        boolean darkMode = prefs.getBoolean("dark_mode", false);
        themeSwitch.setChecked(darkMode);
        applyTheme(darkMode);

        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("dark_mode", isChecked);
            editor.apply();
            applyTheme(isChecked);
        });

        // Logout Button
        logoutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            getActivity().finish(); // ऐप बंद कर दें
        });

        return view;
    }

    private void applyTheme(boolean darkMode) {
        if (darkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}
