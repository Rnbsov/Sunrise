package com.example.sunrise.adapters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sunrise.R;

import java.util.List;

public class InviteCodeAdapter extends RecyclerView.Adapter<InviteCodeAdapter.InviteCodeViewHolder> {

    private final List<String> inviteCodes;
    private final Context context;

    public InviteCodeAdapter(List<String> inviteCodes, Context context) {
        this.inviteCodes = inviteCodes;
        this.context = context;
    }

    @NonNull
    @Override
    public InviteCodeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.invite_code_item_layout, parent, false);
        return new InviteCodeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InviteCodeViewHolder holder, int position) {
        String inviteCode = inviteCodes.get(position);
        holder.inviteCodeTextView.setText(inviteCode);

        // Set click listener to copy invite code to clipboard
        holder.copyInviteCodeImageView.setOnClickListener(v -> {
            ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("Invite Code", inviteCode);
            if (clipboardManager != null) {
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(context, "Invite code copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return inviteCodes.size();
    }

    public void setInviteCodes(List<String> newInviteCodes) {
        inviteCodes.clear();
        inviteCodes.addAll(newInviteCodes);
        notifyDataSetChanged();
    }

    public static class InviteCodeViewHolder extends RecyclerView.ViewHolder {
        TextView inviteCodeTextView;
        ImageView copyInviteCodeImageView;

        InviteCodeViewHolder(View itemView) {
            super(itemView);
            inviteCodeTextView = itemView.findViewById(R.id.invite_code);
            copyInviteCodeImageView = itemView.findViewById(R.id.copy_invite_code);
        }
    }
}
