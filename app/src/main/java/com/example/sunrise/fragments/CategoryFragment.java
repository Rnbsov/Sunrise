package com.example.sunrise.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
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

        // Retrieve the category id and title from arguments
        retrieveArguments();

        // Set action bar title to category title
        setActionBarTitle();

        // Initialize TaskService to interact with Firebase database
        taskService = new TaskService();

        // Setup RecyclerView
        setupRecyclerView(view);

        // Fetch tasks from the database
        fetchTasksFromDatabase();

        // Setup menu options
        setupMenu();
    }

    /**
     * This method retrieve categoryId and categoryTitle from arguments
     * and save them as class properties {@code categoryId} and {@code categoryTitle}.
     */
    private void retrieveArguments() {
        if (getArguments() != null) {
            categoryId = getArguments().getString("categoryId");
            categoryTitle = getArguments().getString("categoryTitle");
        }
    }

    /**
     * Sets up the action bar title to display the category title.
     */
    private void setActionBarTitle() {
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setTitle(categoryTitle);
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
        taskAdapter = new TasksAdapter(new ArrayList<>(), true, taskUpdateHelper::onCheckboxClickedListener, taskUpdateHelper::onTaskClickListener);
        tasksRecyclerView.setAdapter(taskAdapter);
    }

    /**
     * Fetches tasks associated with the category from the Firebase database
     * and updates the RecyclerView adapter with the retrieved tasks.
     */
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


    /**
     * Sets up the menu options for the category fragment, such as editing or deleting the category.
     */
    private void setupMenu() {
        MenuHost menuHost = requireActivity();
        menuHost.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.category_fragment_menu, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                int itemId = menuItem.getItemId();
                if (itemId == R.id.edit_category) {
                    Toast.makeText(requireActivity(), "Not implemented", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (itemId == R.id.delete_category) {
                    Toast.makeText(requireActivity(), "Not implemented", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }
}
