package com.example.wilproject;

public class UserProfileC {

    public String firstName;
    public String lastName;
    public String idNumber;
    public String email;
    public String address1;
    public String address2;
    public String fileNumber;
    public String illness;
    public String gender;

    public UserProfileC() {
        // Default constructor required for calls to DataSnapshot.getValue(UserProfile.class)
    }

    public UserProfileC(String firstName, String surname, String idNumber, String email, String address1, String address2, String fileNumber, String illnessText, String gender) {
    }
}
