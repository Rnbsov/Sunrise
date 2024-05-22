package com.example.sunrise.utils;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sunrise.R;
import com.example.sunrise.adapters.ColorsAdapter;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ColorPickerDialog {
    private final AlertDialog dialog;
    private final List<Integer> colors;
    private final OnColorSelectedListener colorSelectedListener;

    public interface OnColorSelectedListener {
        void onColorSelected(int color);
    }

    public ColorPickerDialog(Context context, OnColorSelectedListener listener) {
        // Initialize colors
        this.colors = initializeColors();
        this.colorSelectedListener = listener;

        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_choose_color, null);
        dialog = new MaterialAlertDialogBuilder(context)
                .setView(dialogView)
                .create();

        RecyclerView recyclerView = dialogView.findViewById(R.id.recycler_view_colors);

        // Some optimization
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 5));
        // Set the spacing between items
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(context.getResources().getDimensionPixelSize(R.dimen.grid_spacing)));

        ColorsAdapter adapter = new ColorsAdapter(colors, this::onColorSelected);
        recyclerView.setAdapter(adapter);
    }

    public void show() {
        if (dialog != null) {
            dialog.show();
        }
    }

    private void onColorSelected(int color) {
        if (colorSelectedListener != null) {
            colorSelectedListener.onColorSelected(color);
        }
        dismiss();
    }

    public void dismiss() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    private List<Integer> initializeColors() {
        List<Integer> colors = new ArrayList<>();

        // Define hexadecimal colors
        String[] hexValues = {
                "#FFCCCC", // Pastel Red
                "#FFE5CC", // Pastel Orange
                "#FFF2CC", // Pastel Yellow
                "#CCFFCC", // Pastel Green
                "#CCE5FF", // Pastel Blue
                "#FFCCFF"  // Pastel Purple
        };

        // Convert hexadecimal values to color integers and add them to the list
        for (String hex : hexValues) {
            int color = Color.parseColor(hex);
            colors.add(color);
        }

        return colors;
    }

    public int getRandomColor() {
        Random random = new Random();
        return colors.get(random.nextInt(colors.size()));
    }
}
