package com.example.sunrise.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sunrise.R;

import java.util.Objects;

public class WorkspaceFragment extends Fragment {

    private String workspaceId;
    private String workspaceTitle;


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
}