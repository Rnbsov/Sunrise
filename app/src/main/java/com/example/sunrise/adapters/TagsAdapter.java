package com.example.sunrise.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sunrise.R;
import com.example.sunrise.models.Tag;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class TagsAdapter extends RecyclerView.Adapter<TagsAdapter.TagViewHolder> {

    private final List<Tag> localDataSet;
    private final TagsAdapter.OnTagClickListener OnTagClickListener;

    public TagsAdapter(List<Tag> dataSet, TagsAdapter.OnTagClickListener OnTagClickListener) {
        this.localDataSet = dataSet;
        this.OnTagClickListener = OnTagClickListener;
    }

    @NonNull
    @Override
    public TagsAdapter.TagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tag_item_layout, parent, false);
        return new TagsAdapter.TagViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull TagsAdapter.TagViewHolder holder, int position) {
        Tag tag = localDataSet.get(position);
        ShapeableImageView colorView = holder.getColorView();
        TextView tagName = holder.getTagName();

        colorView.setBackgroundColor(tag.getColor());
        tagName.setText(tag.getTitle());

        holder.itemView.setOnClickListener(v -> OnTagClickListener.onTagClick(tag));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    public void setTags(List<Tag> newTags) {
        localDataSet.clear(); // Clear the existing tags
        localDataSet.addAll(newTags); // Add all new tags
        notifyDataSetChanged(); // Notify the adapter about the dataset change
    }

    public interface OnTagClickListener {
        void onTagClick(Tag tag);
    }

    public static class TagViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView colorView;
        TextView tagName;

        public TagViewHolder(@NonNull View itemView) {
            super(itemView);
            colorView = itemView.findViewById(R.id.imageView);
            tagName = itemView.findViewById(R.id.tagName);
        }

        public ShapeableImageView getColorView() {
            return colorView;
        }

        public TextView getTagName() {
            return tagName;
        }
    }
}
