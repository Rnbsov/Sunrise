package com.example.sunrise.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sunrise.R;
import com.example.sunrise.models.User;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.MemberViewHolder> {

    private final List<User> localDataSet;
    private final String creatorId;
    private final List<String> adminIds;
    private final Context context;

    public MembersAdapter(List<User> dataSet, String creatorId, List<String> adminIds, Context context) {
        this.localDataSet = dataSet;
        this.creatorId = creatorId;
        this.adminIds = adminIds;
        this.context = context;
    }

    @NonNull
    @Override
    public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.member_item_layout, parent, false);
        return new MemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberViewHolder holder, int position) {
        User member = localDataSet.get(position);
        ShapeableImageView avatarImageView = holder.getAvatarImageView();
        TextView nicknameTextView = holder.getNicknameTextView();
        ImageView memberRoleImageView = holder.getMemberRoleImageView();

        Picasso.get().load(member.getProfilePhotoUri()).into(avatarImageView); // Set member avatar
        nicknameTextView.setText(member.getNickname()); // Set member nickname

        // Set the role icon based on user role
        if (member.getUserId().equals(creatorId)) {
            // If user is creator of the workspace set stars icon
            memberRoleImageView.setImageResource(R.drawable.stars_24px);
            memberRoleImageView.setVisibility(View.VISIBLE);
        } else if (adminIds.contains(member.getUserId())) {
            // If user is admin of the workspace set stars icon
            memberRoleImageView.setImageResource(R.drawable.shield_person_24px);
            memberRoleImageView.setVisibility(View.VISIBLE);
        }

        // Show the member actions if the current user is an admin
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (adminIds.contains(currentUser.getUid())) {
            holder.getMemberActions().setVisibility(View.VISIBLE);
            holder.getMemberActions().setOnClickListener(v -> showPopupMenu(v, member));
        }
    }

    private void showPopupMenu(View view, User member) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.member_actions_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> onMenuItemClick(item, member));
        popupMenu.show();
    }

    private boolean onMenuItemClick(MenuItem item, User member) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_promote) {
            Toast.makeText(context, "Not implemented", Toast.LENGTH_SHORT).show();
            return true;
        } else if (itemId == R.id.action_mute) {
            Toast.makeText(context, "Not implemented", Toast.LENGTH_SHORT).show();
            return true;
        } else if (itemId == R.id.action_kick) {
            Toast.makeText(context, "Not implemented", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    public void setMembers(List<User> newMembers) {
        localDataSet.clear();
        localDataSet.addAll(newMembers);
        notifyDataSetChanged();
    }

    public static class MemberViewHolder extends RecyclerView.ViewHolder {
        private final ShapeableImageView avatarImageView;
        private final TextView nicknameTextView;
        private final ImageView memberRoleImageView;
        private final ImageView memberActions;

        public MemberViewHolder(@NonNull View itemView) {
            super(itemView);
            avatarImageView = itemView.findViewById(R.id.user_avatar);
            nicknameTextView = itemView.findViewById(R.id.user_nickname);
            memberRoleImageView = itemView.findViewById(R.id.member_role);
            memberActions = itemView.findViewById(R.id.member_actions);
        }

        public ShapeableImageView getAvatarImageView() {
            return avatarImageView;
        }

        public TextView getNicknameTextView() {
            return nicknameTextView;
        }

        public ImageView getMemberRoleImageView() {
            return memberRoleImageView;
        }

        public ImageView getMemberActions() {
            return memberActions;
        }
    }
}
