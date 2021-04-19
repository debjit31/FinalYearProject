package com.example.finalyearproject;

public class Donor {
    private String name,age,contact,address,blood_grp,landmark,latitude,longitude, medical_report;
    public Donor(){

    }
    public Donor(String name, String age, String contact, String address, String blood_grp, String landmark, String latitude, String longitude, String medical_report) {
        this.name = name;
        this.age = age;
        this.contact = contact;
        this.address = address;
        this.blood_grp = blood_grp;
        this.landmark = landmark;
        this.latitude = latitude;
        this.longitude = longitude;
        this.medical_report = medical_report;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBlood_grp() {
        return blood_grp;
    }

    public void setBlood_grp(String blood_grp) {
        this.blood_grp = blood_grp;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getMedical_report() {
        return medical_report;
    }

    public void setMedical_report(String medical_report) {
        this.medical_report = medical_report;
    }
}
