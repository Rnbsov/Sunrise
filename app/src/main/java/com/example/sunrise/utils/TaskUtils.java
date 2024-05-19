package com.example.sunrise.utils;

import android.content.Context;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;

import com.example.sunrise.R;
import com.google.android.material.chip.Chip;

public class TaskUtils {

    public static void showPriorityPopupMenu(Context context, View anchorView, Chip priorityChip) {
        PopupMenu popup = new PopupMenu(context, anchorView);
        popup.getMenuInflater().inflate(R.menu.priorities_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            // Call onPriorityMenuItemClick with the MenuItem and priorityChip
            return TaskUtils.onPriorityMenuItemClick(item, priorityChip);
        });

        popup.show();
    }

    public static boolean onPriorityMenuItemClick(@NonNull MenuItem item, Chip priorityChip) {
        int itemId = item.getItemId();

        if (itemId == R.id.priority_high) {
            priorityChip.setText(R.string.priority_high);
            return true;
        } else if (itemId == R.id.priority_medium) {
            priorityChip.setText(R.string.priority_medium);
            return true;
        } else if (itemId == R.id.priority_low) {
            priorityChip.setText(R.string.priority_low);
            return true;
        } else if (itemId == R.id.priority_regular) {
            priorityChip.setText(R.string.priority_regular);
            return true;
        } else {
            return false;
        }
    }

    public static String getPriorityValue(String selectedPriority, String defaultPriority, String regularPriority) {
        // Check if the selected priority is the default one
        if (selectedPriority.equals(defaultPriority)) {
            // If so, set the priority to the regular
            return regularPriority;
        } else {
            // Otherwise, return the selected priority
            return selectedPriority;
        }
    }
}
