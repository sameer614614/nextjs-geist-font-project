package com.smsapp.messages.models;

public class Contact {
    private String contactId;
    private String contactName;
    private String phoneNumber;
    private String displayName;
    private String photoUri;

    public Contact() {
    }

    public Contact(String contactId, String contactName, String phoneNumber) {
        this.contactId = contactId;
        this.contactName = contactName;
        this.phoneNumber = phoneNumber;
        this.displayName = contactName;
    }

    public Contact(String contactName, String phoneNumber) {
        this.contactName = contactName;
        this.phoneNumber = phoneNumber;
        this.displayName = contactName;
    }

    // Getters
    public String getContactId() {
        return contactId;
    }

    public String getContactName() {
        return contactName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getDisplayName() {
        return displayName != null ? displayName : contactName;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    // Setters
    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
        if (this.displayName == null) {
            this.displayName = contactName;
        }
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

    // Utility methods
    public String getInitials() {
        if (displayName != null && !displayName.trim().isEmpty()) {
            String[] parts = displayName.trim().split("\\s+");
            if (parts.length >= 2) {
                return (parts[0].charAt(0) + "" + parts[1].charAt(0)).toUpperCase();
            } else if (parts.length == 1) {
                return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
            }
        }
        return phoneNumber != null && phoneNumber.length() >= 2 ? 
               phoneNumber.substring(phoneNumber.length() - 2) : "??";
    }

    @Override
    public String toString() {
        return "Contact{" +
                "contactId='" + contactId + '\'' +
                ", contactName='" + contactName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", displayName='" + displayName + '\'' +
                ", photoUri='" + photoUri + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Contact contact = (Contact) obj;
        return phoneNumber != null ? phoneNumber.equals(contact.phoneNumber) : contact.phoneNumber == null;
    }

    @Override
    public int hashCode() {
        return phoneNumber != null ? phoneNumber.hashCode() : 0;
    }
}
