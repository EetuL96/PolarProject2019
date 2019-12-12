package com.example.polarproject;

import java.io.Serializable;

public class User implements Serializable {

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    private String ID;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    private String firstName;

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    private String lastName;

    private String email;
    public void setEmail(String email) {this.email = email;}
    public String getEmail (){return this.email;}

    private boolean isFollowed;
    public void setIsFollowed(boolean isFollowed)
    {
        this.isFollowed = isFollowed;
    }

    public boolean getIsFollowed()
    {
        return this.isFollowed;
    }


}
