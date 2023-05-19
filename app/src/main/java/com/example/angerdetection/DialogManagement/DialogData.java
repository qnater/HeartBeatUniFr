package com.example.angerdetection.DialogManagement;

import java.util.ArrayList;

/**
 * Anger Detection
 * This project is directed by the University of Fribourg in the context of the course FS2023: 03035/33035 Multimodal User Interfaces
 * Matilde De Luigi / Quentin Nater
 */
public class DialogData
{
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

    public void setUserQueryResult(ArrayList<String> userQueryResult) { this.userQueryResult = userQueryResult; }


    public ArrayList<String> getUserRefinedQueryResult()
    {
        return userRefinedQueryResult;
    }

    public void setUserRefinedQueryResult(ArrayList<String> userRafinedQueryResult) { this.userRefinedQueryResult = userRafinedQueryResult; }


    public ArrayList<String> getHistoryCommands() {
        return historyCommands;
    }


    public ArrayList<String> getHistoryResults() {
        return historyResults;
    }

    public void setHistoryResults(ArrayList<String> historyResults) { this.historyResults = historyResults;  }

    public void setHistoryCommands(ArrayList<String> historyCommands)
    {
        this.historyCommands = historyCommands;

        System.out.println("\t\t\t HISTORY COMMANDS");
        for (String command : historyCommands)
            System.out.println("\t\t\t " + command);

    }
}
