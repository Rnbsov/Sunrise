package com.example.sunrise.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sunrise.R;
import com.example.sunrise.models.WorkspaceTask;
import com.example.sunrise.services.UserService;
import com.google.android.material.chip.Chip;

import java.util.List;

public class WorkspaceTaskAdapter extends RecyclerView.Adapter<WorkspaceTaskAdapter.WorkspaceTaskViewHolder> {

    private final List<WorkspaceTask> localDataSet;
    private final OnTaskClickListener taskClickListener;

    public WorkspaceTaskAdapter(List<WorkspaceTask> dataSet, OnTaskClickListener taskClickListener) {
        this.localDataSet = dataSet;
        this.taskClickListener = taskClickListener;
    }

    @NonNull
    @Override
    public WorkspaceTaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.workspace_task_item_layout, parent, false);
        return new WorkspaceTaskViewHolder(localDataSet, view, taskClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkspaceTaskViewHolder holder, int position) {
        WorkspaceTask task = localDataSet.get(position);
        holder.bind(task);
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    public void updateData(List<WorkspaceTask> newData) {
        WorkspaceTasksDiffCallback diffCallback = new WorkspaceTasksDiffCallback(localDataSet, newData);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        localDataSet.clear();
        localDataSet.addAll(newData);

        diffResult.dispatchUpdatesTo(this);
    }

    public interface OnTaskClickListener {
        void onTaskClick(WorkspaceTask task);
    }

    public static class WorkspaceTaskViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleTextView;
        private final Chip statusChip;
        private final Chip priorityChip;
        private final Chip assignChip;
        private final OnTaskClickListener taskClickListener;
        private final List<WorkspaceTask> localDataSet;

        public WorkspaceTaskViewHolder(List<WorkspaceTask> localDataSet, @NonNull View itemView, OnTaskClickListener taskClickListener) {
            super(itemView);

            this.localDataSet = localDataSet;
            this.taskClickListener = taskClickListener;

            titleTextView = itemView.findViewById(R.id.title);
            statusChip = itemView.findViewById(R.id.status_chip);
            priorityChip = itemView.findViewById(R.id.priority_chip);
            assignChip = itemView.findViewById(R.id.assigned_chip);
        }

        public void bind(WorkspaceTask task) {
            titleTextView.setText(task.getTitle());
            statusChip.setText(task.getStatus());
            priorityChip.setText(task.getPriority());

            // fetch and set assignee name
            fetchUserName(task.getAssignedUserId());

            // Set task click listener
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && taskClickListener != null) {
                    WorkspaceTask clickedTask = localDataSet.get(position);
                    taskClickListener.onTaskClick(clickedTask);
                }
            });
        }

        private void fetchUserName(String userId) {
            UserService userService = new UserService();
            userService.getUsername(userId, userName -> {
                // Set the fetched username to the assignChip
                assignChip.setText(userName);
            });
        }

    }

    private static class WorkspaceTasksDiffCallback extends DiffUtil.Callback {

        private final List<WorkspaceTask> oldTasks;
        private final List<WorkspaceTask> newTasks;

        public WorkspaceTasksDiffCallback(List<WorkspaceTask> oldTasks, List<WorkspaceTask> newTasks) {
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
            WorkspaceTask oldTask = oldTasks.get(oldItemPosition);
            WorkspaceTask newTask = newTasks.get(newItemPosition);
            return oldTask.getTitle().equals(newTask.getTitle())
                    && oldTask.getPriority().equals(newTask.getPriority())
                    && oldTask.getStatus().equals(newTask.getStatus())
                    && oldTask.getAssignedUserId().equals(newTask.getAssignedUserId())
                    && oldTask.getUpdatedAt() == newTask.getUpdatedAt();
        }
    }
}
