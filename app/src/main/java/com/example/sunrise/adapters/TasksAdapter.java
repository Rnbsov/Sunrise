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

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.ViewHolder> {

    private static List<Task> localDataSet;
    private final onCheckboxClickedListener onCheckedChangeListener;

    /**
     * Initialize the dataset of the Adapter
     *
     * @param dataSet String[] containing the data to populate views to be used
     * by RecyclerView
     */
    public TasksAdapter(List<Task> dataSet, onCheckboxClickedListener onCheckedChangeListener) {
        localDataSet = dataSet;
        this.onCheckedChangeListener = onCheckedChangeListener;
    }

    public interface onCheckboxClickedListener {
        void onCompleted(Task task, TextView title, boolean isChecked);
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.task_item_layout, viewGroup, false);

        return new ViewHolder(view, onCheckedChangeListener);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
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
        // Sorting so that uncompleted tasks go first to completed ones
        newData.sort((task1, task2) -> {
            if (task1.isCompleted() && !task2.isCompleted()) {
                // If task1 is completed and task2 is uncompleted, task2 should come first
                return 1;
            } else if (!task1.isCompleted() && task2.isCompleted()) {
                // If task1 is uncompleted and task2 is completed, task1 should come first
                return -1;
            } else {
                // Otherwise, maintain the original order
                return 0;
            }
        });

        TasksListDiffCallback diffCallback = new TasksListDiffCallback(localDataSet, newData);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        localDataSet.clear();
        localDataSet.addAll(newData);

        diffResult.dispatchUpdatesTo(this);
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView title;
        private final CheckBox completeCheckbox;
        private final Chip priority;

        public ViewHolder(View itemView, onCheckboxClickedListener onCheckboxClick) {
            super(itemView);
            // TODO: Define click listener for the ViewHolder's View

            title = itemView.findViewById(R.id.title);
            completeCheckbox = itemView.findViewById(R.id.checkbox);
            priority = itemView.findViewById(R.id.priorityChip);

            completeCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                int position = getAdapterPosition();

                Task updatedTask = localDataSet.get(position);

                onCheckboxClick.onCompleted(updatedTask, title, isChecked);
            });
        }

        public void bind(Task task) {
            title.setText(task.getTitle());
            completeCheckbox.setChecked(task.isCompleted());
            priority.setText(task.getPriority());
        }

        public TextView getTextView() {
            return title;
        }

        public CheckBox getCompleteCheckbox() {
            return completeCheckbox;
        }

        public Chip getPriorityChip() {
            return priority;
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

