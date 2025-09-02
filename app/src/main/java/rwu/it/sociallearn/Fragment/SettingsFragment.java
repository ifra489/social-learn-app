package rwu.it.sociallearn.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import rwu.it.sociallearn.LoginActivity;

import rwu.it.sociallearn.R;
import rwu.it.sociallearn.RegisterActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SettingsFragment extends Fragment {

    private EditText etUsername, etCurrentPassword, etNewPassword;
    private Button btnChangeUsername, btnChangePassword, btnPrivacyPolicy, btnLogout;
    private TextView btnDeleteAccount;
    private Switch switchDarkTheme;

    private FirebaseUser currentUser;

    private SharedPreferences preferences;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_settings_fragment, container, false);

        etUsername = view.findViewById(R.id.etUsername);
        etCurrentPassword = view.findViewById(R.id.etCurrentPassword);
        etNewPassword = view.findViewById(R.id.etNewPassword);

        btnChangeUsername = view.findViewById(R.id.btnChangeUsername);
        btnChangePassword = view.findViewById(R.id.btnChangePassword);
        btnPrivacyPolicy = view.findViewById(R.id.btnPrivacyPolicy);
        btnLogout = view.findViewById(R.id.btnLogout);
        btnDeleteAccount = view.findViewById(R.id.btnDeleteAccount); // TextView

        switchDarkTheme = view.findViewById(R.id.switchDarkTheme);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        preferences = requireActivity().getSharedPreferences("settings", getContext().MODE_PRIVATE);

        // Set switch according to saved theme
        boolean isDark = preferences.getBoolean("dark_theme", false);
        switchDarkTheme.setChecked(isDark);
        applyTheme(isDark);

        // Change Username
        btnChangeUsername.setOnClickListener(v -> {
            String newUsername = etUsername.getText().toString().trim();
            if (!newUsername.isEmpty()) {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Update Username")
                        .setMessage("Are you sure you want to update your username?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(currentUser.getUid())
                                    .child("name")
                                    .setValue(newUsername)
                                    .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Username updated", Toast.LENGTH_SHORT).show())
                                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                        })
                        .setNegativeButton("No", null)
                        .show();
            } else {
                Toast.makeText(getContext(), "Enter new username", Toast.LENGTH_SHORT).show();
            }
        });

        // Change Password
        btnChangePassword.setOnClickListener(v -> {
            String currentPass = etCurrentPassword.getText().toString().trim();
            String newPass = etNewPassword.getText().toString().trim();
            if (!currentPass.isEmpty() && !newPass.isEmpty()) {
                currentUser.reauthenticate(EmailAuthProvider.getCredential(currentUser.getEmail(), currentPass))
                        .addOnSuccessListener(aVoid -> {
                            currentUser.updatePassword(newPass)
                                    .addOnSuccessListener(aVoid1 -> Toast.makeText(getContext(), "Password updated", Toast.LENGTH_SHORT).show())
                                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                        })
                        .addOnFailureListener(e -> Toast.makeText(getContext(), "Current password is not correct", Toast.LENGTH_SHORT).show());
            } else {
                Toast.makeText(getContext(), "Fill both password fields", Toast.LENGTH_SHORT).show();
            }
        });

        // Dark Theme Toggle
        switchDarkTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferences.edit().putBoolean("dark_theme", isChecked).apply();
            applyTheme(isChecked);
        });

        // Privacy Policy
        btnPrivacyPolicy.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.home_fragment_container, new PrivacyPolicyFragment())
                    .addToBackStack(null)
                    .commit();
        });

        // Logout
        btnLogout.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to log out?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        FirebaseAuth.getInstance().signOut();

                        // Redirect to Login/Register screen and clear back stack
                        Intent intent = new Intent(getContext(), LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        requireActivity().finish();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        // Delete Account
        btnDeleteAccount.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Delete Account")
                    .setMessage("Are you sure you want to delete your account? This action cannot be undone.")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        currentUser.delete()
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(getContext(), "Account deleted", Toast.LENGTH_SHORT).show();

                                    // Redirect to Login/Register screen and clear back stack
                                    Intent intent = new Intent(getContext(), RegisterActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    requireActivity().finish();
                                })
                                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        return view;
    }

    private void applyTheme(boolean darkTheme) {
        if (darkTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}
