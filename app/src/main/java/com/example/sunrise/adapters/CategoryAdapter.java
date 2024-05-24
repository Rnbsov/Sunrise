package com.example.sunrise.adapters;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sunrise.R;
import com.example.sunrise.constants.Icon;
import com.example.sunrise.models.Category;
import com.example.sunrise.utils.ColorUtils;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_CATEGORY = 0;
    private static final int VIEW_TYPE_ADD_BUTTON = 1;

    private final List<Category> localDataSet;
    private final CategoryAdapter.OnCategoryClickListener categoryClickListener;
    private final OnCategoryAddClickListener categoryAddClickListener;

    public CategoryAdapter(List<Category> dataSet, CategoryAdapter.OnCategoryClickListener categoryClickListener, OnCategoryAddClickListener categoryAddClickListener) {
        this.localDataSet = dataSet;
        this.categoryClickListener = categoryClickListener;
        this.categoryAddClickListener = categoryAddClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        return position == localDataSet.size() ? VIEW_TYPE_ADD_BUTTON : VIEW_TYPE_CATEGORY;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_CATEGORY) {
            View view = inflater.inflate(R.layout.category_item_layout, parent, false);
            return new CategoryViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.category_add_button_layout, parent, false);
            return new AddButtonViewHolder(view);
        }
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CategoryViewHolder) {
            CategoryViewHolder categoryViewHolder = (CategoryViewHolder) holder;
            Category category = localDataSet.get(position);
            categoryViewHolder.bind(category);
            categoryViewHolder.itemView.setOnClickListener(v -> categoryClickListener.onCategoryClick(category));
        } else if (holder instanceof AddButtonViewHolder) {
            AddButtonViewHolder addButtonViewHolder = (AddButtonViewHolder) holder;
            addButtonViewHolder.getAddButton().setOnClickListener(v -> categoryAddClickListener.onCategoryAddClick((v)));
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        // Add 1 for the Add category button
        return localDataSet.size() + 1;
    }

    public void setCategories(List<Category> newCategories) {
        localDataSet.clear(); // Clear the existing categories
        localDataSet.addAll(newCategories); // Add all new categories
        notifyDataSetChanged(); // Notify the adapter about the dataset change
    }

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }

    public interface OnCategoryAddClickListener {
        void onCategoryAddClick(View v);
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        private ShapeableImageView iconImageView;
        private TextView title;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            iconImageView = itemView.findViewById(R.id.categoryIcon);
            title = itemView.findViewById(R.id.title);
        }

        public void bind(Category category) {
            int color = category.getColor();
            // get icon name, and then retrieve enum of this icon
            String iconName = category.getIcon();
            Icon icon = Icon.valueOf(iconName);

            String categoryTitle = category.getTitle();

            // Set icon
            iconImageView.setImageResource(icon.getResId());

            // Set icon background
            iconImageView.setBackgroundColor(ColorUtils.darkenColor(color, 0.6f));

            // Set icon color
            ColorStateList colorStateList = ColorStateList.valueOf(color);
            iconImageView.setImageTintList(colorStateList);

            // Set category title
            title.setText(categoryTitle);
        }
    }

    public static class AddButtonViewHolder extends RecyclerView.ViewHolder {
        private final LinearLayout addButton;

        public AddButtonViewHolder(@NonNull View itemView) {
            super(itemView);
            addButton = itemView.findViewById(R.id.add_button);
        }

        public LinearLayout getAddButton() {
            return addButton;
        }
    }
}
