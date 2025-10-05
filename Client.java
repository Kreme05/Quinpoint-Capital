package com.quinpoint.model;

public class Client {
    private final String id;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final int riskScore;

    public Client(String id, String firstName, String lastName, String email, int riskScore) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.riskScore = riskScore;
    }

    public String getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public int getRiskScore() { return riskScore; }

    // helper for full name
    public String getName() {
        return firstName + " " + lastName;
    }
}
