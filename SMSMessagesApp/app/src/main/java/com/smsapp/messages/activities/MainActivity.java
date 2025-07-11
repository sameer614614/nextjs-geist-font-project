package com.smsapp.messages.activities;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.smsapp.messages.R;
import com.smsapp.messages.adapters.SMSAdapter;
import com.smsapp.messages.models.Contact;
import com.smsapp.messages.models.SMSMessage;
import com.smsapp.messages.utils.PermissionUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements SMSAdapter.OnSMSClickListener {
    
    private static final String TAG = "MainActivity";
    
    private Toolbar toolbar;
    private RecyclerView recyclerViewMessages;
    private FloatingActionButton fabNewChat;
    private ProgressBar progressBar;
    private View emptyStateLayout;
    
    private SMSAdapter smsAdapter;
    private List<SMSMessage> smsMessages;
    private Map<String, Contact> contactsMap;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initializeViews();
        setupToolbar();
        setupRecyclerView();
        setupFAB();
        
        // Initialize data structures
        smsMessages = new ArrayList<>();
        contactsMap = new HashMap<>();
        
        // Check permissions and setup
        checkAndRequestPermissions();
    }
    
    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        recyclerViewMessages = findViewById(R.id.recyclerViewMessages);
        fabNewChat = findViewById(R.id.fabNewChat);
        progressBar = findViewById(R.id.progressBar);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);
    }
    
    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.app_name));
        }
    }
    
    private void setupRecyclerView() {
        smsAdapter = new SMSAdapter(this);
        smsAdapter.setOnSMSClickListener(this);
        
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMessages.setAdapter(smsAdapter);
    }
    
    private void setupFAB() {
        fabNewChat.setOnClickListener(v -> showNewChatDialog());
    }
    
    private void checkAndRequestPermissions() {
        // First check if app is default SMS app
        if (!PermissionUtil.isDefaultSmsApp(this)) {
            showDefaultSmsAppDialog();
            return;
        }
        
        // Then check runtime permissions
        if (!PermissionUtil.hasAllRequiredPermissions(this)) {
            showPermissionsDialog();
            return;
        }
        
        // Check battery optimization
        if (!PermissionUtil.isBatteryOptimizationDisabled(this)) {
            showBatteryOptimizationDialog();
            return;
        }
        
        // All permissions granted, load data
        loadSmsMessages();
    }
    
    private void showDefaultSmsAppDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.default_sms_app_title)
                .setMessage(R.string.default_sms_app_message)
                .setPositiveButton(R.string.set_default, (dialog, which) -> {
                    PermissionUtil.requestDefaultSmsApp(this);
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                    Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_LONG).show();
                    finish();
                })
                .setCancelable(false)
                .show();
    }
    
    private void showPermissionsDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.permissions_required_title)
                .setMessage(R.string.permissions_required_message)
                .setPositiveButton(R.string.grant_permissions, (dialog, which) -> {
                    PermissionUtil.requestAllPermissions(this);
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                    Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_LONG).show();
                    finish();
                })
                .setCancelable(false)
                .show();
    }
    
    private void showBatteryOptimizationDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.battery_optimization_title)
                .setMessage(R.string.battery_optimization_message)
                .setPositiveButton(R.string.ok, (dialog, which) -> {
                    PermissionUtil.requestBatteryOptimizationExemption(this);
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                    // Continue without battery optimization exemption
                    loadSmsMessages();
                })
                .show();
    }
    
    private void showNewChatDialog() {
        View dialogView = getLayoutInflater().inflate(android.R.layout.select_dialog_item, null);
        
        String[] options = {"Enter Phone Number", "Select from Contacts"};
        
        new AlertDialog.Builder(this)
                .setTitle("New Chat")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        showPhoneNumberDialog();
                    } else {
                        showContactsDialog();
                    }
                })
                .show();
    }
    
    private void showPhoneNumberDialog() {
        EditText editText = new EditText(this);
        editText.setHint(R.string.enter_phone_number);
        editText.setInputType(android.text.InputType.TYPE_CLASS_PHONE);
        
        new AlertDialog.Builder(this)
                .setTitle("Enter Phone Number")
                .setView(editText)
                .setPositiveButton(R.string.ok, (dialog, which) -> {
                    String phoneNumber = editText.getText().toString().trim();
                    if (!phoneNumber.isEmpty()) {
                        openChatActivity(phoneNumber, phoneNumber);
                    } else {
                        Toast.makeText(this, R.string.invalid_number, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }
    
    private void showContactsDialog() {
        // Load contacts and show in dialog
        loadContacts();
        
        if (contactsMap.isEmpty()) {
            Toast.makeText(this, "No contacts found", Toast.LENGTH_SHORT).show();
            return;
        }
        
        List<String> contactNames = new ArrayList<>();
        List<String> phoneNumbers = new ArrayList<>();
        
        for (Contact contact : contactsMap.values()) {
            contactNames.add(contact.getDisplayName());
            phoneNumbers.add(contact.getPhoneNumber());
        }
        
        String[] contactArray = contactNames.toArray(new String[0]);
        
        new AlertDialog.Builder(this)
                .setTitle(R.string.select_contact)
                .setItems(contactArray, (dialog, which) -> {
                    String contactName = contactNames.get(which);
                    String phoneNumber = phoneNumbers.get(which);
                    openChatActivity(phoneNumber, contactName);
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }
    
    private void openChatActivity(String phoneNumber, String contactName) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("phone_number", phoneNumber);
        intent.putExtra("contact_name", contactName);
        startActivity(intent);
    }
    
    private void loadSmsMessages() {
        showLoading(true);
        
        new Thread(() -> {
            try {
                List<SMSMessage> messages = getSmsMessages();
                
                runOnUiThread(() -> {
                    showLoading(false);
                    smsMessages.clear();
                    smsMessages.addAll(messages);
                    smsAdapter.setSmsMessages(smsMessages);
                    
                    if (smsMessages.isEmpty()) {
                        showEmptyState(true);
                    } else {
                        showEmptyState(false);
                    }
                });
                
            } catch (Exception e) {
                Log.e(TAG, "Error loading SMS messages", e);
                runOnUiThread(() -> {
                    showLoading(false);
                    Toast.makeText(this, "Error loading messages", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }
    
    private List<SMSMessage> getSmsMessages() {
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
            
            Cursor cursor = contentResolver.query(uri, projection, null, null, 
                    Telephony.Sms.DATE + " DESC LIMIT 100");
            
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
            Log.e(TAG, "Error querying SMS messages", e);
        }
        
        return messages;
    }
    
    private void loadContacts() {
        contactsMap.clear();
        
        try {
            ContentResolver contentResolver = getContentResolver();
            Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
            
            String[] projection = {
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER
            };
            
            Cursor cursor = contentResolver.query(uri, projection, null, null, 
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
            
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String contactId = cursor.getString(cursor.getColumnIndexOrThrow(
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                    String displayName = cursor.getString(cursor.getColumnIndexOrThrow(
                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow(
                            ContactsContract.CommonDataKinds.Phone.NUMBER));
                    
                    Contact contact = new Contact(contactId, displayName, phoneNumber);
                    contactsMap.put(phoneNumber, contact);
                }
                cursor.close();
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error loading contacts", e);
        }
    }
    
    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerViewMessages.setVisibility(show ? View.GONE : View.VISIBLE);
    }
    
    private void showEmptyState(boolean show) {
        emptyStateLayout.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerViewMessages.setVisibility(show ? View.GONE : View.VISIBLE);
    }
    
    @Override
    public void onSMSClick(SMSMessage smsMessage) {
        String phoneNumber = smsMessage.getSenderNumber();
        String contactName = getContactName(phoneNumber);
        openChatActivity(phoneNumber, contactName);
    }
    
    private String getContactName(String phoneNumber) {
        Contact contact = contactsMap.get(phoneNumber);
        return contact != null ? contact.getDisplayName() : phoneNumber;
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == PermissionUtil.REQUEST_CODE_DEFAULT_SMS) {
            // Check if app is now default SMS app
            if (PermissionUtil.isDefaultSmsApp(this)) {
                checkAndRequestPermissions();
            } else {
                Toast.makeText(this, "App must be set as default SMS app", Toast.LENGTH_LONG).show();
                finish();
            }
        } else if (requestCode == PermissionUtil.REQUEST_CODE_BATTERY_OPTIMIZATION) {
            // Continue regardless of battery optimization result
            loadSmsMessages();
        }
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, 
                                         @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        boolean allGranted = true;
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                allGranted = false;
                break;
            }
        }
        
        if (allGranted) {
            checkAndRequestPermissions();
        } else {
            Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_LONG).show();
            // You might want to show a dialog explaining why permissions are needed
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Refresh messages when returning to activity
        if (PermissionUtil.hasAllRequiredPermissions(this)) {
            loadSmsMessages();
        }
    }
}
