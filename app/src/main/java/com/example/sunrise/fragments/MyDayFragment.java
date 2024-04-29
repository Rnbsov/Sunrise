package com.example.sunrise.fragments;

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

import com.example.sunrise.R;
import com.example.sunrise.adapters.TasksAdapter;
import com.example.sunrise.models.Task;
import com.example.sunrise.services.TaskService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyDayFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
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
                // Create adapter with dataset of fetched tasks
                adapter = new TasksAdapter(taskList);
                Log.d("MyDayFragment", "tasks" + taskList.toString());

                // Setting custom adapter to recyclerView
                tasksList.setAdapter(adapter);
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

}