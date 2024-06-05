package com.example.sunrise.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sunrise.R;
import com.example.sunrise.adapters.TasksAdapter;
import com.example.sunrise.helpers.TaskUpdateHelper;
import com.example.sunrise.models.Task;
import com.example.sunrise.services.MyDayService;
import com.example.sunrise.services.TaskService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MyDayFragment extends Fragment {
    private View fragment;
    private RecyclerView tasksList;
    private LinearLayout emptyMyDayLayout;
    private TasksAdapter adapter;
    private TaskService taskService;
    private MyDayService myDayService;

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

        emptyMyDayLayout = fragment.findViewById(R.id.empty_my_day);
        tasksList = fragment.findViewById(R.id.tasks_list);

        // Setup RecyclerView
        setupRecyclerView();

        // Initialize Services
        taskService = new TaskService();
        myDayService = new MyDayService();

        // Fetch tasks
        fetchTasksFromDatabase();
    }

    /**
     * Initializes the RecyclerView, TaskListenerHelper, and TasksAdapter,
     * and sets up the RecyclerView with the adapter.
     */
    private void setupRecyclerView() {
        // Creating and setting linear layout manager to recyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        tasksList.setLayoutManager(layoutManager);

        // Initialize TaskListenerHelper to help with responding task actions
        TaskUpdateHelper taskUpdateHelper = new TaskUpdateHelper(requireContext());

        // Initialize adapter
        adapter = new TasksAdapter(new ArrayList<>(), false, taskUpdateHelper::onCheckboxClickedListener, taskUpdateHelper::onTaskClickListener);
        tasksList.setAdapter(adapter);
    }

    private void fetchTasksFromDatabase() {
        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        myDayService.getMyDayTaskIds(userId, taskIds -> {
            if (taskIds == null) {
                // If there are no MyDay tasks, show the "My Day is empty" layout and hide the RecyclerView
                tasksList.setVisibility(View.GONE);
                emptyMyDayLayout.setVisibility(View.VISIBLE);
            } else {
                taskService.getTasksByIds(taskIds, new TaskService.TasksListener() {
                    @Override
                    public void onTasksRetrieved(List<Task> tasks) {
                        // Sort tasks based on completion status
                        sortTasksByCompletion(tasks);

                        // Update data in the adapter using DiffUtil
                        adapter.updateData(tasks);

                        // Show the RecyclerView and hide the "My Day is empty" TextView
                        tasksList.setVisibility(View.VISIBLE);
                        emptyMyDayLayout.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("MyDayFragment", "Error fetching tasks", databaseError.toException());
                    }
                });
            }
        });
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

