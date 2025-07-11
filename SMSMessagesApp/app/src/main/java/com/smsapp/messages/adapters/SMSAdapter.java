package com.smsapp.messages.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class SMSAdapter extends RecyclerView.Adapter<SMSAdapter.SMSViewHolder> {
    
    private Context context;
    private List<SMSMessage> smsMessages;
    private OnSMSClickListener onSMSClickListener;
    private SimpleDateFormat timeFormat;
    
    public interface OnSMSClickListener {
        void onSMSClick(SMSMessage smsMessage);
    }
    
    public SMSAdapter(Context context) {
        this.context = context;
        this.smsMessages = new ArrayList<>();
        this.timeFormat = new SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault());
    }
    
    public void setSmsMessages(List<SMSMessage> smsMessages) {
        this.smsMessages = smsMessages != null ? smsMessages : new ArrayList<>();
        notifyDataSetChanged();
    }
    
    public void addSmsMessage(SMSMessage smsMessage) {
        if (smsMessage != null) {
            this.smsMessages.add(0, smsMessage); // Add to top
            notifyItemInserted(0);
        }
    }
    
    public void setOnSMSClickListener(OnSMSClickListener listener) {
        this.onSMSClickListener = listener;
    }
    
    @NonNull
    @Override
    public SMSViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_conversation, parent, false);
        return new SMSViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull SMSViewHolder holder, int position) {
        SMSMessage smsMessage = smsMessages.get(position);
        holder.bind(smsMessage);
    }
    
    @Override
    public int getItemCount() {
        return smsMessages.size();
    }
    
    class SMSViewHolder extends RecyclerView.ViewHolder {
        
        private TextView textViewAvatar;
        private TextView textViewContactName;
        private TextView textViewLastMessage;
        private TextView textViewTime;
        
        public SMSViewHolder(@NonNull View itemView) {
            super(itemView);
            
            textViewAvatar = itemView.findViewById(R.id.textViewAvatar);
            textViewContactName = itemView.findViewById(R.id.textViewContactName);
            textViewLastMessage = itemView.findViewById(R.id.textViewLastMessage);
            textViewTime = itemView.findViewById(R.id.textViewTime);
            
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && onSMSClickListener != null) {
                    onSMSClickListener.onSMSClick(smsMessages.get(position));
                }
            });
        }
        
        public void bind(SMSMessage smsMessage) {
            try {
                // Set contact name (phone number for now)
                String contactName = getContactName(smsMessage.getSenderNumber());
                textViewContactName.setText(contactName);
                
                // Set avatar (first letter of contact name or phone number)
                String avatarText = getAvatarText(contactName);
                textViewAvatar.setText(avatarText);
                
                // Set last message
                String messagePreview = smsMessage.getMessageBody();
                if (messagePreview.length() > 50) {
                    messagePreview = messagePreview.substring(0, 50) + "...";
                }
                textViewLastMessage.setText(messagePreview);
                
                // Set time
                String timeText = timeFormat.format(new Date(smsMessage.getTimestamp()));
                textViewTime.setText(timeText);
                
            } catch (Exception e) {
                // Handle any binding errors gracefully
                textViewContactName.setText(smsMessage.getSenderNumber());
                textViewLastMessage.setText(smsMessage.getMessageBody());
                textViewTime.setText("");
                textViewAvatar.setText("?");
            }
        }
        
        private String getContactName(String phoneNumber) {
            // For now, return phone number
            // Later, you can implement contact lookup
            if (phoneNumber != null && phoneNumber.length() > 10) {
                return phoneNumber.substring(phoneNumber.length() - 10);
            }
            return phoneNumber != null ? phoneNumber : "Unknown";
        }
        
        private String getAvatarText(String contactName) {
            if (contactName != null && !contactName.isEmpty()) {
                if (contactName.matches("\\d+")) {
                    // If it's a phone number, use last 2 digits
                    return contactName.length() >= 2 ? 
                           contactName.substring(contactName.length() - 2) : contactName;
                } else {
                    // If it's a name, use first letter
                    return contactName.substring(0, 1).toUpperCase();
                }
            }
            return "?";
        }
    }
}
