package com.example.checksensoravailability.DialogManagement;


import java.util.ArrayList;

public class DialogData
{
    // FIXED DATA
    private ArrayList<String> userQueryResult;
    private ArrayList<String> userRafinedQueryResult;

    public DialogData()
    {
        userQueryResult = new ArrayList<>();
        userRafinedQueryResult = new ArrayList<>();
    }

    public ArrayList<String> getUserQueryResult() {
        return userQueryResult;
    }

    public void setUserQueryResult(ArrayList<String> userQueryResult) {
        this.userQueryResult = userQueryResult;
    }


    public ArrayList<String> getUserRafinedQueryResult()
    {
        return userRafinedQueryResult;
    }

    public void setUserRafinedQueryResult(ArrayList<String> userRafinedQueryResult)
    {
        this.userRafinedQueryResult = userRafinedQueryResult;
    }

}
