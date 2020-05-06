package com.loghme.service.DTO;

public class UserDTO {
    private String firstName;
    private String lastName;
    private String email;
    private int credit;

    public UserDTO(String firstName, String lastName, String email, int credit) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.credit = credit;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getCredit() {
        return credit;
    }

    public void setCredit(int credit) {
        this.credit = credit;
    }
}
