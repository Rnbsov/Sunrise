package com.example.sunrise.services;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.sunrise.models.User;
import com.example.sunrise.models.Workspace;
import com.example.sunrise.utils.InviteCodeUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class WorkspaceService {

    private final FirebaseDatabase database;
    private final DatabaseReference workspacesRef;
    private final DatabaseReference inviteCodesRef;

    public WorkspaceService() {
        // Initialize Firebase database reference
        database = FirebaseDatabase.getInstance();
        workspacesRef = database.getReference("Workspaces");
        inviteCodesRef = database.getReference("InviteCodes");
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
        DatabaseReference workspaceNewRef = workspacesRef.child(workspace.getWorkspaceId());

        // Update the workspace at the specified location in Firebase
        workspaceNewRef.setValue(workspace);
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
            workspaceRef.addValueEventListener(new ValueEventListener() {
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

    public void getWorkspaceById(String workspaceId, WorkspaceRetrievedListener listener) {
        DatabaseReference workspaceRef = workspacesRef.child(workspaceId);
        workspaceRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Workspace workspace = dataSnapshot.getValue(Workspace.class);
                    listener.onWorkspaceRetrieved(workspace);
                } else {
                    listener.onWorkspaceRetrieved(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("WorkspaceService", "Failed to retrieve workspace: " + databaseError.getMessage());
                listener.onCancelled(databaseError);
            }
        });
    }

    public interface WorkspaceRetrievedListener {
        void onWorkspaceRetrieved(Workspace workspace);
        void onCancelled(DatabaseError databaseError);
    }


    public void createInviteCode(String workspaceId) {
        String inviteCode = InviteCodeUtils.generateInviteCode();

        // Check if the code already exists
        inviteCodesRef.child(inviteCode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Code already exists, generate a new one
                    createInviteCode(workspaceId);
                } else {
                    // Save the new invite code
                    inviteCodesRef.child(inviteCode).setValue(workspaceId)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    // Invite code created successfully
                                    Log.d("WorkspaceService", "Invite code created");
                                } else {
                                    // Handle error
                                    Log.e("WorkspaceService", "Invite code creation failed");
                                }
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                                    Log.e("WorkspaceService", "Invite code creation failed " + databaseError.getMessage());

            }
        });
    }

    public void joinWorkspace(String code, String userId) {
        DatabaseReference inviteCodeRef = inviteCodesRef.child(code);
        inviteCodeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String workspaceId = dataSnapshot.getValue(String.class);
                    addUserToWorkspace(userId, workspaceId);
                } else {
                    // Handle case where invite code does not exist
                    Log.d("WorkspaceService", "No invite code found");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
                Log.d("WorkspaceService", "Joining to workspace failed " + databaseError.getMessage());
            }
        });
    }

    private void addUserToWorkspace(String userId, String workspaceId) {
        // Update user's workspaceIds list
        updateUserWorkspaceIds(workspaceId);

        // Update workspace's memberIds list
        updateWorkspaceMemberIds(workspaceId, userId);
    }

    private void updateUserWorkspaceIds(String workspaceId) {
        UserService userService = new UserService();
        userService.getCurrentUser(new UserService.CurrentUserListener() {
            @Override
            public void onCurrentUserRetrieved(User user) {
                if (user != null) {
                    List<String> workspaceIds = user.getWorkspaceIds();
                    if (workspaceIds == null) {
                        workspaceIds = new ArrayList<>();
                    }
                    // Check if workspaceId already exists in the list
                    if (!workspaceIds.contains(workspaceId)) {
                        workspaceIds.add(workspaceId);
                        user.setWorkspaceIds(workspaceIds);
                        // Update user in Firebase
                        userService.updateUser(user);
                    } else {
                        Log.d("WorkspaceService", "Workspace ID already exists for the user");
                    }
                } else {
                    Log.e("WorkspaceService", "User not found");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("WorkspaceService", "Failed to retrieve current user: " + databaseError.getMessage());
            }
        });
    }

    private void updateWorkspaceMemberIds(String workspaceId, String userId) {
        getWorkspaceById(workspaceId, new WorkspaceRetrievedListener() {
            @Override
            public void onWorkspaceRetrieved(Workspace workspace) {
                if (workspace != null) {
                    List<String> memberIds = workspace.getMemberIds();
                    // Check if userId already exists in the list
                    if (!memberIds.contains(userId)) {
                        memberIds.add(userId);
                        workspace.setMemberIds(memberIds);
                        // Update workspace in Firebase
                        updateWorkspace(workspace);
                    } else {
                        Log.d("WorkspaceService", "User ID already exists for the workspace");
                    }
                } else {
                    Log.e("WorkspaceService", "Workspace not found");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("WorkspaceService", "Failed to retrieve workspace: " + databaseError.getMessage());
            }
        });
    }

}
