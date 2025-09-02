package rwu.it.sociallearn.Fragment;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rwu.it.sociallearn.Quiz.Question;
import rwu.it.sociallearn.R;

public class QuizQuestionFragment extends Fragment {

    private TextView tvQuestion, tvTimer, tvQuestionNumber;
    private RadioGroup rgOptions;
    private RadioButton rbA, rbB, rbC, rbD;
    private Button btnNext;

    private List<Question> quizList;
    private int currentIndex = 0;
    private int score = 0;

    private CountDownTimer countDownTimer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_quiz_questions_fragment, container, false);

        tvQuestionNumber = view.findViewById(R.id.tvQuestionsNumber);
        tvQuestion = view.findViewById(R.id.tvQuestion);
        tvTimer = view.findViewById(R.id.tvTimer);
        rgOptions = view.findViewById(R.id.rgOptions);
        rbA = view.findViewById(R.id.rbOptionA);
        rbB = view.findViewById(R.id.rbOptionB);
        rbC = view.findViewById(R.id.rbOptionC);
        rbD = view.findViewById(R.id.rbOptionD);
        btnNext = view.findViewById(R.id.btnNext);

        loadQuizData();
        showQuestion();

        btnNext.setOnClickListener(v -> nextQuestion());

        return view;
    }

    private void loadQuizData() {
        quizList = new ArrayList<>();
        quizList.add(new Question("Blue dwarfs’ and ‘Red giants’ respectively refer to?", " Moons of Jupiter", "Young star and Old star", "Old star and Young star", "None of these", 3));
        quizList.add(new Question("Capital of Pakistan?", "Islamabad", "Karachi", "Lahore", "Peshawar", 1));
        quizList.add(new Question("Which continent was historically referred to as the ‘Dark Continent’?", " Europe", " Africa", "Asia", "Australia", 2));
        quizList.add(new Question("What is the largest type of shark?", " Hammerhead Shark", "Tiger Shark", "Great White Shark", "Bull Shark", 3));
        quizList.add(new Question("Which planet is known for its beautiful rings?", "Mars", " Jupiter", "Uranus", "Saturn", 4));
    }

    private void showQuestion() {
        if (currentIndex >= quizList.size()) {
            saveScoreToFirebase(score, quizList.size());
            showResult();
            return;
        }

        Question current = quizList.get(currentIndex);

        // Show question number
        tvQuestionNumber.setText("Question " + (currentIndex + 1) );

        tvQuestion.setText(current.getQuestion());
        rbA.setText(current.getOption1());
        rbB.setText(current.getOption2());
        rbC.setText(current.getOption3());
        rbD.setText(current.getOption4());

        rgOptions.clearCheck(); // Clear previous selection
        btnNext.setEnabled(false); // Disable Next until selection or timer ends

        // Enable Next when an option is selected
        rgOptions.setOnCheckedChangeListener((group, checkedId) -> btnNext.setEnabled(true));

        startTimer();
    }

    private void startTimer() {
        if (countDownTimer != null) countDownTimer.cancel();

        countDownTimer = new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvTimer.setText("Time: " + millisUntilFinished / 1000 + "s");
            }

            @Override
            public void onFinish() {
                btnNext.setEnabled(true); // Ensure next is enabled
                nextQuestion();
            }
        }.start();
    }

    private void nextQuestion() {
        if (countDownTimer != null) countDownTimer.cancel();

        Question current = quizList.get(currentIndex);
        int selectedId = rgOptions.getCheckedRadioButtonId();
        int selectedOption = 0;
        if (selectedId == rbA.getId()) selectedOption = 1;
        else if (selectedId == rbB.getId()) selectedOption = 2;
        else if (selectedId == rbC.getId()) selectedOption = 3;
        else if (selectedId == rbD.getId()) selectedOption = 4;

        if (selectedOption == current.getCorrectOption()) score++;

        currentIndex++;
        showQuestion();
    }

    private void showResult() {
        getParentFragmentManager().beginTransaction()
                .replace(R.id.home_fragment_container, QuizResultFragment.newInstance(score, quizList.size()))
                .commit();
    }

    private void saveScoreToFirebase(int score, int total) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users")
                .child(uid)
                .child("quiz_history");

        String quizId = ref.push().getKey();

        HashMap<String, Object> quizData = new HashMap<>();
        quizData.put("score", score);
        quizData.put("total", total);
        quizData.put("timestamp", System.currentTimeMillis());

        ref.child(quizId).setValue(quizData)
                .addOnSuccessListener(aVoid -> Toast.makeText(getActivity(), "Score saved!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getActivity(), "Failed to save score: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
