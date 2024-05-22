package com.example.sunrise.helpers;

import android.content.Context;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.sunrise.R;
import com.example.sunrise.models.Category;
import com.example.sunrise.models.Task;
import com.example.sunrise.services.CategoryService;
import com.example.sunrise.services.TaskService;
import com.example.sunrise.utils.TaskUtils;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class TaskUpdateHelper {
    private final Context context;
    private final TaskService taskService;
    private final CategoryService categoryService;
    private BottomSheetDialog bottomSheetDialog;
    private TextInputLayout titleInputLayout;
    private TextInputEditText editTitle;
    private Chip priorityChip;
    private Chip categoryChip;
    private Button saveBtn;
    private String selectedCategoryId;

    public TaskUpdateHelper(Context context) {
        this.context = context;

        // Initialize TaskService and Category to interact with Firebase database
        this.taskService = new TaskService();
        this.categoryService = new CategoryService();
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
        // Fetch and set the category name
        fetchAndSetCategory(task.getCategoryId());

        // Change priority listener
        priorityChip.setOnClickListener(view -> TaskUtils.showPriorityPopupMenu(context, view, priorityChip));

        // Change category listener
        categoryChip = bottomSheetContentView.findViewById(R.id.category);
        categoryChip.setOnClickListener(view -> TaskUtils.showCategoriesPopupMenu(context, view, categoryChip, categoryService, this::onCategorySelected));

        // Setting listener on save button
        saveBtn.setOnClickListener(view -> saveTask(task));
    }

    private void onCategorySelected(String selectedCategoryId, String selectedCategoryName) {
        // Store it like class var
        this.selectedCategoryId = selectedCategoryId;

        // Set selected category's title to chip
        categoryChip.setText(selectedCategoryName);
    }

    private void saveTask(Task task) {
        String title = Objects.requireNonNull(editTitle.getText()).toString();
        String priority = TaskUtils.getPriorityValue(priorityChip.getText().toString(), context.getString(R.string.priority), context.getString((R.string.priority_regular)));

        // Some checks before updating task
        // Check if the title is empty and show an error if it is
        if (title.isEmpty()) {
            titleInputLayout.setError("Title cannot be empty");
            return;
        }

        // Create a copy of the original task
        Task updatedTask = new Task(task);

        // Update copy with user-entered data
        updatedTask.setTitle(title);
        updatedTask.setPriority(priority);
        updatedTask.setCategoryId(selectedCategoryId);

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


    private void fetchAndSetCategory(String categoryId) {
        categoryService.getCategoryById(categoryId, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Category category = snapshot.getValue(Category.class);
                if (category != null) {
                    selectedCategoryId = category.getCategoryId();
                    categoryChip.setText(category.getTitle());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("TaskListenerHelper", "Failed to get task's category object");
            }
        });
    }
}
