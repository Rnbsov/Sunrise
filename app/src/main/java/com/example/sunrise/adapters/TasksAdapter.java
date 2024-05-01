package com.example.sunrise.adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sunrise.R;
import com.example.sunrise.models.Task;
import com.google.android.material.chip.Chip;

import java.util.List;

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.TaskViewHolder> {

    private final List<Task> localDataSet;
    private final OnTaskCheckedChangeListener onCheckedChangeListener;
    private final OnItemClickedListener onItemClickedListener;

    /**
     * Initialize the dataset of the Adapter
     *
     * @param dataSet String[] containing the data to populate views to be used
     * by RecyclerView
     */
    public TasksAdapter(List<Task> dataSet, OnTaskCheckedChangeListener onCheckedChangeListener, OnItemClickedListener onItemClickedListener) {
        this.localDataSet = dataSet;
        this.onCheckedChangeListener = onCheckedChangeListener;
        this.onItemClickedListener = onItemClickedListener;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.task_item_layout, viewGroup, false);

        return new TaskViewHolder(localDataSet, view, onCheckedChangeListener, onItemClickedListener);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(TaskViewHolder viewHolder, final int position) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        Task task = localDataSet.get(position);
        viewHolder.bind(task);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    public void updateData(List<Task> newData) {
        TasksListDiffCallback diffCallback = new TasksListDiffCallback(localDataSet, newData);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        localDataSet.clear();
        localDataSet.addAll(newData);

        diffResult.dispatchUpdatesTo(this);
    }

    public interface OnTaskCheckedChangeListener {
        void onCompleted(Task task, TextView title, boolean isChecked);
    }

    public interface OnItemClickedListener {
        void itemClicked(Task task);
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        private final TextView title;
        private final CheckBox completeCheckbox;
        private final Chip priority;

        public TaskViewHolder(List<Task> localDataSet, View itemView, OnTaskCheckedChangeListener onCheckboxClick, OnItemClickedListener onItemClickedListener) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            completeCheckbox = itemView.findViewById(R.id.checkbox);
            priority = itemView.findViewById(R.id.priorityChip);

            // Setting on complete checkbox click listener
            completeCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && onItemClickedListener != null) {
                    Task updatedTask = localDataSet.get(position);
                    onCheckboxClick.onCompleted(updatedTask, title, isChecked);
                }
            });

            // Setting on task click listener
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && onItemClickedListener != null) {
                    Task clickedTask = localDataSet.get(position);
                    onItemClickedListener.itemClicked(clickedTask);
                }
            });
        }

        public void bind(Task task) {
            title.setText(task.getTitle());
            completeCheckbox.setChecked(task.isCompleted());
            priority.setText(task.getPriority());
        }
    }

    private static class TasksListDiffCallback extends DiffUtil.Callback {

        private final List<Task> oldTasks;
        private final List<Task> newTasks;

        public TasksListDiffCallback(List<Task> oldTasks, List<Task> newTasks) {
            this.oldTasks = oldTasks;
            this.newTasks = newTasks;
        }

        @Override
        public int getOldListSize() {
            return oldTasks.size();
        }

        @Override
        public int getNewListSize() {
            return newTasks.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            String oldTaskId = oldTasks.get(oldItemPosition).getTaskId();
            String newTaskId = newTasks.get(newItemPosition).getTaskId();
            return oldTaskId.equals(newTaskId);
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            Task oldTask = oldTasks.get(oldItemPosition);
            Task newTask = newTasks.get(newItemPosition);
            return oldTask.getTitle().equals(newTask.getTitle())
                    && oldTask.getPriority().equals(newTask.getPriority())
                    && oldTask.isCompleted() == newTask.isCompleted()
                    && oldTask.getCompletedAt() == newTask.getCompletedAt()
                    && oldTask.getUpdatedAt() == newTask.getUpdatedAt();
        }
    }
}

