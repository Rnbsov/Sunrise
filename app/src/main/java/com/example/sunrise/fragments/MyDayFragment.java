package com.example.sunrise.fragments;

import android.graphics.Paint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.sunrise.R;
import com.example.sunrise.adapters.TasksAdapter;
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

        // Initialize adapter
        adapter = new TasksAdapter(new ArrayList<>(), this::onCheckboxClickedListener);
        tasksList.setAdapter(adapter);

        // Fetch tasks
        taskService = new TaskService();

        ValueEventListener tasksListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Task> taskList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Task task = snapshot.getValue(Task.class);
                    taskList.add(task);
                }

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

    private void onCheckboxClickedListener(Task task, TextView title, boolean isChecked) {
            // Tick it
            task.setCompleted(isChecked);

            // Set completedAt
            if (isChecked) {
                task.setCompletedAt(System.currentTimeMillis()); // Set completion timestamp
            } else {
                task.setCompletedAt(0); // Reset completion timestamp
            }

            // Save updated task to Firebase
            TaskService taskService = new TaskService();
            taskService.updateTask(task);

            // Apply strikethrough style if the task is completed
            if (task.isCompleted()) {
                title.setPaintFlags(title.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                // If task is not completed, it shouldn't be applied strikethrough style
                title.setPaintFlags(title.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }
    }

}