package com.example.wilproject;

public class UserProfile {
    String IDNumb;
    String name;
    String surname;
    String email;
    String password;




    public UserProfile(String IDNumb, String name, String surname, String email, String password) {
        this.IDNumb = IDNumb;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;

    }

    public String getIDNumb() {
        return IDNumb;
    }

    public void setIDNumb(String IDNumb) {
        this.IDNumb = IDNumb;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }



    public UserProfile(){

    }

}
