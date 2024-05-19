package com.example.sunrise.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sunrise.R;
import com.example.sunrise.adapters.TasksAdapter;
import com.example.sunrise.helpers.TaskListenerHelper;
import com.example.sunrise.models.Task;
import com.example.sunrise.services.TaskService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CategoryFragment extends Fragment {

    private RecyclerView tasksRecyclerView;
    private TasksAdapter taskAdapter;
    private TaskService taskService;
    private String categoryId;
    private String categoryTitle;

    public CategoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_category, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Retrieve the categoryId from arguments
        if (getArguments() != null) {
            categoryId = getArguments().getString("categoryId");
            categoryTitle = getArguments().getString("categoryTitle");
        }

        // Set action bar title to category title
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setTitle(categoryTitle);

        // Initialize TaskService to interact with Firebase database
        taskService = new TaskService();

        // Initialize TaskListenerHelper to help with responding task actions
        TaskListenerHelper taskListenerHelper = new TaskListenerHelper(requireContext());

        tasksRecyclerView = view.findViewById(R.id.tasks_recycler_view);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        taskAdapter = new TasksAdapter(new ArrayList<>(), taskListenerHelper::onCheckboxClickedListener, taskListenerHelper::onTaskClickListener);
        tasksRecyclerView.setAdapter(taskAdapter);

        fetchTasksFromDatabase();
    }

    private void fetchTasksFromDatabase() {
        ValueEventListener tasksListener = new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Task> taskList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Task task = snapshot.getValue(Task.class);
                    if (task != null && task.getCategoryId().equals(categoryId)) {
                        taskList.add(task);
                    }
                }

                // Update data in the adapter using DiffUtil
                taskAdapter.updateData(taskList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("CategoryFragment", "Failed to load tasks");
            }
        };
        taskService.getTasksByCategoryId(categoryId, tasksListener);
    }
}
