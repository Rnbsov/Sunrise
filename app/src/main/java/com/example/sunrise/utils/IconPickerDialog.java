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

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class IconPickerDialog {
    private final AlertDialog dialog;
    private final List<Icon> icons;
    private final OnIconSelectedListener iconSelectedListener;

    public interface OnIconSelectedListener {
        void onIconSelected(Icon icon);
    }

    public IconPickerDialog(Context context, OnIconSelectedListener listener) {
        // Initialize icons
        this.icons = Arrays.asList(Icon.values());;
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

    private void onIconSelected(Icon icon) {
        if (iconSelectedListener != null) {
            iconSelectedListener.onIconSelected(icon);
        }
        dismiss();
    }

    public void dismiss() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public Icon getRandomIcon() {
        Random random = new Random();
        Icon randomIcon = icons.get(random.nextInt(icons.size()));
        return randomIcon;
    }

    public enum Icon {
        LABEL(R.drawable.label_24px),
        PALETTE(R.drawable.palette_24px),
        FLOWER(R.drawable.flower_24px),
        DIAMOND(R.drawable.diamond_24px);

        private final int resId;

        Icon(int resId) {
            this.resId = resId;
        }

        public int getResId() {
            return resId;
        }
    }
}
