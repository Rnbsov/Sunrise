package com.example.sunrise.fragments;

import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.sunrise.R;
import com.example.sunrise.adapters.TasksAdapter;
import com.example.sunrise.models.Task;
import com.example.sunrise.services.TaskService;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MyDayFragment extends Fragment {
    private View fragment;
    private RecyclerView tasksList;
    private TasksAdapter adapter;
    private TaskService taskService;

    // Views properties
    private BottomSheetDialog bottomSheetDialog;
    private TextInputLayout titleInputLayout;
    private TextInputEditText editTitle;
    private Chip priorityChip;
    private Button saveBtn;


    public MyDayFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragment = inflater.inflate(R.layout.fragment_my_day, container, false);

        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tasksList = fragment.findViewById(R.id.tasks_list);

        // Creating and setting linear layout manager to recyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        tasksList.setLayoutManager(layoutManager);

        // Initialize adapter
        adapter = new TasksAdapter(new ArrayList<>(), this::onCheckboxClickedListener, this::onTaskClickListener);
        tasksList.setAdapter(adapter);

        // Initialize TaskService
        taskService = new TaskService();

        // Fetch tasks
        fetchTasksFromDatabase();
    }

    private void fetchTasksFromDatabase() {
        ValueEventListener tasksListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Task> taskList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Task task = snapshot.getValue(Task.class);
                    taskList.add(task);
                }

                Log.d("Firebase listener", "Firebase listener");
                for (Task task : taskList) {
                    System.out.println(task);
                }
                // Sort tasks based on completion status
                sortTasksByCompletion(taskList);

                // Update data in the adapter using DiffUtil
                adapter.updateData(taskList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
                Log.e("MyDayFragment", "Error fetching tasks", databaseError.toException());
            }
        };

        // Call getTasks method from TaskService to register the listener
        taskService.getTasks(tasksListener);
    }

    /**
     * Sorts a list of tasks based on their completion status.
     * <p>
     * Completed tasks are sorted after uncompleted tasks. If both tasks have the same completion status,
     * their order remains unchanged.
     */
    private void sortTasksByCompletion(List<Task> taskList) {
        taskList.sort((task1, task2) -> {
            // Completed tasks should come after uncompleted tasks
            if (task1.isCompleted() && !task2.isCompleted()) {
                // If task1 is completed and task2 is uncompleted, task2 should come first
                return 1;
            } else if (!task1.isCompleted() && task2.isCompleted()) {
                // If task1 is uncompleted and task2 is completed, task1 should come first
                return -1;
            } else {
                // Otherwise, maintain the original order
                return 0;
            }
        });
    }

    private void onCheckboxClickedListener(Task task, TextView title, boolean isChecked) {
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

    private void onTaskClickListener(Task task) {
        Context context = requireContext();
        View fragmentRootView = requireView(); // Get the root view of the fragment

        // Setup bottom sheet dialog
        bottomSheetDialog = new BottomSheetDialog(context);
        View bottomSheetContentView = LayoutInflater.from(context).inflate(R.layout.edit_task_bottom_sheet, (ViewGroup) fragmentRootView, false);
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
        priorityChip.setOnClickListener(this::showPriorityPopupMenu);

        // Setting listener on save button
        saveBtn.setOnClickListener(view -> saveTask(task));
    }

    private void saveTask(Task task) {
        String title = Objects.requireNonNull(editTitle.getText()).toString();
        String priority = getPriorityValue(priorityChip.getText().toString());

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

    private void showPriorityPopupMenu(View view) {
        PopupMenu popup = new PopupMenu(requireContext(), view);
        popup.getMenuInflater().inflate(R.menu.priorities_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(this::onPriorityMenuItemClick);
        popup.show();
    }

    private boolean onPriorityMenuItemClick(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.priority_high) {
            priorityChip.setText(R.string.priority_high);
            return true;
        } else if (itemId == R.id.priority_medium) {
            priorityChip.setText(R.string.priority_medium);
            return true;
        } else if (itemId == R.id.priority_low) {
            priorityChip.setText(R.string.priority_low);
            return true;
        } else if (itemId == R.id.priority_regular) {
            priorityChip.setText(R.string.priority_regular);
            return true;
        } else {
            return false;
        }
    }

    private String getPriorityValue(String selectedPriority) {
        // Check if the selected priority is the default one
        if (selectedPriority.equals(getString(R.string.priority))) {
            // If so, set the priority to the regular
            return getString(R.string.priority_regular);
        } else {
            // Otherwise, return the selected priority
            return selectedPriority;
        }
    }

}

