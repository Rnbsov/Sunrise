package com.example.sunrise.helpers;

import static com.example.sunrise.utils.WorkspaceTaskUtils.showUsersPopupMenu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;

import com.example.sunrise.R;
import com.example.sunrise.models.WorkspaceTask;
import com.example.sunrise.services.WorkspaceTaskService;
import com.example.sunrise.utils.TaskUtils;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class WorkspaceTaskCreationHelper {
    private final Context context;
    private final String workspaceId;
    private final WorkspaceTaskService workspaceTaskService;
    private BottomSheetDialog bottomSheetDialog;
    private TextInputEditText editTitle;
    private TextInputLayout titleInputLayout;
    private Chip priorityChip;
    private Chip statusChip;
    private Chip assignChip;
    private String assignedUserId;

    public WorkspaceTaskCreationHelper(Context context, String workspaceId) {
        this.context = context;
        this.workspaceId = workspaceId;

        // Initialize services to interact with Firebase database
        this.workspaceTaskService = new WorkspaceTaskService();
    }

    public void showWorkspaceTaskCreationDialog(View v) {
        bottomSheetDialog = new BottomSheetDialog(context);
        View bottomSheetContentView = LayoutInflater.from(context).inflate(R.layout.create_workspace_task_bottom_sheet, null);
        bottomSheetDialog.setContentView(bottomSheetContentView);
        bottomSheetDialog.show();

        titleInputLayout = bottomSheetContentView.findViewById(R.id.titleInputLayout);
        editTitle = bottomSheetContentView.findViewById(R.id.title);
        Button createBtn = bottomSheetContentView.findViewById(R.id.create_btn);

        priorityChip = bottomSheetContentView.findViewById(R.id.priority);
        priorityChip.setOnClickListener(view -> TaskUtils.showPriorityPopupMenu(context, view, priorityChip));

        statusChip = bottomSheetContentView.findViewById(R.id.status);
        statusChip.setOnClickListener(view -> showStatusPopupMenu(context, view, statusChip));

        assignChip = bottomSheetContentView.findViewById(R.id.assign_chip);
        assignChip.setOnClickListener(view -> showUsersPopupMenu(context, view, workspaceId, this::onUserSelected));

        createBtn.setOnClickListener(this::createWorkspaceTask);
    }

    private void onUserSelected(String selectedUserId, String selectedUserName) {
        // Save it like class var
        this.assignedUserId = selectedUserId;

        // Set selected user's name to chip
        assignChip.setText(selectedUserName);
    }

    public static void showStatusPopupMenu(Context context, View anchorView, Chip statusChip) {
        PopupMenu popup = new PopupMenu(context, anchorView);
        popup.getMenuInflater().inflate(R.menu.status_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> onStatusMenuItemClick(item, statusChip));

        popup.show();
    }

    public static boolean onStatusMenuItemClick(@NonNull MenuItem item, Chip statusChip) {
        int itemId = item.getItemId();

        if (itemId == R.id.status_not_started) {
            statusChip.setText(R.string.status_not_started);
            return true;
        } else if (itemId == R.id.status_in_process) {
            statusChip.setText(R.string.status_in_process);
            return true;
        } else if (itemId == R.id.status_done) {
            statusChip.setText(R.string.status_done);
            return true;
        } else {
            return false;
        }
    }

    private void createWorkspaceTask(View view) {
        String title = Objects.requireNonNull(editTitle.getText()).toString().trim();
        String priority = priorityChip.getText().toString();
        String status = statusChip.getText().toString();

        // Check if the title is empty and show an error if it is
        if (title.isEmpty()) {
            titleInputLayout.setError("Please add title");
            return;
        }

        WorkspaceTask workspaceTask = new WorkspaceTask(title, status, priority, assignedUserId, workspaceId);

        // Save the newly created task to Firebase database
        workspaceTaskService.createWorkspaceTask(workspaceTask);

        // Dismiss the bottom sheet dialog after task creation
        bottomSheetDialog.dismiss();
    }
}

