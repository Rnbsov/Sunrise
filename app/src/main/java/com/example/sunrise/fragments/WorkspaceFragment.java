package com.example.sunrise.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sunrise.R;
import com.example.sunrise.adapters.MembersAdapter;
import com.example.sunrise.adapters.WorkspaceTaskAdapter;
import com.example.sunrise.helpers.WorkspaceTaskCreationHelper;
import com.example.sunrise.models.User;
import com.example.sunrise.models.Workspace;
import com.example.sunrise.models.WorkspaceTask;
import com.example.sunrise.services.UserService;
import com.example.sunrise.services.WorkspaceService;
import com.example.sunrise.services.WorkspaceTaskService;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WorkspaceFragment extends Fragment {

    private String workspaceId;
    private String workspaceTitle;
    private List<String> workspaceAdminIds;
    private String creatorId;
    private WorkspaceTaskAdapter workspaceTasksAdapter;
    private WorkspaceTaskService workspaceTaskService;

    public WorkspaceFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_workspace, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Retrieve the workspace id and title from arguments
        retrieveArguments();

        // Set action bar title to workspace title
        setActionBarTitle();

        // Fetch workspace details to get admin IDs and creator ID
        fetchWorkspaceDetails();

        // Setup menu options
        setupMenu();

        // Setup fab
        setupExtendedFabButton(view);

        // Initialize WorkspaceTaskService
        workspaceTaskService = new WorkspaceTaskService();

        // Setup RecyclerView
        setupRecyclerView(view);
    }

    /**
     * This method retrieve workspaceId and workspaceTitle from arguments
     * and save them as class properties {@code workspaceId} and {@code workspaceTitle}.
     */
    private void retrieveArguments() {
        if (getArguments() != null) {
            workspaceId = getArguments().getString("workspaceId");
            workspaceTitle = getArguments().getString("workspaceTitle");
        }
    }

    /**
     * Sets up the action bar title to display the workspace title.
     */
    private void setActionBarTitle() {
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setTitle(workspaceTitle);
    }

    private void fetchWorkspaceDetails() {
        WorkspaceService workspaceService = new WorkspaceService();
        workspaceService.getWorkspaceById(workspaceId, new WorkspaceService.WorkspaceRetrievedListener() {
            @Override
            public void onWorkspaceRetrieved(Workspace workspace) {
                if (workspace != null) {
                    creatorId = workspace.getCreatorId();
                    workspaceAdminIds = workspace.getWorkspaceAdminIds();
                } else {
                    Log.e("WorkspaceFragment", "Workspace not found");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("WorkspaceFragment", "Failed to retrieve workspace: " + databaseError.getMessage());
            }
        });
    }

    private void setupMenu() {
        MenuHost menuHost = requireActivity();
        menuHost.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.workspace_fragment_menu, menu);

                // Show or hide admin-specific menu items
                UserService userService = new UserService();
                userService.getCurrentUser(new UserService.CurrentUserListener() {
                    @Override
                    public void onCurrentUserRetrieved(User user) {
                        if (user != null && workspaceAdminIds != null && workspaceAdminIds.contains(user.getUserId())) {
                            menu.findItem(R.id.edit_workspace).setVisible(true);
                            menu.findItem(R.id.invite_codes).setVisible(true);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("WorkspaceFragment", "Failed to get current user: " + databaseError.getMessage());
                    }
                });
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                int itemId = menuItem.getItemId();
                if (itemId == R.id.members_list) {
                    openMembersBottomSheet();
                    return true;
                } else if (itemId == R.id.edit_workspace) {
                    Toast.makeText(requireActivity(), "Edit Workspace selected", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (itemId == R.id.invite_codes) {
                    Toast.makeText(requireActivity(), "Invite Codes selected", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    /**
     * Opens the bottom sheet to display workspace members.
     */
    private void openMembersBottomSheet() {
        // Inflate the bottom sheet layout
        View bottomSheetView = LayoutInflater.from(requireContext()).inflate(R.layout.members_list_bottom_sheet, null);

        // Initialize RecyclerView
        RecyclerView membersRecyclerView = bottomSheetView.findViewById(R.id.members_recycler_view);
        membersRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Create adapter
        MembersAdapter membersAdapter = new MembersAdapter(new ArrayList<>(), creatorId, workspaceAdminIds);
        membersRecyclerView.setAdapter(membersAdapter);

        // Fetch workspace members
        WorkspaceService workspaceService = new WorkspaceService();
        workspaceService.getWorkspaceMembers(workspaceId, users -> {
            // Update data in the adapter
            membersAdapter.setMembers(users);
        });

        // Create and show the bottom sheet dialog
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    /**
     * When the FAB is clicked, it opens a dialog for creating a new workspace task.
     *
     * @param view The root view of the fragment.
     */
    private void setupExtendedFabButton(View view) {
        ExtendedFloatingActionButton extendedFab = view.findViewById(R.id.extendedFab);

        extendedFab.setOnClickListener(v -> {
            // Instantiate WorkspaceTaskCreationHelper and show task creation dialog
            WorkspaceTaskCreationHelper taskCreationHelper = new WorkspaceTaskCreationHelper(requireContext(), workspaceId);
            taskCreationHelper.showWorkspaceTaskCreationDialog(view);
        });
    }

    /**
     * Sets up the RecyclerView to display workspace tasks.
     *
     * @param view The root view of the fragment.
     */
    private void setupRecyclerView(View view) {
        // Initialize RecyclerView and adapter
        RecyclerView workspaceTasksRecyclerView = view.findViewById(R.id.workspace_tasks_recycler_view);
        workspaceTasksRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        workspaceTasksAdapter = new WorkspaceTaskAdapter(new ArrayList<>(), this::onWorkspaceTaskClick);

        // Set adapter to RecyclerView
        workspaceTasksRecyclerView.setAdapter(workspaceTasksAdapter);

        // Fetch workspace tasks
        fetchWorkspaceTasksFromDatabase();
    }

    private void fetchWorkspaceTasksFromDatabase() {
        workspaceTaskService.getWorkspaceTasks(workspaceId, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<WorkspaceTask> workspaceTasks = new ArrayList<>();
                for (DataSnapshot taskSnapshot : snapshot.getChildren()) {
                    WorkspaceTask workspaceTask = taskSnapshot.getValue(WorkspaceTask.class);
                    if (workspaceTask != null) {
                        workspaceTasks.add(workspaceTask);
                    }
                }

                // Update data in the adapter
                workspaceTasksAdapter.updateData(workspaceTasks);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("WorkspaceFragment", "Error fetching workspace tasks", databaseError.toException());
            }
        });
    }

    /**
     * Handles workspace task item click.
     *
     * @param workspaceTask Clicked workspace task.
     */
    private void onWorkspaceTaskClick(WorkspaceTask workspaceTask) {
        Toast.makeText(requireContext(), "task clicked", Toast.LENGTH_SHORT).show();
    }
}