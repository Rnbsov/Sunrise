package com.example.sunrise.services;

import com.example.sunrise.models.WorkspaceTask;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class WorkspaceTaskService {

    private final DatabaseReference workspaceTasksRef;

    public WorkspaceTaskService() {
        // Initialize Firebase database reference
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        workspaceTasksRef = database.getReference("WorkspaceTasks");
    }

    /**
     * Method to create the workspace task in Firebase database
     */
    public void createWorkspaceTask(WorkspaceTask workspaceTask) {
        // Update the updatedAt timestamp before starting saving
        workspaceTask.setUpdatedAt(System.currentTimeMillis());

        // Generate a reference to a new child location under "workspaceTasks" with a client-side auto-generated key
        DatabaseReference newWorkspaceTaskRef = workspaceTasksRef.push();

        String taskId = newWorkspaceTaskRef.getKey(); // Retrieve the unique ID
        workspaceTask.setTaskId(taskId); // Save this unique ID to the workspace task object

        newWorkspaceTaskRef.setValue(workspaceTask); // Save the workspace task to Firebase database
    }

    /**
     * Method to update the workspace task in Firebase database
     */
    public void updateWorkspaceTask(WorkspaceTask workspaceTask) {
        // Update the updatedAt timestamp before starting saving
        workspaceTask.setUpdatedAt(System.currentTimeMillis());

        // Get the reference to the workspace task's location in Firebase using its unique ID
        DatabaseReference workspaceTaskRef = workspaceTasksRef.child(workspaceTask.getTaskId());

        // Update the workspace task at the specified location in Firebase
        workspaceTaskRef.setValue(workspaceTask);
    }

    /**
     * Method to retrieve all workspace tasks by workspace ID
     */
    public void getWorkspaceTasks(String workspaceId, ValueEventListener listener) {
        // Create a query to filter tasks by workspaceId
        Query query = workspaceTasksRef.orderByChild("workspaceId").equalTo(workspaceId);

        // Add ValueEventListener to the query to listen for changes in Firebase data
        query.addValueEventListener(listener);
    }
}
