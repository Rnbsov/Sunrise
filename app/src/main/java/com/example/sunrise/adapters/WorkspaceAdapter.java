package com.example.sunrise.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sunrise.R;
import com.example.sunrise.models.Workspace;

import java.util.List;

public class WorkspaceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_WORKSPACE = 0;
    private static final int VIEW_TYPE_ADD_BUTTON = 1;

    private final List<Workspace> localDataSet;
    private final OnWorkspaceClickListener workspaceClickListener;
    private final OnWorkspaceAddClickListener workspaceAddClickListener;

    public WorkspaceAdapter(List<Workspace> dataSet, OnWorkspaceClickListener workspaceClickListener, OnWorkspaceAddClickListener workspaceAddClickListener) {
        this.localDataSet = dataSet;
        this.workspaceClickListener = workspaceClickListener;
        this.workspaceAddClickListener = workspaceAddClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        return position == localDataSet.size() ? VIEW_TYPE_ADD_BUTTON : VIEW_TYPE_WORKSPACE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_WORKSPACE) {
            View view = inflater.inflate(R.layout.workspace_item_layout, parent, false);
            return new WorkspaceViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.workspace_add_button_layout, parent, false);
            return new AddButtonViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof WorkspaceViewHolder) {
            WorkspaceViewHolder workspaceViewHolder = (WorkspaceViewHolder) holder;
            Workspace workspace = localDataSet.get(position);
            workspaceViewHolder.bind(workspace);
            workspaceViewHolder.itemView.setOnClickListener(v -> workspaceClickListener.onWorkspaceClick(workspace));
        } else if (holder instanceof AddButtonViewHolder) {
            AddButtonViewHolder addButtonViewHolder = (AddButtonViewHolder) holder;
            addButtonViewHolder.getAddButton().setOnClickListener(v -> workspaceAddClickListener.onWorkspaceAddClick(v));
        }
    }

    @Override
    public int getItemCount() {
        // Add 1 for the Add workspace button
        return localDataSet.size() + 1;
    }

    public void setWorkspaces(List<Workspace> newWorkspaces) {
        localDataSet.clear(); // Clear the existing workspaces
        localDataSet.addAll(newWorkspaces); // Add all new workspaces
        notifyDataSetChanged(); // Notify the adapter about the dataset change
    }

    public interface OnWorkspaceClickListener {
        void onWorkspaceClick(Workspace workspace);
    }

    public interface OnWorkspaceAddClickListener {
        void onWorkspaceAddClick(View v);
    }

    public static class WorkspaceViewHolder extends RecyclerView.ViewHolder {
        private TextView title;

        public WorkspaceViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.workspace_title);
        }

        public void bind(Workspace workspace) {
            title.setText(workspace.getTitle());
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
