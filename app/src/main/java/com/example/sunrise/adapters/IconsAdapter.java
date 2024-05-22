package com.example.sunrise.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sunrise.R;

import java.util.List;

public class IconsAdapter extends RecyclerView.Adapter<IconsAdapter.IconViewHolder> {

    private final List<Integer> icons;
    private final OnIconClickListener listener;

    public IconsAdapter(List<Integer> icons, OnIconClickListener listener) {
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
        final int iconResId = icons.get(position);

        // Setting icon to imageView
        holder.iconView.setImageResource(iconResId);

        holder.itemView.setOnClickListener(v -> listener.onIconClick(iconResId));
    }

    @Override
    public int getItemCount() {
        return icons.size();
    }

    public interface OnIconClickListener {
        void onIconClick(int iconResId);
    }

    public static class IconViewHolder extends RecyclerView.ViewHolder {
        ImageView iconView;

        public IconViewHolder(@NonNull View itemView) {
            super(itemView);
            iconView = itemView.findViewById(R.id.iconImageView);
        }
    }
}
