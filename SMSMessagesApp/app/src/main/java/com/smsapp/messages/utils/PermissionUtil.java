package com.smsapp.messages.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.provider.Telephony;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionUtil {
    
    public static final int REQUEST_CODE_SMS_PERMISSIONS = 100;
    public static final int REQUEST_CODE_CONTACTS_PERMISSIONS = 101;
    public static final int REQUEST_CODE_ALL_PERMISSIONS = 102;
    public static final int REQUEST_CODE_DEFAULT_SMS = 103;
    public static final int REQUEST_CODE_BATTERY_OPTIMIZATION = 104;

    // Required SMS permissions
    private static final String[] SMS_PERMISSIONS = {
            Manifest.permission.SEND_SMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.READ_SMS,
            Manifest.permission.WRITE_SMS,
            Manifest.permission.READ_PHONE_STATE
    };

    // Required contacts permissions
    private static final String[] CONTACTS_PERMISSIONS = {
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS
    };

    // All required permissions
    private static final String[] ALL_PERMISSIONS = {
            Manifest.permission.SEND_SMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.READ_SMS,
            Manifest.permission.WRITE_SMS,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS
    };

    /**
     * Check if the app is set as default SMS app
     */
    public static boolean isDefaultSmsApp(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return context.getPackageName().equals(Telephony.Sms.getDefaultSmsPackage(context));
        }
        return true; // For older versions, assume true
    }

    /**
     * Request to set app as default SMS app
     */
    public static void requestDefaultSmsApp(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
            intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, activity.getPackageName());
            activity.startActivityForResult(intent, REQUEST_CODE_DEFAULT_SMS);
        }
    }

    /**
     * Check if all SMS permissions are granted
     */
    public static boolean hasSmsPermissions(Context context) {
        for (String permission : SMS_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if all contacts permissions are granted
     */
    public static boolean hasContactsPermissions(Context context) {
        for (String permission : CONTACTS_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if all required permissions are granted
     */
    public static boolean hasAllRequiredPermissions(Context context) {
        for (String permission : ALL_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * Request SMS permissions
     */
    public static void requestSmsPermissions(Activity activity) {
        ActivityCompat.requestPermissions(activity, SMS_PERMISSIONS, REQUEST_CODE_SMS_PERMISSIONS);
    }

    /**
     * Request contacts permissions
     */
    public static void requestContactsPermissions(Activity activity) {
        ActivityCompat.requestPermissions(activity, CONTACTS_PERMISSIONS, REQUEST_CODE_CONTACTS_PERMISSIONS);
    }

    /**
     * Request all necessary permissions
     */
    public static void requestAllPermissions(Activity activity) {
        ActivityCompat.requestPermissions(activity, ALL_PERMISSIONS, REQUEST_CODE_ALL_PERMISSIONS);
    }

    /**
     * Check which permissions are missing
     */
    public static String[] getMissingPermissions(Context context) {
        java.util.List<String> missingPermissions = new java.util.ArrayList<>();
        for (String permission : ALL_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission);
            }
        }
        return missingPermissions.toArray(new String[0]);
    }

    /**
     * Check if we should show rationale for any permission
     */
    public static boolean shouldShowRequestPermissionRationale(Activity activity) {
        for (String permission : ALL_PERMISSIONS) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Request battery optimization exemption
     */
    public static void requestBatteryOptimizationExemption(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + activity.getPackageName()));
                activity.startActivityForResult(intent, REQUEST_CODE_BATTERY_OPTIMIZATION);
            } catch (Exception e) {
                // Fallback to general battery optimization settings
                Intent intent = new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                activity.startActivity(intent);
            }
        }
    }

    /**
     * Check if battery optimization is disabled for the app
     */
    public static boolean isBatteryOptimizationDisabled(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            android.os.PowerManager powerManager = (android.os.PowerManager) context.getSystemService(Context.POWER_SERVICE);
            return powerManager != null && powerManager.isIgnoringBatteryOptimizations(context.getPackageName());
        }
        return true; // For older versions, assume true
    }

    /**
     * Open app settings page
     */
    public static void openAppSettings(Activity activity) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
        intent.setData(uri);
        activity.startActivity(intent);
    }

    /**
     * Get permission name for display
     */
    public static String getPermissionDisplayName(String permission) {
        switch (permission) {
            case Manifest.permission.SEND_SMS:
                return "Send SMS";
            case Manifest.permission.RECEIVE_SMS:
                return "Receive SMS";
            case Manifest.permission.READ_SMS:
                return "Read SMS";
            case Manifest.permission.WRITE_SMS:
                return "Write SMS";
            case Manifest.permission.READ_PHONE_STATE:
                return "Read Phone State";
            case Manifest.permission.READ_CONTACTS:
                return "Read Contacts";
            case Manifest.permission.WRITE_CONTACTS:
                return "Write Contacts";
            default:
                return permission;
        }
    }
}
