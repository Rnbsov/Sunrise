package com.example.sunrise.services;

import androidx.annotation.NonNull;

import com.example.sunrise.models.Workspace;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class WorkspaceService {

    private final DatabaseReference workspacesRef;

    public WorkspaceService() {
        // Initialize Firebase database reference
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        workspacesRef = database.getReference("Workspaces");
    }

    /**
     * Method to create a workspace in Firebase database
     */
    public void createWorkspace(Workspace workspace) {
        // Generate a reference to a new child location under "workspaces" with a client-side auto-generated key
        DatabaseReference newWorkspaceRef = workspacesRef.push();

        String workspaceId = newWorkspaceRef.getKey(); // Retrieve the unique ID
        workspace.setWorkspaceId(workspaceId); // Save this unique ID to the workspace object

        newWorkspaceRef.setValue(workspace); // Save the workspace to Firebase database
    }

    /**
     * Method to update a workspace in Firebase database
     */
    public void updateWorkspace(Workspace workspace) {
        // Update the updatedAt timestamp before saving
        workspace.setUpdatedAt(System.currentTimeMillis());

        // Get the reference to the workspace's location in Firebase using its unique ID
        DatabaseReference workspaceRef = workspacesRef.child(workspace.getWorkspaceId());

        // Update the workspace at the specified location in Firebase
        workspaceRef.setValue(workspace);
    }

    /**
     * Method to retrieve workspaces by their IDs
     *
     * @param workspaceIds List of workspace IDs to retrieve
     * @param listener     Custom interface for receiving workspace data
     */
    public void retrieveWorkspacesByIds(List<String> workspaceIds, WorkspacesListener listener) {
        List<Workspace> workspaces = new ArrayList<>();

        for (String workspaceId : workspaceIds) {
            DatabaseReference workspaceRef = workspacesRef.child(workspaceId);
            workspaceRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Workspace workspace = dataSnapshot.getValue(Workspace.class);
                        workspaces.add(workspace);

                        // Check if all workspaces have been retrieved
                        if (workspaces.size() == workspaceIds.size()) {
                            listener.onWorkspacesRetrieved(workspaces);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle error if query is cancelled
                    listener.onCancelled(error);
                }
            });
        }
    }

    public interface WorkspacesListener {
        void onWorkspacesRetrieved(List<Workspace> workspaces);

        void onCancelled(DatabaseError databaseError);
    }

    /**
     * Method to retrieve workspace members by workspace ID
     */
    public void getWorkspaceMembers(String workspaceId, final WorkspaceMembersListener listener) {
        DatabaseReference workspaceRef = workspacesRef.child(workspaceId);

        workspaceRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Workspace workspace = dataSnapshot.getValue(Workspace.class);
                if (workspace != null) {
                    listener.onWorkspaceMembersLoaded(workspace.getMemberIds());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onCancelled(databaseError);
            }
        });
    }

    public interface WorkspaceMembersListener {
        void onWorkspaceMembersLoaded(List<String> memberIds);
        void onCancelled(DatabaseError databaseError);
    }
}
