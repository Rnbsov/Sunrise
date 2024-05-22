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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MyDayFragment extends Fragment {
    private View fragment;
    private RecyclerView tasksList;
    private TasksAdapter adapter;
    private TaskService taskService;

    public MyDayFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragment = inflater.inflate(R.layout.fragment_my_day, container, false);

        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tasksList = fragment.findViewById(R.id.tasks_list);

        // Creating and setting linear layout manager to recyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        tasksList.setLayoutManager(layoutManager);

        // Initialize TaskListenerHelper to help with responding task actions
        TaskUpdateHelper taskUpdateHelper = new TaskUpdateHelper(requireContext());

        // Initialize adapter
        adapter = new TasksAdapter(new ArrayList<>(), taskUpdateHelper::onCheckboxClickedListener, taskUpdateHelper::onTaskClickListener);
        tasksList.setAdapter(adapter);

        // Initialize TaskService
        taskService = new TaskService();

        // Fetch tasks
        fetchTasksFromDatabase();
    }

    private void fetchTasksFromDatabase() {
        ValueEventListener tasksListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Task> taskList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Task task = snapshot.getValue(Task.class);
                    taskList.add(task);
                }

                Log.d("Firebase listener", "Firebase listener");
                for (Task task : taskList) {
                    System.out.println(task);
                }
                // Sort tasks based on completion status
                sortTasksByCompletion(taskList);

                // Update data in the adapter using DiffUtil
                adapter.updateData(taskList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
                Log.e("MyDayFragment", "Error fetching tasks", databaseError.toException());
            }
        };

        // Call getTasks method from TaskService to register the listener
        taskService.getTasks(tasksListener);
    }

    /**
     * Sorts a list of tasks based on their completion status.
     * <p>
     * Completed tasks are sorted after uncompleted tasks. If both tasks have the same completion status,
     * their order remains unchanged.
     */
    private void sortTasksByCompletion(List<Task> taskList) {
        taskList.sort((task1, task2) -> {
            // Completed tasks should come after uncompleted tasks
            if (task1.isCompleted() && !task2.isCompleted()) {
                // If task1 is completed and task2 is uncompleted, task2 should come first
                return 1;
            } else if (!task1.isCompleted() && task2.isCompleted()) {
                // If task1 is uncompleted and task2 is completed, task1 should come first
                return -1;
            } else {
                // Otherwise, maintain the original order
                return 0;
            }
        });
    }
}

