package rwu.it.sociallearn;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

import rwu.it.sociallearn.Task_Module.Task;

public class AddTaskActivity extends AppCompatActivity {

    private EditText etTitle, etDescription, etStatus;
    private Spinner spPriority;
    private Button btnSave, btnSelectDate;

    private String selectedDate = ""; // stores selected date

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        etStatus = findViewById(R.id.etStatus);
        spPriority = findViewById(R.id.spPriority);
        btnSave = findViewById(R.id.btnSave);
        btnSelectDate = findViewById(R.id.btnSelectDate);

        // Priority Spinner setup
        String[] priorities = {"Low", "Medium", "High"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, priorities);
        spPriority.setAdapter(adapter);

        // Status is always Pending
        etStatus.setText("Pending");

        // Date Picker
        btnSelectDate.setOnClickListener(v -> showDatePicker());

        btnSave.setOnClickListener(v -> saveTask());
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                AddTaskActivity.this,
                (view, year, month, dayOfMonth) -> {
                    // Month is 0-based, add 1
                    calendar.set(year, month, dayOfMonth);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    selectedDate = sdf.format(calendar.getTime());
                    btnSelectDate.setText(selectedDate); // show selected date
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.show();
    }

    private void saveTask() {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String priority = spPriority.getSelectedItem().toString();
        String status = etStatus.getText().toString().trim();

        if (TextUtils.isEmpty(title)) {
            Toast.makeText(this, "Please enter task title", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(selectedDate)) {
            Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String taskId = UUID.randomUUID().toString();

        // Create Task object
        Task task = new Task(taskId, title, description, selectedDate, priority, status);

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("Tasks")
                .child(userId);

        ref.child(taskId).setValue(task).addOnCompleteListener(taskSave -> {
            if (taskSave.isSuccessful()) {
                Toast.makeText(this, "Task Saved", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to save task", Toast.LENGTH_SHORT).show();
            }
        });
    }
}