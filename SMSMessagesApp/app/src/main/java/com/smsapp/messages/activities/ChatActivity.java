package com.smsapp.messages.activities;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.smsapp.messages.R;
import com.smsapp.messages.adapters.ChatAdapter;
import com.smsapp.messages.models.SMSMessage;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    
    private static final String TAG = "ChatActivity";
    private static final String SMS_SENT = "SMS_SENT";
    private static final String SMS_DELIVERED = "SMS_DELIVERED";
    
    private Toolbar toolbar;
    private RecyclerView recyclerViewChat;
    private TextInputEditText editTextMessage;
    private MaterialButton buttonSend;
    
    private ChatAdapter chatAdapter;
    private List<SMSMessage> chatMessages;
    
    private String phoneNumber;
    private String contactName;
    
    private BroadcastReceiver sentReceiver;
    private BroadcastReceiver deliveredReceiver;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        
        // Get data from intent
        getIntentData();
        
        initializeViews();
        setupToolbar();
        setupRecyclerView();
        setupSendButton();
        setupBroadcastReceivers();
        
        // Load chat messages
        loadChatMessages();
    }
    
    private void getIntentData() {
        Intent intent = getIntent();
        phoneNumber = intent.getStringExtra("phone_number");
        contactName = intent.getStringExtra("contact_name");
        
        if (TextUtils.isEmpty(phoneNumber)) {
            Toast.makeText(this, "Invalid phone number", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        if (TextUtils.isEmpty(contactName)) {
            contactName = phoneNumber;
        }
    }
    
    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        recyclerViewChat = findViewById(R.id.recyclerViewChat);
        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSend = findViewById(R.id.buttonSend);
    }
    
    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(contactName);
        }
    }
    
    private void setupRecyclerView() {
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(this);
        
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true); // Start from bottom
        
        recyclerViewChat.setLayoutManager(layoutManager);
        recyclerViewChat.setAdapter(chatAdapter);
    }
    
    private void setupSendButton() {
        buttonSend.setOnClickListener(v -> sendMessage());
        
        // Enable/disable send button based on message input
        editTextMessage.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                buttonSend.setEnabled(!TextUtils.isEmpty(s.toString().trim()));
            }
            
            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });
    }
    
    private void setupBroadcastReceivers() {
        // SMS sent receiver
        sentReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case android.app.Activity.RESULT_OK:
                        Toast.makeText(ChatActivity.this, R.string.sms_sent, Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(ChatActivity.this, R.string.sms_failed, Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(ChatActivity.this, "No service", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(ChatActivity.this, "Null PDU", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(ChatActivity.this, "Radio off", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
        
        // SMS delivered receiver
        deliveredReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case android.app.Activity.RESULT_OK:
                        Toast.makeText(ChatActivity.this, "SMS delivered", Toast.LENGTH_SHORT).show();
                        break;
                    case android.app.Activity.RESULT_CANCELED:
                        Toast.makeText(ChatActivity.this, "SMS not delivered", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
    }
    
    private void sendMessage() {
        String messageText = editTextMessage.getText().toString().trim();
        
        if (TextUtils.isEmpty(messageText)) {
            Toast.makeText(this, R.string.empty_message, Toast.LENGTH_SHORT).show();
            return;
        }
        
        try {
            SmsManager smsManager = SmsManager.getDefault();
            
            // Create pending intents for sent and delivered
            PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, 
                    new Intent(SMS_SENT), PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0, 
                    new Intent(SMS_DELIVERED), PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            
            // Send SMS
            smsManager.sendTextMessage(phoneNumber, null, messageText, sentPI, deliveredPI);
            
            // Add message to chat immediately
            SMSMessage sentMessage = new SMSMessage(
                    phoneNumber,
                    messageText,
                    System.currentTimeMillis(),
                    true // This is a sent message
            );
            
            chatMessages.add(sentMessage);
            chatAdapter.addMessage(sentMessage);
            
            // Scroll to bottom
            recyclerViewChat.scrollToPosition(chatAdapter.getItemCount() - 1);
            
            // Clear input
            editTextMessage.setText("");
            
            Log.d(TAG, "SMS sent to: " + phoneNumber + ", Message: " + messageText);
            
        } catch (Exception e) {
            Log.e(TAG, "Error sending SMS", e);
            Toast.makeText(this, R.string.sms_failed, Toast.LENGTH_SHORT).show();
        }
    }
    
    private void loadChatMessages() {
        new Thread(() -> {
            try {
                List<SMSMessage> messages = getChatMessages(phoneNumber);
                
                runOnUiThread(() -> {
                    chatMessages.clear();
                    chatMessages.addAll(messages);
                    chatAdapter.setMessages(chatMessages);
                    
                    // Scroll to bottom
                    if (!chatMessages.isEmpty()) {
                        recyclerViewChat.scrollToPosition(chatAdapter.getItemCount() - 1);
                    }
                });
                
            } catch (Exception e) {
                Log.e(TAG, "Error loading chat messages", e);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error loading messages", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }
    
    private List<SMSMessage> getChatMessages(String phoneNumber) {
        List<SMSMessage> messages = new ArrayList<>();
        
        try {
            ContentResolver contentResolver = getContentResolver();
            Uri uri = Telephony.Sms.CONTENT_URI;
            
            String[] projection = {
                    Telephony.Sms._ID,
                    Telephony.Sms.ADDRESS,
                    Telephony.Sms.BODY,
                    Telephony.Sms.DATE,
                    Telephony.Sms.TYPE
            };
            
            String selection = Telephony.Sms.ADDRESS + " = ?";
            String[] selectionArgs = {phoneNumber};
            String sortOrder = Telephony.Sms.DATE + " ASC";
            
            Cursor cursor = contentResolver.query(uri, projection, selection, selectionArgs, sortOrder);
            
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    long id = cursor.getLong(cursor.getColumnIndexOrThrow(Telephony.Sms._ID));
                    String address = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.ADDRESS));
                    String body = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.BODY));
                    long date = cursor.getLong(cursor.getColumnIndexOrThrow(Telephony.Sms.DATE));
                    int type = cursor.getInt(cursor.getColumnIndexOrThrow(Telephony.Sms.TYPE));
                    
                    boolean isSent = (type == Telephony.Sms.MESSAGE_TYPE_SENT);
                    
                    SMSMessage smsMessage = new SMSMessage(id, address, body, date, isSent);
                    messages.add(smsMessage);
                }
                cursor.close();
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error querying chat messages", e);
        }
        
        return messages;
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        
        // Register broadcast receivers
        registerReceiver(sentReceiver, new IntentFilter(SMS_SENT));
        registerReceiver(deliveredReceiver, new IntentFilter(SMS_DELIVERED));
        
        // Refresh messages
        loadChatMessages();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        
        // Unregister broadcast receivers
        try {
            unregisterReceiver(sentReceiver);
            unregisterReceiver(deliveredReceiver);
        } catch (Exception e) {
            Log.e(TAG, "Error unregistering receivers", e);
        }
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
