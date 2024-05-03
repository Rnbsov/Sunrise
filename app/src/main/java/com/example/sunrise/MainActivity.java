package com.example.sunrise;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.sunrise.models.Task;
import com.example.sunrise.services.TaskService;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private Chip priorityChip;
    private BottomSheetDialog bottomSheetDialog;
    TextInputEditText editTitle;
    TextInputLayout titleInputLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupNavigation();
        setupFabButton();
    }

    private void setupNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragments_container);

        assert navHostFragment != null : "navHostFragment should not be null";

        NavigationUI.setupWithNavController(bottomNavigationView,
                navHostFragment.getNavController());
    }

    private void setupFabButton() {
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(this::showTaskCreationDialog);
    }

    private void showTaskCreationDialog(View v) {
        bottomSheetDialog = new BottomSheetDialog(MainActivity.this);
            View bottomSheetContentView = LayoutInflater.from(MainActivity.this).inflate(R.layout.create_task_bottom_sheet, null);
        bottomSheetDialog.setContentView(bottomSheetContentView);
        bottomSheetDialog.show();

        titleInputLayout = bottomSheetContentView.findViewById(R.id.titleInputLayout);
        editTitle = bottomSheetContentView.findViewById(R.id.title);
        Button createBtn = bottomSheetContentView.findViewById(R.id.create_btn);

        priorityChip = bottomSheetContentView.findViewById(R.id.priority);
        priorityChip.setOnClickListener(this::showPriorityPopupMenu);

        createBtn.setOnClickListener(this::createTask);
    }

    private void showPriorityPopupMenu(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.getMenuInflater().inflate(R.menu.priorities_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(this::onPriorityMenuItemClick);
        popup.show();
    }

    private void createTask(View view) {
        String title = Objects.requireNonNull(editTitle.getText()).toString();
        String priority = getPriorityValue(priorityChip.getText().toString());
        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        if (title.isEmpty()) {
            titleInputLayout.setError("Please type title");
            return;
        }

        Task task = new Task(title, priority, userId);

        // Initialize TaskService to interact with Firebase database
        TaskService taskService = new TaskService();

        // Save the newly created task to Firebase database
        taskService.saveTask(task);

        // Dismiss the bottom sheet dialog after task creation
        bottomSheetDialog.dismiss();
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