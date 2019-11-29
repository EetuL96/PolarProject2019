package com.example.polarproject;



public class Application extends android.app.Application {

    private User user;

    public void  setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return this.user;
    }
}