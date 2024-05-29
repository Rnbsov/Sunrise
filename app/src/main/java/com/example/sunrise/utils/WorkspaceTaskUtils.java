package com.example.sunrise.utils;

import android.content.Context;
import android.util.Pair;
import android.view.View;
import android.widget.PopupMenu;

import com.example.sunrise.models.User;
import com.example.sunrise.services.WorkspaceService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkspaceTaskUtils {

    /**
     * Shows a popup menu with the list of users who are members of a specified workspace.
     *
     * @param context     The context in which the popup menu should be displayed.
     * @param anchorView  The view to which the popup menu should be anchored.
     * @param workspaceId The Id of the workspace whose members are to be shown.
     * @param listener    The listener to handle the event when a user is selected from the popup menu.
     */
    public static void showUsersPopupMenu(Context context, View anchorView, String workspaceId, OnUserSelectedListener listener) {
        WorkspaceService workspaceService = new WorkspaceService();

        workspaceService.getWorkspaceMembers(workspaceId, new WorkspaceService.WorkspaceMembersListener() {
            @Override
            public void onWorkspaceMembersRetrieved(List<User> users) {
                if (users == null || users.isEmpty()) {
                    return;
                }

                Map<Integer, Pair<String, String>> userIdMap = new HashMap<>();

                // Store user details in a map with the user's hash code as the key
                for (User user : users) {
                    String userId = user.getUserId();
                    String userName = user.getNickname();
                    int userIdHashCode = userId.hashCode();
                    userIdMap.put(userIdHashCode, new Pair<>(userId, userName));
                }

                PopupMenu popup = new PopupMenu(context, anchorView);

                // Add each user to the popup menu
                for (Map.Entry<Integer, Pair<String, String>> entry : userIdMap.entrySet()) {
                    popup.getMenu().add(0, entry.getKey(), 0, entry.getValue().second);
                }

                // Set the listener to handle menu item clicks
                popup.setOnMenuItemClickListener(item -> {
                    int userIdHashCode = item.getItemId();
                    Pair<String, String> selectedUserInfo = userIdMap.get(userIdHashCode);
                    if (selectedUserInfo != null) {
                        String selectedUserId = selectedUserInfo.first;
                        String selectedUserName = selectedUserInfo.second;

                        // Trigger the listener callback with the selected user's information
                        listener.onUserSelected(selectedUserId, selectedUserName);
                        return true;
                    }
                    return false;
                });

                // Show the popup menu
                popup.show();
            }
        });
    }

    public interface OnUserSelectedListener {
        void onUserSelected(String userId, String userName);
    }
}
