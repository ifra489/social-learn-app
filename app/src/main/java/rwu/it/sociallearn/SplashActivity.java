package rwu.it.sociallearn;

import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;




import android.content.Intent;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;

import rwu.it.sociallearn.Onboarding.OnboardingActivity;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 2000; // 2 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(() -> {
            // Check user login status
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                // User logged in → Go to Main Activity
                startActivity(new Intent(SplashActivity.this, HomeActivity.class));
            } else {
                // Not logged in → Go to Onboarding
                startActivity(new Intent(SplashActivity.this, OnboardingActivity.class));
            }
            finish();
        }, SPLASH_DELAY);
    }
}
