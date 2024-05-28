package com.example.sunrise.utils;

import android.content.Context;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;

import com.example.sunrise.models.User;
import com.example.sunrise.services.UserService;
import com.example.sunrise.services.WorkspaceService;
import com.google.firebase.database.DatabaseError;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkspaceTaskUtils {

    /**
     * Shows a popup menu with the list of users who are members of a specified workspace.
     *
     * @param context    The context in which the popup menu should be displayed.
     * @param anchorView The view to which the popup menu should be anchored.
     * @param workspaceId  The Id of the workspace whose members are to be shown.
     * @param listener   The listener to handle the event when a user is selected from the popup menu.
     */
    public static void showUsersPopupMenu(Context context, View anchorView, String workspaceId, OnUserSelectedListener listener) {
        WorkspaceService workspaceService = new WorkspaceService();

        workspaceService.getWorkspaceMembers(workspaceId, new WorkspaceService.WorkspaceMembersListener() {
            @Override
            public void onWorkspaceMembersLoaded(List<String> memberIds) {
                if (memberIds == null || memberIds.isEmpty()) {
                    return;
                }

                UserService userService = new UserService();
                // Retrieve user details from the database for the given list of member IDs
                retrieveUsersFromDatabase(userService, memberIds, userIdMap -> {
                    PopupMenu popup = new PopupMenu(context, anchorView);

                    // Add each user to the popup menu
                    for (Map.Entry<Integer, Pair<String, String>> entry : userIdMap.entrySet()) {
                        popup.getMenu().add(0, entry.getKey(), 0, entry.getValue().second);
                    }

                    // Set the listener to handle menu item clicks
                    popup.setOnMenuItemClickListener(item -> {
                        int userIdHashCode = item.getItemId();
                        Pair<String, String> selectedUserInfo = userIdMap.get(userIdHashCode);
                        assert selectedUserInfo != null;

                        String selectedUserId = selectedUserInfo.first;
                        String selectedUserName = selectedUserInfo.second;

                        // Trigger the listener callback with the selected user's information
                        listener.onUserSelected(selectedUserId, selectedUserName);
                        return true;
                    });

                    // Show the popup menu
                    popup.show();
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error if necessary
                Log.e("TaskUtils", "Failed to retrieve workspace members: " + databaseError.getMessage());
            }
        });
    }

    /**
     * Retrieves user details from the database for the given list of user IDs and passes the results
     * to the specified callback.
     *
     * @param userService The UserService instance used to retrieve user details.
     * @param userIds     The list of user IDs to retrieve.
     * @param callback    The callback to handle the retrieved user details.
     */
    private static void retrieveUsersFromDatabase(UserService userService, List<String> userIds, UsersRetrievedCallback callback) {
        Map<Integer, Pair<String, String>> userIdMap = new HashMap<>();

        // Retrieve users from the database for the given list of user IDs
        userService.getUsersByIds(userIds, users -> {

            for (User user : users) {
                String userId = user.getUserId();
                String userName = user.getNickname();
                int userIdHashCode = userId.hashCode();

                // Store the user details in a map with the user ID hash code as the key
                userIdMap.put(userIdHashCode, new Pair<>(userId, userName));
            }

            // Trigger the callback with the retrieved user details
            callback.onUsersRetrieved(userIdMap);
        });
    }

    public interface UsersRetrievedCallback {
        void onUsersRetrieved(Map<Integer, Pair<String, String>> userIdMap);
    }

    public interface OnUserSelectedListener {
        void onUserSelected(String userId, String userName);
    }
}
