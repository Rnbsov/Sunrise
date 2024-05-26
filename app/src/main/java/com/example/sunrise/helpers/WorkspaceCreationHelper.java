package com.example.sunrise.helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.sunrise.R;
import com.example.sunrise.models.Workspace;
import com.example.sunrise.services.WorkspaceService;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WorkspaceCreationHelper {

    private final Context context;
    private final WorkspaceService workspaceService;
    private BottomSheetDialog createWorkspaceBottomSheetDialog;
    private TextInputLayout titleInputLayout;
    private TextInputEditText editTitle;

    public WorkspaceCreationHelper(Context context) {
        this.context = context;
        this.workspaceService = new WorkspaceService();
    }

    public void showWorkspaceCreationDialog(ViewGroup parent) {
        // Setup bottom sheet dialog
        createWorkspaceBottomSheetDialog = new BottomSheetDialog(context);
        View bottomSheetContentView = LayoutInflater.from(context).inflate(R.layout.create_workspace_bottom_sheet, (ViewGroup) parent, false);
        createWorkspaceBottomSheetDialog.setContentView(bottomSheetContentView);
        createWorkspaceBottomSheetDialog.show();

        // Find views
        titleInputLayout = bottomSheetContentView.findViewById(R.id.workspace_title_input_layout);
        editTitle = bottomSheetContentView.findViewById(R.id.workspace_title);
        Button createBtn = bottomSheetContentView.findViewById(R.id.create_btn);

        // Setting listener on save button
        createBtn.setOnClickListener(view -> createWorkspace());
    }

    private void createWorkspace() {
        String title = Objects.requireNonNull(editTitle.getText()).toString();
        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        // Some checks before creating workspace
        if (title.isEmpty()) {
            titleInputLayout.setError("Please type title");
            return;
        }

        // Create a new Workspace object
        List<String> membersIds = new ArrayList<>();
        List<String> adminIds = new ArrayList<>();

        membersIds.add(userId); // Add current user as member of the workspace
        adminIds.add(userId); // Add current user as an admin

        Workspace workspace = new Workspace(title, membersIds, adminIds);

        // Save the newly created workspace to Firebase database
        workspaceService.createWorkspace(workspace);

        // Dismiss the bottom sheet dialog after workspace creation
        createWorkspaceBottomSheetDialog.dismiss();
    }
}
