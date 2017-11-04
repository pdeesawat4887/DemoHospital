package com.example.pacharapoldeesawat.demohospital.Model;

public class User {
    private String citizenId;
    private String role;

    public User() {
    }

    public User(String citizenId, String role) {
        this.citizenId = citizenId;
        this.role = role;
    }

    public String getCitizenId() {
        return citizenId;
    }

    public void setCitizenId(String citizenId) {
        this.citizenId = citizenId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
