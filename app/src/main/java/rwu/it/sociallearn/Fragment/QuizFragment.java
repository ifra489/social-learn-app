package rwu.it.sociallearn.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import rwu.it.sociallearn.R;

public class QuizFragment extends Fragment {

    private Button btnStartQuiz, btnViewHistory;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quiz, container, false);

        btnStartQuiz = view.findViewById(R.id.btn_start_quiz);
        btnViewHistory = view.findViewById(R.id.btnViewHistory);

        // Start Quiz button → open QuizQuestionFragment
        btnStartQuiz.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.home_fragment_container, new QuizQuestionFragment())
                    .addToBackStack(null)
                    .commit();
        });

        // View History button → open QuizHistoryFragment
        btnViewHistory.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.home_fragment_container, new QuizHistoryFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }
}
