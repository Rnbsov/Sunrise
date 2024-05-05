package com.example.sunrise.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sunrise.R;
import com.example.sunrise.models.Category;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private final List<Category> localDataSet;
    private final CategoryAdapter.OnCategoryClickListener OnCategoryClickListener;

    public CategoryAdapter(List<Category> dataSet, CategoryAdapter.OnCategoryClickListener OnCategoryClickListener) {
        this.localDataSet = dataSet;
        this.OnCategoryClickListener = OnCategoryClickListener;
    }

    @NonNull
    @Override
    public CategoryAdapter.CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item_layout, parent, false);
        return new CategoryAdapter.CategoryViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.CategoryViewHolder holder, int position) {
        Category category = localDataSet.get(position);
        ShapeableImageView icon = holder.getColorView();
        TextView title = holder.getCategoryName();

        icon.setBackgroundColor(category.getColor());
        title.setText(category.getTitle());

        holder.itemView.setOnClickListener(v -> OnCategoryClickListener.onCategoryClick(category));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    public void setCategories(List<Category> newCategories) {
        localDataSet.clear(); // Clear the existing tags
        localDataSet.addAll(newCategories); // Add all new categories
        notifyDataSetChanged(); // Notify the adapter about the dataset change
    }

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView icon;
        TextView title;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.categoryIcon);
            title = itemView.findViewById(R.id.title);
        }

        public ShapeableImageView getColorView() {
            return icon;
        }

        public TextView getCategoryName() {
            return title;
        }
    }
}
