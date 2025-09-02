package rwu.it.sociallearn.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

import rwu.it.sociallearn.R;

public class ProfileFragment extends Fragment {

    private TextView tvName, tvEmail, tvTaskCount, tvQuizHistory;
    private ImageView ivProfile;
    private FirebaseAuth auth;
    private DatabaseReference userRef;
    private StorageReference storageReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_profile_fragment, container, false);

        // Init views
        tvName = view.findViewById(R.id.tvName);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvTaskCount = view.findViewById(R.id.tvTaskCount);
        tvQuizHistory = view.findViewById(R.id.tvQuizHistory);
        ivProfile = view.findViewById(R.id.ivProfile);

        // Firebase
        auth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("profile_images");
        String userId = auth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);

        loadUserData();

        return view;
    }

    private void loadUserData() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Get values from Firebase
                    String name = snapshot.child("name").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    Long taskCount = snapshot.child("taskCount").getValue(Long.class);
                    String quizHistory = snapshot.child("quizHistory").getValue(String.class);
                    String profileImageUrl = snapshot.child("profileImageUrl").getValue(String.class);

                    // Set values in UI
                    tvName.setText(name != null ? name : "No Name");
                    tvEmail.setText(email != null ? email : "No Email");
                    tvTaskCount.setText("Tasks Completed: " + (taskCount != null ? taskCount : 0));
                    tvQuizHistory.setText("Quiz: " + (quizHistory != null ? quizHistory : "Not attempted"));

                    // Profile Image
                    if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                        Glide.with(getContext())
                                .load(profileImageUrl)
                                .placeholder(R.drawable.ic_male) // fallback image
                                .into(ivProfile);
                    } else {
                        // Default image
                        ivProfile.setImageResource(R.drawable.ic_male);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load profile", Toast.LENGTH_SHORT).show();
            }
        });
    }
}