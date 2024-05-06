package com.example.sunrise.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sunrise.R;
import com.example.sunrise.adapters.ColorsAdapter;

import java.util.List;

public class ColorPickerDialog {
    private final AlertDialog dialog;
    private final List<Integer> colors;
    private final OnColorSelectedListener colorSelectedListener;

    public interface OnColorSelectedListener {
        void onColorSelected(int color);
    }

    public ColorPickerDialog(Context context, List<Integer> colors, OnColorSelectedListener listener) {
        this.colors = colors;
        this.colorSelectedListener = listener;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_choose_color, null);
        builder.setView(dialogView);

        RecyclerView recyclerView = dialogView.findViewById(R.id.recycler_view_colors);

        // Some optimization
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 5));
        // Set the spacing between items
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(context.getResources().getDimensionPixelSize(R.dimen.grid_spacing)));

        ColorsAdapter adapter = new ColorsAdapter(colors, this::onColorSelected);
        recyclerView.setAdapter(adapter);

        dialog = builder.create();
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
}
