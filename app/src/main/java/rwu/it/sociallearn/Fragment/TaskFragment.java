package rwu.it.sociallearn.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import rwu.it.sociallearn.AddTaskActivity;
import rwu.it.sociallearn.R;
import rwu.it.sociallearn.Task_Module.Task;
import rwu.it.sociallearn.Task_Module.TaskAdapter;

public class TaskFragment extends Fragment {

    private RecyclerView recyclerView;
    private TaskAdapter adapter;
    private List<Task> taskList;
    private DatabaseReference taskRef;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tasks, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewTasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        taskList = new ArrayList<>();

        // ✅ Adapter with OnTaskActionListener
        adapter = new TaskAdapter(taskList, new TaskAdapter.OnTaskActionListener() {
            @Override
            public void onEdit(Task task) {
                // Edit ka intent open karo
                Intent intent = new Intent(getContext(), AddTaskActivity.class);
                intent.putExtra("taskId", task.getTaskId());
                startActivity(intent);
            }

            @Override
            public void onDelete(Task task) {
                if (task.getTaskId() != null) {
                    taskRef.child(task.getTaskId()).removeValue();
                }
            }

            @Override
            public void onStatusChange(Task task, String newStatus) {
                if (task.getTaskId() != null) {
                    taskRef.child(task.getTaskId()).child("status").setValue(newStatus);
                }
            }
        });

        recyclerView.setAdapter(adapter);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        taskRef = FirebaseDatabase.getInstance()
                .getReference("Tasks")
                .child(userId);

        loadTasks();

        FloatingActionButton fabAddTask = view.findViewById(R.id.fabAddTask);
        fabAddTask.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), AddTaskActivity.class));
        });

        return view;
    }

    private void loadTasks() {
        taskRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                taskList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Task task = ds.getValue(Task.class);
                    if (task != null) {
                        taskList.add(task);
                    }
                }
                adapter.updateList(taskList); // ✅ refresh after fetching
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
}
