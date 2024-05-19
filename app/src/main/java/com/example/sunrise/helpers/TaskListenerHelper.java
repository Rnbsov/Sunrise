package com.example.sunrise.helpers;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.sunrise.R;
import com.example.sunrise.models.Task;
import com.example.sunrise.services.TaskService;
import com.example.sunrise.utils.TaskUtils;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class TaskListenerHelper {
    private final Context context;
    private final TaskService taskService;
    private BottomSheetDialog bottomSheetDialog;
    private TextInputLayout titleInputLayout;
    private TextInputEditText editTitle;
    private Chip priorityChip;
    private Button saveBtn;

    public TaskListenerHelper(Context context) {
        this.context = context;

        // Initialize TaskService and TagService to interact with Firebase database
        this.taskService = new TaskService();
    }

    public void onTaskClickListener(Task task) {
        // Setup bottom sheet dialog
        bottomSheetDialog = new BottomSheetDialog(context);
        View bottomSheetContentView = LayoutInflater.from(context).inflate(R.layout.edit_task_bottom_sheet, null);
        bottomSheetDialog.setContentView(bottomSheetContentView);
        bottomSheetDialog.show();

        // Find views
        titleInputLayout = bottomSheetContentView.findViewById(R.id.titleInputLayout);
        editTitle = bottomSheetContentView.findViewById(R.id.title);
        priorityChip = bottomSheetContentView.findViewById(R.id.priority);
        saveBtn = bottomSheetContentView.findViewById(R.id.create_btn);

        // Setting properties from task object
        editTitle.setText(task.getTitle());
        priorityChip.setText(task.getPriority());

        // Change priority listener
        priorityChip.setOnClickListener(view -> TaskUtils.showPriorityPopupMenu(context, view, priorityChip));

        // Setting listener on save button
        saveBtn.setOnClickListener(view -> saveTask(task));
    }

    private void saveTask(Task task) {
        String title = Objects.requireNonNull(editTitle.getText()).toString();
        String priority = TaskUtils.getPriorityValue(priorityChip.getText().toString(), context.getString(R.string.priority), context.getString((R.string.priority_regular)));

        // Some checks before updating task
        if (title.isEmpty()) {
            titleInputLayout.setError("Title cannot be empty");
            return;
        }

        // Create a copy of the original task
        Task updatedTask = new Task(task);

        // Update copy with user-entered data
        updatedTask.setTitle(title);
        updatedTask.setPriority(priority);

        // Save updated task to Firebase database
        taskService.updateTask(updatedTask);

        // Dismiss the bottom sheet dialog after task creation
        bottomSheetDialog.dismiss();
    }

    public void onCheckboxClickedListener(Task task, TextView title, boolean isChecked) {
        // Tick it
        task.setCompleted(isChecked);

        // Set completedAt
        if (isChecked) {
            task.setCompletedAt(System.currentTimeMillis()); // Set completion timestamp
        } else {
            task.setCompletedAt(0); // Reset completion timestamp
        }

        // Save updated task to Firebase
        taskService.updateTask(task);

        // Apply strikethrough style if the task is completed
        if (task.isCompleted()) {
            title.setPaintFlags(title.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            // If task is not completed, it shouldn't be applied strikethrough style
            title.setPaintFlags(title.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }
    }
}
