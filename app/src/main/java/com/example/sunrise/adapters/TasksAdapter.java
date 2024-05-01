package com.example.sunrise.adapters;


import android.graphics.Paint;
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
import com.example.sunrise.services.TaskService;
import com.google.android.material.chip.Chip;

import java.util.List;

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.ViewHolder> {

    private final List<Task> localDataSet;

    /**
     * Initialize the dataset of the Adapter
     *
     * @param dataSet String[] containing the data to populate views to be used
     * by RecyclerView
     */
    public TasksAdapter(List<Task> dataSet) {
        localDataSet = dataSet;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.task_item_layout, viewGroup, false);

        final ViewHolder viewHolder = new ViewHolder(view);

        // Set checkbox click listener
        viewHolder.getCompleteCheckbox().setOnCheckedChangeListener((buttonView, isChecked) -> {
            int position = viewHolder.getAdapterPosition();

            // Update task completion status
            Task updatedTask = localDataSet.get(position);

            // Tick it
            updatedTask.setCompleted(isChecked);

            // Set completedAt
            if (isChecked) {
                updatedTask.setCompletedAt(System.currentTimeMillis()); // Set completion timestamp
            } else {
                updatedTask.setCompletedAt(0); // Reset completion timestamp
            }

            // Save updated task to Firebase
            TaskService taskService = new TaskService();
            taskService.updateTask(updatedTask);

            // Apply strikethrough style if the task is completed
            if (localDataSet.get(position).isCompleted()) {
                viewHolder.getTextView().setPaintFlags(viewHolder.getTextView().getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                // If task is not completed, it shouldn't be applied strikethrough style
                viewHolder.getTextView().setPaintFlags(viewHolder.getTextView().getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }
        });

        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.getTextView().setText(localDataSet.get(position).getTitle());
        viewHolder.getPriorityChip().setText(localDataSet.get(position).getPriority());
        viewHolder.getCompleteCheckbox().setChecked(localDataSet.get(position).isCompleted());
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

        public ViewHolder(View itemView) {
            super(itemView);
            // TODO: Define click listener for the ViewHolder's View

            title = itemView.findViewById(R.id.title);
            completeCheckbox = itemView.findViewById(R.id.checkbox);
            priority = itemView.findViewById(R.id.priorityChip);
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

