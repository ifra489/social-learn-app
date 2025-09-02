package rwu.it.sociallearn;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import rwu.it.sociallearn.Fragment.ChatContainerFragment;
import rwu.it.sociallearn.Fragment.AllChatsFragment;
import rwu.it.sociallearn.Fragment.ProfileFragment;
import rwu.it.sociallearn.Fragment.QuizFragment;
import rwu.it.sociallearn.Fragment.SettingsFragment;
import rwu.it.sociallearn.Fragment.TaskFragment;
import rwu.it.sociallearn.Fragment.TaskHomeFragment;
import rwu.it.sociallearn.R;

public class HomeActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Default Fragment
        loadFragment(new QuizFragment());

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            int itemId = item.getItemId();

            if (itemId == R.id.nav_quiz) {
                selectedFragment = new QuizFragment();
            } else if (itemId == R.id.nav_tasks) {
                selectedFragment = new TaskHomeFragment();
            } else if (itemId == R.id.nav_chat) {
                selectedFragment = new ChatContainerFragment();
            } else if (itemId == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            } else if (itemId == R.id.nav_settings) {
                selectedFragment = new SettingsFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
                return true;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.home_fragment_container, fragment)
                .commit();
    }
}
