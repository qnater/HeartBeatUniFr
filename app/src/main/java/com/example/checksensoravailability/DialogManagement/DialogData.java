package com.example.checksensoravailability.DialogManagement;


import java.util.ArrayList;

public class DialogData
{
    // FIXED DATA
    private ArrayList<String> userQueryResult;
    private ArrayList<String> userRefinedQueryResult;

    private ArrayList<String> historyCommands;
    private ArrayList<String> historyResults;


    public DialogData()
    {
        userQueryResult = new ArrayList<>();
        userRefinedQueryResult = new ArrayList<>();
        historyCommands = new ArrayList<>();
        historyResults = new ArrayList<>();
    }

    public ArrayList<String> getUserQueryResult() {
        return userQueryResult;
    }

    public void setUserQueryResult(ArrayList<String> userQueryResult) {
        this.userQueryResult = userQueryResult;
    }


    public ArrayList<String> getUserRefinedQueryResult()
    {
        return userRefinedQueryResult;
    }

    public void setUserRefinedQueryResult(ArrayList<String> userRafinedQueryResult)
    {
        this.userRefinedQueryResult = userRafinedQueryResult;
    }


    public ArrayList<String> getHistoryCommands() {
        return historyCommands;
    }

    public void setHistoryCommands(ArrayList<String> historyCommands)
    {
        this.historyCommands = historyCommands;

        System.out.println("\t\t\t HISTORY COMMANDS");
        for (String command : historyCommands)
            System.out.println("\t\t\t " + command);

    }

    public ArrayList<String> getHistoryResults() {
        return historyResults;
    }

    public void setHistoryResults(ArrayList<String> historyResults) {
        this.historyResults = historyResults;
    }

}
