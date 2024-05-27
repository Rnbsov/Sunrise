package com.example.sunrise.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.sunrise.R;
import com.example.sunrise.models.User;
import com.example.sunrise.models.Workspace;
import com.example.sunrise.services.UserService;
import com.example.sunrise.services.WorkspaceService;
import com.google.firebase.database.DatabaseError;

import java.util.List;
import java.util.Objects;

public class WorkspaceFragment extends Fragment {

    private String workspaceId;
    private String workspaceTitle;
    private List<String> workspaceAdminIds;


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

        // Fetch workspace details to get admin IDs
        fetchWorkspaceDetails();

        // Setup menu options
        setupMenu();
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
                    Toast.makeText(requireActivity(), "Members List selected", Toast.LENGTH_SHORT).show();
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
}