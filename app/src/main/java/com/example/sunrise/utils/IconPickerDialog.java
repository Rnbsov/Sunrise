package com.example.sunrise.utils;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.sunrise.R;
import com.example.sunrise.adapters.IconsAdapter;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class IconPickerDialog {
    private final AlertDialog dialog;
    private final List<Integer> icons;
    private final OnIconSelectedListener iconSelectedListener;

    public interface OnIconSelectedListener {
        void onIconSelected(int iconResId);
    }

    public IconPickerDialog(Context context, OnIconSelectedListener listener) {
        // Initialize icons
        this.icons = initializeIcons();
        this.iconSelectedListener = listener;

        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_choose_icon, null);
        dialog = new MaterialAlertDialogBuilder(context)
                .setView(dialogView)
                .create();

        RecyclerView recyclerView = dialogView.findViewById(R.id.recycler_view_icons);

        // Some optimization
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 5));
        // Set the spacing between items
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(context.getResources().getDimensionPixelSize(R.dimen.grid_spacing)));

        IconsAdapter adapter = new IconsAdapter(icons, this::onIconSelected);
        recyclerView.setAdapter(adapter);
    }

    public void show() {
        if (dialog != null) {
            dialog.show();
        }
    }

    private void onIconSelected(int iconResId) {
        if (iconSelectedListener != null) {
            iconSelectedListener.onIconSelected(iconResId);
        }
        dismiss();
    }

    public void dismiss() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    private List<Integer> initializeIcons() {
        List<Integer> icons = new ArrayList<>();

        // Initialize icons
        icons = Arrays.asList(
                R.drawable.label_24px,
                R.drawable.palette_24px,
                R.drawable.flower_24px
        );

        return icons;
    }

    public int getRandomIcon() {
        Random random = new Random();
        return icons.get(random.nextInt(icons.size()));
    }
}
