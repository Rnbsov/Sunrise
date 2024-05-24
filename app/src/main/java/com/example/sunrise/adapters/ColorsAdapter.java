package com.example.sunrise.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sunrise.R;
import com.example.sunrise.constants.ColorsEnum;

import java.util.List;

public class ColorsAdapter extends RecyclerView.Adapter<ColorsAdapter.ColorViewHolder> {

    private final List<ColorsEnum> colors;
    private final OnColorClickListener listener;

    public ColorsAdapter(List<ColorsEnum> colors, OnColorClickListener listener) {
        this.colors = colors;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ColorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.color_item_layout, parent, false);
        return new ColorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ColorViewHolder holder, int position) {
        ColorsEnum color = colors.get(position);
        View colorView = holder.getColorView();

        colorView.setBackgroundColor(color.getColor());

        holder.itemView.setOnClickListener(v -> listener.onColorClick(color));
    }

    @Override
    public int getItemCount() {
        return colors.size();
    }

    public interface OnColorClickListener {
        void onColorClick(ColorsEnum color);
    }

    public static class ColorViewHolder extends RecyclerView.ViewHolder {
        View colorView;

        public ColorViewHolder(@NonNull View itemView) {
            super(itemView);
            colorView = itemView.findViewById(R.id.imageView);
        }

        public View getColorView() {
            return colorView;
        }
    }
}
