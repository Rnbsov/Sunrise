package com.example.sunrise.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sunrise.R;
import com.example.sunrise.utils.IconPickerDialog;

import java.util.List;

public class IconsAdapter extends RecyclerView.Adapter<IconsAdapter.IconViewHolder> {

    private final List<IconPickerDialog.Icon> icons;
    private final OnIconClickListener listener;

    public IconsAdapter(List<IconPickerDialog.Icon> icons, OnIconClickListener listener) {
        this.icons = icons;
        this.listener = listener;
    }

    @NonNull
    @Override
    public IconViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.icon_item_layout, parent, false);
        return new IconViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IconViewHolder holder, int position) {
        final IconPickerDialog.Icon icon = icons.get(position);

        // Setting icon to imageView
        holder.iconView.setImageResource(icon.getResId());

        holder.itemView.setOnClickListener(v -> listener.onIconClick(icon));
    }

    @Override
    public int getItemCount() {
        return icons.size();
    }

    public interface OnIconClickListener {
        void onIconClick(IconPickerDialog.Icon icon);
    }

    public static class IconViewHolder extends RecyclerView.ViewHolder {
        ImageView iconView;

        public IconViewHolder(@NonNull View itemView) {
            super(itemView);
            iconView = itemView.findViewById(R.id.iconImageView);
        }
    }
}
