package rwu.it.sociallearn.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import rwu.it.sociallearn.R;

public class QuizResultFragment extends Fragment {

    private static final String ARG_SCORE = "score";
    private static final String ARG_TOTAL = "total";

    public static QuizResultFragment newInstance(int score, int total) {
        QuizResultFragment fragment = new QuizResultFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SCORE, score);
        args.putInt(ARG_TOTAL, total);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_quiz_result_fragment, container, false);

        TextView tvResult = view.findViewById(R.id.tv_result);

        int score = getArguments().getInt(ARG_SCORE);
        int total = getArguments().getInt(ARG_TOTAL);

        tvResult.setText("Your Score: " + score + " / " + total);

        // ✅ Save score to Firebase
        saveScoreToFirebase(score, total);

        return view;
    }

    private void saveScoreToFirebase(int score, int total) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users")
                .child(uid)
                .child("quiz_history");

        String quizId = ref.push().getKey(); // unique ID for each quiz attempt

        HashMap<String, Object> quizData = new HashMap<>();
        quizData.put("score", score);
        quizData.put("total", total);
        quizData.put("timestamp", System.currentTimeMillis());

        ref.child(quizId).setValue(quizData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getActivity(), "Score saved!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Failed to save score: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
