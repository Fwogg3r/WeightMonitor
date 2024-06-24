package com.example.weightwatchersthomasbryant;
import java.util.Date;
import java.util.LinkedList;
public class User {

    private int userID;
    private String email;
    private String password;

    public void setCurrGoalWeight(double currGoalWeight) {
        this.currGoalWeight = currGoalWeight;
    }

    public void setCurrWeight(double currWeight) {
        this.currWeight = currWeight;
    }

    public void setUserID(int userID)
    {
        this.userID = userID;
    }

    private double currGoalWeight;
    private double currWeight;

    public User(int userID, String email, String password, double currGoalWeight, double currWeight) {
        this.userID = userID;
        this.email = email;
        this.password = password;
        this.currGoalWeight = currGoalWeight;
        this.currWeight = currWeight;
        //this.date = date; //java.time.LocalDate.now().toString(); //MESSY!
    }

    public int getUserID() {
        return userID;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public double getCurrGoalWeight() {
        return currGoalWeight;
    }

    public double getCurrWeight() {
        return currWeight;
    }
}
