package com.smsapp.messages.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.smsapp.messages.services.SMSService;

public class BootReceiver extends BroadcastReceiver {
    
    private static final String TAG = "BootReceiver";
    
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Boot receiver triggered: " + intent.getAction());
        
        try {
            if (intent.getAction() != null) {
                switch (intent.getAction()) {
                    case Intent.ACTION_BOOT_COMPLETED:
                    case Intent.ACTION_MY_PACKAGE_REPLACED:
                    case Intent.ACTION_PACKAGE_REPLACED:
                        handleBootCompleted(context);
                        break;
                    default:
                        Log.d(TAG, "Unknown action: " + intent.getAction());
                        break;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error handling boot completed", e);
        }
    }
    
    private void handleBootCompleted(Context context) {
        try {
            Log.d(TAG, "Device boot completed, initializing SMS service");
            
            // Start SMS service if needed
            Intent serviceIntent = new Intent(context, SMSService.class);
            context.startService(serviceIntent);
            
            // You can add other initialization tasks here
            // For example, checking if app is still default SMS app
            
        } catch (Exception e) {
            Log.e(TAG, "Error initializing after boot", e);
        }
    }
}
