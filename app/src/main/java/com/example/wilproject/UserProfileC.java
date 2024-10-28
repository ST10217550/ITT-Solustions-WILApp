package com.example.wilproject;

public class UserProfileC {

    public String name;
    public String surname;
    public String IDNumb;
    public String email;
    public String phoneNumber;
    public String address;
    public String city;

    public String zipCode;
    public String dob;
    public String illnesses;
    public String gender;

    public String kinName;
    public String kinEmail;
    public String phone;



    public UserProfileC(String IDNumb, String phoneNumber, String address, String city, String zipCode, String dob, String illnesses, String gender, String kinName, String kinEmail, String phone ){
        this.IDNumb = IDNumb;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.city = city;
        this.zipCode = zipCode;
        this.dob = dob;
        this.illnesses = illnesses;
        this.gender = gender;
        this.kinName = kinName;
        this.kinEmail = kinEmail;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getIDNumb() {
        return IDNumb;
    }

    public void setIDNumb(String IDNumb) {
        this.IDNumb = IDNumb;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getIllnesses() {
        return illnesses;
    }

    public void setIllnesses(String illnesses) {
        this.illnesses = illnesses;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getKinName() {
        return kinName;
    }

    public void setKinName(String kinName) {
        this.kinName = kinName;
    }

    public String getKinEmail() {
        return kinEmail;
    }

    public void setKinEmail(String kinEmail) {
        this.kinEmail = kinEmail;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }



    public UserProfileC() {
        // Default constructor required for calls to DataSnapshot.getValue(UserProfile.class)
    }


}
