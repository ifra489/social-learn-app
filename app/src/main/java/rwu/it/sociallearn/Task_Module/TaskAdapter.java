package rwu.it.sociallearn.Task_Module;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import rwu.it.sociallearn.R;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> taskList;
    private List<Task> filteredList;
    private OnTaskActionListener listener;

    public interface OnTaskActionListener {
        void onEdit(Task task);
        void onDelete(Task task);
        void onStatusChange(Task task, String newStatus);
    }

    public TaskAdapter(List<Task> taskList, OnTaskActionListener listener) {
        this.taskList = taskList;
        this.filteredList = new ArrayList<>(taskList);
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = filteredList.get(position);
        holder.tvTitle.setText(task.getTitle());
        holder.tvDescription.setText(task.getDescription());
        holder.tvStatus.setText("Status: " + task.getStatus());

        // ✅ Mark button text update based on status
        if ("Completed".equals(task.getStatus())) {
            holder.btnMark.setText("Unfinished");
        } else {
            holder.btnMark.setText("Complete");
        }

        // ✅ Mark complete/unfinished
        holder.btnMark.setOnClickListener(v -> {
            String newStatus = task.getStatus().equals("Completed") ? "Pending" : "Completed";
            listener.onStatusChange(task, newStatus);
        });

        // ✅ Edit task
        holder.btnEdit.setOnClickListener(v -> listener.onEdit(task));

        // ✅ Delete task with confirmation
        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(v.getContext())
                    .setTitle("Delete Task")
                    .setMessage("Are you sure you want to delete this task?")
                    .setPositiveButton("Yes", (dialog, which) -> listener.onDelete(task))
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public void filter(String status) {
        filteredList.clear();
        if (status.equals("All")) {
            filteredList.addAll(taskList);
        } else {
            for (Task t : taskList) {
                if (t.getStatus().equals(status)) {
                    filteredList.add(t);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void updateList(List<Task> newList) {
        this.taskList = newList;
        this.filteredList = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription, tvStatus;
        Button btnMark, btnEdit, btnDelete;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnMark = itemView.findViewById(R.id.btnComplete);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
