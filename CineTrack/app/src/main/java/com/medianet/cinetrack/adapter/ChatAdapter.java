package com.medianet.cinetrack.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.medianet.cinetrack.R;
import com.medianet.cinetrack.ui.chat.ChatMessage;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_USER = 0;
    private static final int TYPE_AI   = 1;

    private List<ChatMessage> messages = new ArrayList<>();

    public void setMessages(List<ChatMessage> list) {
        this.messages = new ArrayList<>(list);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).type == ChatMessage.Type.USER ? TYPE_USER : TYPE_AI;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inf = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_USER) {
            return new UserVH(inf.inflate(R.layout.item_message_user, parent, false));
        }
        return new AiVH(inf.inflate(R.layout.item_message_ai, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage msg = messages.get(position);
        if (holder instanceof UserVH) {
            ((UserVH) holder).textMessage.setText(msg.text);
        } else if (holder instanceof AiVH) {
            AiVH ai = (AiVH) holder;
            if (msg.type == ChatMessage.Type.LOADING) {
                ai.textMessage.setText("…");
            } else {
                ai.textMessage.setText(msg.text);
            }
        }
    }

    @Override
    public int getItemCount() { return messages.size(); }

    static class UserVH extends RecyclerView.ViewHolder {
        TextView textMessage;
        UserVH(View v) { super(v); textMessage = v.findViewById(R.id.textMessage); }
    }

    static class AiVH extends RecyclerView.ViewHolder {
        TextView textMessage;
        AiVH(View v) { super(v); textMessage = v.findViewById(R.id.textMessage); }
    }
}
