package com.example.sunrise.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    public MembersAdapter(List<User> dataSet) {
        this.localDataSet = dataSet;
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

        Picasso.get().load(member.getProfilePhotoUri()).into(avatarImageView);
        nicknameTextView.setText(member.getNickname());
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

        public MemberViewHolder(@NonNull View itemView) {
            super(itemView);
            avatarImageView = itemView.findViewById(R.id.user_avatar);
            nicknameTextView = itemView.findViewById(R.id.user_nickname);
        }

        public ShapeableImageView getAvatarImageView() {
            return avatarImageView;
        }

        public TextView getNicknameTextView() {
            return nicknameTextView;
        }
    }
}
