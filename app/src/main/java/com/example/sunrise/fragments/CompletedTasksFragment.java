package com.example.sunrise.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sunrise.R;
import com.example.sunrise.adapters.TasksAdapter;
import com.example.sunrise.helpers.TaskUpdateHelper;
import com.example.sunrise.models.Task;
import com.example.sunrise.services.TaskService;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;

public class CompletedTasksFragment extends Fragment {

    private RecyclerView tasksRecyclerView;
    private TasksAdapter taskAdapter;
    private TaskService taskService;

    public CompletedTasksFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_completed_tasks, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize TaskService to interact with Firebase database
        taskService = new TaskService();

        // Setup RecyclerView
        setupRecyclerView(view);

        // Fetch completed tasks from the Firebase
        fetchCompletedTasksFromDatabase();
    }

    /**
     * Initializes the RecyclerView, TaskListenerHelper, and TasksAdapter,
     * and sets up the RecyclerView with the adapter.
     *
     * @param view The root view of the fragment.
     */
    private void setupRecyclerView(View view) {
        // Find view and set layout manager for it
        tasksRecyclerView = view.findViewById(R.id.tasks_recycler_view);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Initialize TaskListenerHelper to help with responding task actions
        TaskUpdateHelper taskUpdateHelper = new TaskUpdateHelper(requireContext());

        // Create TaskAdapter and set it to recycler view
        taskAdapter = new TasksAdapter(new ArrayList<>(), false, taskUpdateHelper::onCheckboxClickedListener, taskUpdateHelper::onTaskClickListener);
        tasksRecyclerView.setAdapter(taskAdapter);
    }

    /**
     * Fetches completed tasks from the Firebase database
     * and updates the RecyclerView adapter with the retrieved tasks.
     */
    private void fetchCompletedTasksFromDatabase() {
        // Define a listener to handle completed tasks retrieval
        TaskService.CompletedTasksListener tasksListener = new TaskService.CompletedTasksListener() {
            @Override
            public void onCompletedTasksLoaded(List<Task> completedTasks) {
                // Update data in the adapter using DiffUtil
                taskAdapter.updateData(completedTasks);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("CategoryFragment", "Failed to load tasks");
            }
        };

        taskService.getCompletedTasks(tasksListener);
    }
}