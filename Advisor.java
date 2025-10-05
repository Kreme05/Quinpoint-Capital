package com.quinpoint.model;

public class Advisor {
    private final String id;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String desk;

    public Advisor(String id, String firstName, String lastName, String email, String desk) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.desk = desk;
    }

    public String getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getDesk() { return desk; }

    // helper for full name
    public String getName() {
        return firstName + " " + lastName;
    }
}
