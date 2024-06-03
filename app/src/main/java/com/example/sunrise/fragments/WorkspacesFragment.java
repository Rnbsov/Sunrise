package com.example.sunrise.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sunrise.R;
import com.example.sunrise.adapters.WorkspaceAdapter;
import com.example.sunrise.helpers.WorkspaceCreationHelper;
import com.example.sunrise.models.User;
import com.example.sunrise.models.Workspace;
import com.example.sunrise.services.UserService;
import com.example.sunrise.services.WorkspaceService;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;

public class WorkspacesFragment extends Fragment {

    private RecyclerView workspacesRecyclerView;
    private WorkspaceAdapter workspaceAdapter;
    private WorkspaceCreationHelper workspaceCreationHelper;
    private WorkspaceService workspaceService;

    public WorkspacesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_workspaces, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize WorkspacesService
        workspaceService = new WorkspaceService();

        // Initialize WorkspaceCreationHelper
        workspaceCreationHelper = new WorkspaceCreationHelper(requireContext());

        // Setup Workspaces RecyclerView
        setupWorkspacesRecyclerView(view);
        setupJoinButton(view); // Setup join workspace button

        // Fetch user participating workspaces
        fetchWorkspacesFromDatabase();
    }

    private void setupWorkspacesRecyclerView(View view) {
        // Initialize RecyclerView and adapter
        workspacesRecyclerView = view.findViewById(R.id.workspaces_recycler_view);
        workspacesRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        List<Workspace> workspaces = new ArrayList<>();

        workspaceAdapter = new WorkspaceAdapter(workspaces, this::onWorkspaceClick, this::onWorkspaceAddButtonClick);

        // Set adapter to RecyclerView
        workspacesRecyclerView.setAdapter(workspaceAdapter);
    }

    private void setupJoinButton(View view) {
        TextInputEditText workspaceInviteCodeInput = view.findViewById(R.id.workspace_invite_code_input);
        Button joinBtn = view.findViewById(R.id.join_btn);

        joinBtn.setOnClickListener(v -> {
            String inviteCode = workspaceInviteCodeInput.getText().toString().trim();
            if (!inviteCode.isEmpty()) {
                joinWorkspace(inviteCode);
                fetchWorkspacesFromDatabase();
            } else {
                Toast.makeText(requireContext(), "Invite code not exist or expired", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchWorkspacesFromDatabase() {
        // Get workspace IDs from the current user's workspaceIds field
        getCurrentUserWorkspaceIds(workspaceIds -> {
            // Use WorkspaceService to retrieve workspaces by their IDs
            workspaceService.retrieveWorkspacesByIds(workspaceIds, new WorkspaceService.WorkspacesListener() {
                @Override
                public void onWorkspacesRetrieved(List<Workspace> workspaces) {
                    // Update the RecyclerView with the fetched workspaces
                    workspaceAdapter.setWorkspaces(workspaces);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("WorkspacesFragment", "Failed to fetch workspaces: " + databaseError.getMessage());
                    Toast.makeText(requireContext(), "Failed to fetch workspaces: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void getCurrentUserWorkspaceIds(WorkspaceIdsListener listener) {
        UserService userService = new UserService();

        userService.getCurrentUser(new UserService.CurrentUserListener() {
            @Override
            public void onCurrentUserRetrieved(User user) {
                if (user != null && user.getWorkspaceIds() != null) {
                    listener.onWorkspaceIdsRetrieved(user.getWorkspaceIds());
                } else {
                    listener.onWorkspaceIdsRetrieved(new ArrayList<>());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("WorkspacesFragment", "Failed to get current user workspaces list: " + databaseError.getMessage());
            }
        });
    }

    /**
     * Interface for retrieving workspace IDs
     */
    public interface WorkspaceIdsListener {
        void onWorkspaceIdsRetrieved(List<String> workspaceIds);
    }

    /**
     * Handle workspace item click
     *
     * @param workspace clicked workspace object
     */
    private void onWorkspaceClick(Workspace workspace) {
        Bundle bundle = new Bundle();
        bundle.putString("workspaceId", workspace.getWorkspaceId());
        bundle.putString("workspaceTitle", workspace.getTitle());

        NavController navController = Navigation.findNavController(requireView());
        navController.navigate(R.id.action_page_workspaces_to_workspaceFragment, bundle);
    }

    private void onWorkspaceAddButtonClick(View v) {
        workspaceCreationHelper.showWorkspaceCreationDialog((ViewGroup) requireView());
    }

    private void joinWorkspace(String inviteCode) {
        UserService userService = new UserService();
        userService.getCurrentUser(new UserService.CurrentUserListener() {
            @Override
            public void onCurrentUserRetrieved(User user) {
                if (user != null) {
                    workspaceService.joinWorkspace(inviteCode, user.getUserId());
                } else {
                    Log.e("WorkspacesFragment", "Current user is null");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("WorkspacesFragment", "Failed to get current user: " + databaseError.getMessage());
            }
        });
    }
}
