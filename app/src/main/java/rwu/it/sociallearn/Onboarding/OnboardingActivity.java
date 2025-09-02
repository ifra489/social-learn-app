package rwu.it.sociallearn.Onboarding;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.LoadAdError;

import java.util.ArrayList;
import java.util.List;

import rwu.it.sociallearn.R;
import rwu.it.sociallearn.RegisterActivity;

public class OnboardingActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private LinearLayout layoutDots;
    private Button btnNext, btnSkip, btnFinish;
    private OnboardingAdapter adapter;
    private AdView bannerAdView;
    private InterstitialAd mInterstitialAd;


    private int[] images = {
            R.drawable.ic_welcome,
            R.drawable.ic_features,
            R.drawable.ic_privacy
    };

    private String[] titles = {
            "Welcome",
            "Features",
            "Privacy Policy"
    };

    private String[] descriptions = {
            "Welcome to SocialLearn App! Let's get started on your learning journey.",
            "✔ Take interactive quizzes with timers and leaderboards.\n" +
                    "✔ Manage your tasks with status filters (Pending, Ongoing, Finished).\n" +
                    "✔ Chat with friends in real-time.\n" +
                    "✔ Track your learning progress and achievements.",
            "We respect your privacy and keep your data secure.\n\n" +
                    "By continuing, you agree to our Terms & Privacy Policy.\n" +
                    "Your information will only be used to improve your experience."
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        viewPager = findViewById(R.id.viewPager);
        layoutDots = findViewById(R.id.layoutDots);
        btnNext = findViewById(R.id.btnNext);
        btnSkip = findViewById(R.id.btnSkip);
        btnFinish = findViewById(R.id.btnFinish);
        bannerAdView = findViewById(R.id.adView);

        // Load banner ad
        AdRequest adRequest = new AdRequest.Builder().build();
        bannerAdView.loadAd(adRequest);

        // Load interstitial ad
        com.google.android.gms.ads.interstitial.InterstitialAd.load(this,
                "ca-app-pub-3940256099942544/1033173712", // Test ID
                adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(InterstitialAd interstitialAd) {
                        mInterstitialAd = interstitialAd;
                    }

                    @Override
                    public void onAdFailedToLoad(LoadAdError loadAdError) {
                        mInterstitialAd = null;
                    }
                });

        setupOnboardingItems();

        btnNext.setOnClickListener(v -> {
            if (viewPager.getCurrentItem() + 1 < adapter.getItemCount()) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            }
        });

        btnSkip.setOnClickListener(v -> startRegisterActivity());

        btnFinish.setOnClickListener(v -> {
            if (mInterstitialAd != null) {
                mInterstitialAd.show(OnboardingActivity.this);
                mInterstitialAd.setFullScreenContentCallback(new com.google.android.gms.ads.FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        startRegisterActivity();
                    }
                });
            } else {
                startRegisterActivity();
            }
        });
    }

    private void setupOnboardingItems() {
        List<OnboardingItem> items = new ArrayList<>();
        for (int i = 0; i < titles.length; i++) {
            items.add(new OnboardingItem(images[i], titles[i], descriptions[i]));
        }

        adapter = new OnboardingAdapter(items);
        viewPager.setAdapter(adapter);

        // Last page pe Finish button show
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                if (position == adapter.getItemCount() - 1) {
                    btnNext.setVisibility(View.GONE);
                    btnFinish.setVisibility(View.VISIBLE);
                } else {
                    btnNext.setVisibility(View.VISIBLE);
                    btnFinish.setVisibility(View.GONE);
                }
            }
        });
    }

    private void startRegisterActivity() {
        Intent intent = new Intent(OnboardingActivity.this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }
}
