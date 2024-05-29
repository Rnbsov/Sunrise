package com.example.sunrise.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sunrise.R;
import com.example.sunrise.models.User;
import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.MemberViewHolder> {

    private final List<User> localDataSet;
    private final String creatorId;
    private final List<String> adminIds;

    public MembersAdapter(List<User> dataSet, String creatorId, List<String> adminIds) {
        this.localDataSet = dataSet;
        this.creatorId = creatorId;
        this.adminIds = adminIds;
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

        public MemberViewHolder(@NonNull View itemView) {
            super(itemView);
            avatarImageView = itemView.findViewById(R.id.user_avatar);
            nicknameTextView = itemView.findViewById(R.id.user_nickname);
            memberRoleImageView = itemView.findViewById(R.id.member_role);
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
    }
}
