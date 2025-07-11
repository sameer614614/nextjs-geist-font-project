package com.smsapp.messages.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.smsapp.messages.models.SMSMessage;

public class SMSReceiver extends BroadcastReceiver {
    
    private static final String TAG = "SMSReceiver";
    
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "SMS received: " + intent.getAction());
        
        try {
            if (intent.getAction() != null) {
                switch (intent.getAction()) {
                    case Telephony.Sms.Intents.SMS_RECEIVED_ACTION:
                    case Telephony.Sms.Intents.SMS_DELIVER_ACTION:
                        handleIncomingSms(context, intent);
                        break;
                    default:
                        Log.d(TAG, "Unknown action: " + intent.getAction());
                        break;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error processing SMS", e);
        }
    }
    
    private void handleIncomingSms(Context context, Intent intent) {
        try {
            SmsMessage[] messages = null;
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                // For KitKat and above, use Telephony.Sms.Intents.getMessagesFromIntent
                messages = Telephony.Sms.Intents.getMessagesFromIntent(intent);
            } else {
                // For older versions, extract manually
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    String format = bundle.getString("format");
                    
                    if (pdus != null) {
                        messages = new SmsMessage[pdus.length];
                        for (int i = 0; i < pdus.length; i++) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i], format);
                            } else {
                                messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                            }
                        }
                    }
                }
            }
            
            if (messages != null && messages.length > 0) {
                processSmsMessages(context, messages);
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error handling incoming SMS", e);
        }
    }
    
    private void processSmsMessages(Context context, SmsMessage[] messages) {
        try {
            StringBuilder messageBody = new StringBuilder();
            String senderNumber = null;
            long timestamp = System.currentTimeMillis();
            
            // Combine all message parts
            for (SmsMessage smsMessage : messages) {
                if (smsMessage != null) {
                    if (senderNumber == null) {
                        senderNumber = smsMessage.getDisplayOriginatingAddress();
                        timestamp = smsMessage.getTimestampMillis();
                    }
                    messageBody.append(smsMessage.getMessageBody());
                }
            }
            
            if (senderNumber != null && messageBody.length() > 0) {
                // Create SMS message object
                SMSMessage smsMsg = new SMSMessage(
                    senderNumber,
                    messageBody.toString(),
                    timestamp,
                    false // This is a received message
                );
                
                Log.d(TAG, "SMS processed: " + smsMsg.toString());
                
                // Store the message (you can implement database storage here)
                storeSmsMessage(context, smsMsg);
                
                // Notify the app about new message
                notifyNewMessage(context, smsMsg);
                
                // Show notification (optional)
                showNotification(context, smsMsg);
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error processing SMS messages", e);
        }
    }
    
    private void storeSmsMessage(Context context, SMSMessage smsMessage) {
        try {
            // Here you can implement database storage or use ContentResolver
            // For now, we'll just log it
            Log.d(TAG, "Storing SMS: " + smsMessage.toString());
            
            // You can also broadcast the message to update UI
            Intent broadcastIntent = new Intent("com.smsapp.messages.NEW_SMS");
            broadcastIntent.putExtra("sender", smsMessage.getSenderNumber());
            broadcastIntent.putExtra("message", smsMessage.getMessageBody());
            broadcastIntent.putExtra("timestamp", smsMessage.getTimestamp());
            context.sendBroadcast(broadcastIntent);
            
        } catch (Exception e) {
            Log.e(TAG, "Error storing SMS message", e);
        }
    }
    
    private void notifyNewMessage(Context context, SMSMessage smsMessage) {
        try {
            // Send local broadcast to update UI if app is running
            Intent intent = new Intent("com.smsapp.messages.SMS_RECEIVED");
            intent.putExtra("sms_message", smsMessage.toString());
            context.sendBroadcast(intent);
            
        } catch (Exception e) {
            Log.e(TAG, "Error notifying new message", e);
        }
    }
    
    private void showNotification(Context context, SMSMessage smsMessage) {
        try {
            // For now, just show a toast (you can implement proper notifications later)
            String notificationText = "New SMS from " + smsMessage.getSenderNumber();
            Toast.makeText(context, notificationText, Toast.LENGTH_SHORT).show();
            
        } catch (Exception e) {
            Log.e(TAG, "Error showing notification", e);
        }
    }
}
