package com.example.finalyearproject;

public class Donor {
    private String name;
    private String contact;
    private int age;

    public Donor(){

    }
    public Donor(String name, String contact, int age) {
        this.name = name;
        this.contact = contact;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
