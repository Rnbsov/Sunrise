package com.example.sunrise.utils;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.sunrise.R;
import com.example.sunrise.adapters.IconsAdapter;

import java.util.List;

public class IconPickerDialog {
    private AlertDialog dialog;
    private List<Integer> icons;
    private OnIconSelectedListener iconSelectedListener;

    public interface OnIconSelectedListener {
        void onIconSelected(int iconResId);
    }

    public IconPickerDialog(Context context, List<Integer> icons, OnIconSelectedListener listener) {
        this.icons = icons;
        this.iconSelectedListener = listener;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_choose_icon, null);
        builder.setView(dialogView);

        RecyclerView recyclerView = dialogView.findViewById(R.id.recycler_view_icons);

        // Some optimization
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 5));
        // Set the spacing between items
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(context.getResources().getDimensionPixelSize(R.dimen.grid_spacing)));

        IconsAdapter adapter = new IconsAdapter(icons, this::onIconSelected);
        recyclerView.setAdapter(adapter);

        dialog = builder.create();
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
}
