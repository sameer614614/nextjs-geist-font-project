package com.smsapp.messages.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.smsapp.messages.R;
import com.smsapp.messages.models.SMSMessage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    
    private Context context;
    private List<SMSMessage> messages;
    private SimpleDateFormat timeFormat;
    
    public ChatAdapter(Context context) {
        this.context = context;
        this.messages = new ArrayList<>();
        this.timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    }
    
    public void setMessages(List<SMSMessage> messages) {
        this.messages = messages != null ? messages : new ArrayList<>();
        notifyDataSetChanged();
    }
    
    public void addMessage(SMSMessage message) {
        if (message != null) {
            this.messages.add(message);
            notifyItemInserted(messages.size() - 1);
        }
    }
    
    public void clearMessages() {
        this.messages.clear();
        notifyDataSetChanged();
    }
    
    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_message, parent, false);
        return new ChatViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        SMSMessage message = messages.get(position);
        holder.bind(message);
    }
    
    @Override
    public int getItemCount() {
        return messages.size();
    }
    
    class ChatViewHolder extends RecyclerView.ViewHolder {
        
        private LinearLayout layoutSentMessage;
        private LinearLayout layoutReceivedMessage;
        private TextView textViewSentMessage;
        private TextView textViewSentTime;
        private TextView textViewReceivedMessage;
        private TextView textViewReceivedTime;
        
        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            
            layoutSentMessage = itemView.findViewById(R.id.layoutSentMessage);
            layoutReceivedMessage = itemView.findViewById(R.id.layoutReceivedMessage);
            textViewSentMessage = itemView.findViewById(R.id.textViewSentMessage);
            textViewSentTime = itemView.findViewById(R.id.textViewSentTime);
            textViewReceivedMessage = itemView.findViewById(R.id.textViewReceivedMessage);
            textViewReceivedTime = itemView.findViewById(R.id.textViewReceivedTime);
        }
        
        public void bind(SMSMessage message) {
            try {
                String timeText = timeFormat.format(new Date(message.getTimestamp()));
                
                if (message.isSent()) {
                    // Show sent message
                    layoutSentMessage.setVisibility(View.VISIBLE);
                    layoutReceivedMessage.setVisibility(View.GONE);
                    
                    textViewSentMessage.setText(message.getMessageBody());
                    textViewSentTime.setText(timeText);
                } else {
                    // Show received message
                    layoutSentMessage.setVisibility(View.GONE);
                    layoutReceivedMessage.setVisibility(View.VISIBLE);
                    
                    textViewReceivedMessage.setText(message.getMessageBody());
                    textViewReceivedTime.setText(timeText);
                }
                
            } catch (Exception e) {
                // Handle binding errors gracefully
                layoutSentMessage.setVisibility(View.GONE);
                layoutReceivedMessage.setVisibility(View.VISIBLE);
                textViewReceivedMessage.setText(message.getMessageBody());
                textViewReceivedTime.setText("");
            }
        }
    }
}
