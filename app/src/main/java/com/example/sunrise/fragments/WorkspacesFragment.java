package com.example.sunrise.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.sunrise.models.Workspace;

import java.util.ArrayList;
import java.util.List;

public class WorkspacesFragment extends Fragment {

    private RecyclerView workspacesRecyclerView;
    private WorkspaceAdapter workspaceAdapter;
    private WorkspaceCreationHelper workspaceCreationHelper;

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

        // Initialize CategoryCreationHelper
        workspaceCreationHelper = new WorkspaceCreationHelper(requireContext());

        // Setup Workspaces RecyclerView
        setupWorkspacesRecyclerView(view);
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

    /**
     * Handle workspace item click
     *
     * @param workspace clicked workspace object
     */
    private void onWorkspaceClick(Workspace workspace) {
        Toast.makeText(requireContext(), "Clicked: " + workspace.getTitle(), Toast.LENGTH_SHORT).show();

        Bundle bundle = new Bundle();
        bundle.putString("workspaceId", workspace.getWorkspaceId());
        bundle.putString("workspaceTitle", workspace.getTitle());

        NavController navController = Navigation.findNavController(requireView());
        navController.navigate(R.id.action_page_workspaces_to_workspaceFragment, bundle);
    }

    private void onWorkspaceAddButtonClick(View v) {
        workspaceCreationHelper.showWorkspaceCreationDialog((ViewGroup) requireView());
    }
}
