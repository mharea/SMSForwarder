package com.mobile.smsforwarder.model;

public class Contact {

    private String contactId;
    private String contactName;
    private String contactMobileNumber;

    private Contact() {}

    private Contact(String contactId) {
        this.contactId = contactId;
    }

    public static Contact from(String contactId) {
        return new Contact(contactId);
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactMobileNumber() {
        return contactMobileNumber;
    }

    public void setContactMobileNumber(String contactMobileNumber) {
        this.contactMobileNumber = contactMobileNumber;
    }
}
