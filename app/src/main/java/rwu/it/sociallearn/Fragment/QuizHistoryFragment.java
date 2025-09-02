package rwu.it.sociallearn.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

import rwu.it.sociallearn.Quiz.QuizHistoryAdapter;
import rwu.it.sociallearn.Quiz.QuizHistory;
import rwu.it.sociallearn.R;

public class QuizHistoryFragment extends Fragment {

    private RecyclerView rvQuizHistory;
    private QuizHistoryAdapter adapter;
    private List<QuizHistory> historyList;

    private DatabaseReference ref;
    private String uid;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_quiz_history_fragment, container, false);

        rvQuizHistory = view.findViewById(R.id.rvQuizHistory);
        rvQuizHistory.setLayoutManager(new LinearLayoutManager(getContext()));

        historyList = new ArrayList<>();
        adapter = new QuizHistoryAdapter(historyList);
        rvQuizHistory.setAdapter(adapter);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ref = FirebaseDatabase.getInstance().getReference("Users").child(uid).child("quiz_history");

        loadHistory();

        return view;
    }

    private void loadHistory() {
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                historyList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    QuizHistory item = ds.getValue(QuizHistory.class);
                    if (item != null) historyList.add(item);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load history: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
