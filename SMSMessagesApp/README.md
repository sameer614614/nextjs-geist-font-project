# SMS Messages App

A comprehensive SMS-only messaging application for Android that serves as a default SMS app with full functionality for sending and receiving text messages.

## Features

### Core Functionality
- **SMS Only**: Focused on text messaging without MMS or attachment support
- **Default SMS App**: Can be set as the device's default SMS application
- **Send & Receive**: Full SMS sending and receiving capabilities
- **Background Operation**: Works in foreground and background modes
- **Contact Integration**: Reads and integrates with device contacts
- **Message History**: Displays existing SMS messages from the device

### User Interface
- **Modern Design**: Clean, minimalist black and white interface
- **Conversation View**: Chat-style message bubbles for easy reading
- **Contact List**: Shows conversations with contact names or phone numbers
- **FAB (Floating Action Button)**: Quick access to start new conversations
- **Responsive Layout**: Works across different screen sizes

### Permissions & Security
- **Runtime Permissions**: Requests all necessary permissions at startup
- **SMS Permissions**: READ_SMS, WRITE_SMS, SEND_SMS, RECEIVE_SMS
- **Contacts Permissions**: READ_CONTACTS, WRITE_CONTACTS
- **Phone State**: READ_PHONE_STATE for SMS functionality
- **Battery Optimization**: Requests exemption for background operation
- **Boot Receiver**: Automatically starts after device reboot

## Technical Specifications

### Android Compatibility
- **Minimum SDK**: 24 (Android 7.0 Nougat)
- **Target SDK**: 34 (Android 14)
- **Maximum SDK**: 36 (Android 14+)
- **Architecture**: Supports all Android device architectures

### Project Structure
```
SMSMessagesApp/
├── app/
│   ├── src/main/
│   │   ├── java/com/smsapp/messages/
│   │   │   ├── activities/          # UI Activities
│   │   │   │   ├── MainActivity.java
│   │   │   │   └── ChatActivity.java
│   │   │   ├── adapters/           # RecyclerView Adapters
│   │   │   │   ├── SMSAdapter.java
│   │   │   │   └── ChatAdapter.java
│   │   │   ├── models/             # Data Models
│   │   │   │   ├── SMSMessage.java
│   │   │   │   └── Contact.java
│   │   │   ├── receivers/          # Broadcast Receivers
│   │   │   │   ├── SMSReceiver.java
│   │   │   │   └── BootReceiver.java
│   │   │   ├── services/           # Background Services
│   │   │   │   └── SMSService.java
│   │   │   └── utils/              # Utility Classes
│   │   │       └── PermissionUtil.java
│   │   ├── res/                    # Resources
│   │   │   ├── layout/             # XML Layouts
│   │   │   ├── values/             # Strings, Colors, Styles
│   │   │   ├── drawable/           # Drawable Resources
│   │   │   └── xml/                # Configuration Files
│   │   └── AndroidManifest.xml     # App Manifest
│   └── build.gradle                # App Build Configuration
├── gradle/                         # Gradle Wrapper
├── build.gradle                    # Project Build Configuration
├── settings.gradle                 # Project Settings
└── gradle.properties              # Gradle Properties
```

## Setup Instructions

### Prerequisites
- Android Studio Arctic Fox or later
- Android SDK with API levels 24-34
- Java 8 or later
- Gradle 8.0 or later

### Installation Steps
1. **Clone/Download** the project to your local machine
2. **Open** the project in Android Studio
3. **Sync** the project with Gradle files
4. **Build** the project (Build → Make Project)
5. **Run** on an Android device or emulator

### Device Requirements
- Android 7.0 (API 24) or higher
- SMS capability (physical device recommended for testing)
- Contacts access
- Phone state access

## Usage Guide

### First Launch
1. **Default SMS App**: The app will prompt to set it as the default SMS app
2. **Permissions**: Grant all requested permissions for full functionality
3. **Battery Optimization**: Disable battery optimization for background SMS reception
4. **Message Loading**: Existing SMS messages will be loaded and displayed

### Sending Messages
1. **Tap FAB**: Use the floating action button to start a new conversation
2. **Choose Option**: Select "Enter Phone Number" or "Select from Contacts"
3. **Compose Message**: Type your message in the chat interface
4. **Send**: Tap the send button to deliver the message

### Receiving Messages
- Messages are automatically received in the background
- New messages appear in existing conversations
- Notifications are shown for new messages

## Key Components

### MainActivity
- Entry point of the application
- Handles permission requests and default SMS app setup
- Displays conversation list with existing SMS threads
- Manages FAB for new conversation creation

### ChatActivity
- Individual conversation interface
- Displays message history in chat bubble format
- Handles message composition and sending
- Real-time message updates

### SMSReceiver
- Broadcast receiver for incoming SMS messages
- Processes SMS data and updates the app
- Works in background even when app is closed

### PermissionUtil
- Centralized permission management
- Handles runtime permission requests
- Manages default SMS app status
- Battery optimization exemption

## Permissions Explained

### SMS Permissions
- **SEND_SMS**: Send text messages
- **RECEIVE_SMS**: Receive incoming messages
- **READ_SMS**: Read existing messages from device
- **WRITE_SMS**: Write messages to SMS database

### Contact Permissions
- **READ_CONTACTS**: Access device contacts for name display
- **WRITE_CONTACTS**: Update contact information if needed

### System Permissions
- **READ_PHONE_STATE**: Required for SMS functionality
- **RECEIVE_BOOT_COMPLETED**: Auto-start after device reboot
- **REQUEST_IGNORE_BATTERY_OPTIMIZATIONS**: Background operation

## Troubleshooting

### Common Issues
1. **Messages not sending**: Check SMS permissions and network connectivity
2. **Not receiving messages**: Ensure app is set as default SMS app
3. **Battery optimization**: Disable for consistent background operation
4. **Contact names not showing**: Grant contacts permission

### Debug Tips
- Check Android Studio Logcat for detailed error messages
- Verify all permissions are granted in device settings
- Ensure device has SMS capability for testing
- Test on physical device for best results

## Development Notes

### Architecture
- **MVVM Pattern**: Separation of concerns with clear data flow
- **Material Design**: Modern Android UI components
- **Background Processing**: Efficient SMS handling
- **Error Handling**: Comprehensive exception management

### Code Quality
- **Clean Code**: Well-documented and organized
- **Error Handling**: Robust exception management
- **Performance**: Optimized for smooth operation
- **Security**: Proper permission handling

## Future Enhancements

### Potential Features
- Message search functionality
- Message backup and restore
- Dark theme support
- Message scheduling
- Group messaging support
- Message encryption
- Custom notification sounds
- Message templates

### Technical Improvements
- Database integration for better message storage
- Push notifications for better user experience
- Message synchronization across devices
- Advanced contact management
- Message analytics and insights

## License

This project is created for educational and development purposes. Please ensure compliance with local regulations regarding SMS applications and user privacy.

## Support

For issues, questions, or contributions:
1. Check the troubleshooting section
2. Review the code documentation
3. Test on different Android versions
4. Verify all permissions are properly configured

---

**Note**: This app requires physical device testing for full SMS functionality. Emulators have limited SMS capabilities.
