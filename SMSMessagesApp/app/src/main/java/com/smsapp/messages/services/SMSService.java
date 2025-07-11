package com.smsapp.messages.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class SMSService extends Service {
    
    private static final String TAG = "SMSService";
    
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "SMS Service created");
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "SMS Service started");
        
        // Return START_STICKY to restart service if killed by system
        return START_STICKY;
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        // This service doesn't support binding
        return null;
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "SMS Service destroyed");
    }
}
