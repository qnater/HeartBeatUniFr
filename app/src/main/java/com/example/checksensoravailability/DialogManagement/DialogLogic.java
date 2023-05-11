package com.example.checksensoravailability.DialogManagement;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.app.Activity.RESULT_OK;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Environment;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.checksensoravailability.ContextUserModelHistory.PersistenceLogic;
import com.example.checksensoravailability.ModalitiesFission.Fission;
import com.example.checksensoravailability.ModalitiesFission.FissionLogic;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class DialogLogic
{
    protected static final int RESULT_SPEECH = 1;
    private Boolean deadLock = false;
    private Activity recorderActivity;
    private Context context;
    private MediaRecorder mRecorder;

    private FissionLogic fissionLogic;
    private PersistenceLogic persistenceLogic;
    private DialogData dialogData;
    private Fission fission;
    private static final String TAG = "DialogLogic";
    private static final int REQUEST_AUDIO_PERMISSION_CODE = 1;

    public DialogLogic(Activity mainActivity, Context context, Fission fission, DialogData dialogData, FissionLogic fissionLogic, PersistenceLogic persistenceLogic)
    {
        this.dialogData = dialogData;
        this.fissionLogic = fissionLogic;
        this.persistenceLogic = persistenceLogic;
        this.recorderActivity = mainActivity;
        this.fission = fission;
        this.context = context;
    }

    /**
     * Function that calls the sphinx recorder for user commands
     *
     * @autor Quentin Nater
     */
    public void sphinxCall()
    {
        deadLock = false;
        // Create the basics that will contain the sphinx call
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");

        try
        {
            // start the activity the receive the voice
            recorderActivity.startActivityForResult(intent, RESULT_SPEECH);
        }
        catch (ActivityNotFoundException e)
        {
            Toast.makeText(context, "Your device doesn't support Speech to Text", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        deadLock = true;
    }

    /**
     * Get the current time
     *
     * @return String - the right date yyyy-MM-dd-HH-mm-ss
     * @autor Quentin Nater
     */
    public String getCurrentTime()
    {
        // set the timestamp
        // test to test yesterday data : timestamp = "2023-05-XX-16-37-03";
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
        return now.format(formatter);
    }



    /**
     * Function that starts a recording of the user voice for storage
     *
     * @autor Quentin Nater
     */
    public void startRecording()
    {
        // check permission method
        if (CheckPermissions())
        {

            String mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();

            String timestamp = getCurrentTime();

            mFileName += "/" + timestamp + "record.3gp";

            // set the recorder
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.setOutputFile(mFileName);

            try
            {
                mRecorder.prepare();
            }
            catch (IOException e)
            {
                Log.e("TAG", "prepare() failed");
            }
            Log.d(TAG, "Start recording");

            mRecorder.start();
        }
        else
        {
            // if audio recording permissions
            RequestPermissions();
        }
    }



    /**
     * Function that starts a recording of the user voice for storage
     *
     * @param userCommand String - Value of the user command
     * @autor Quentin Nater
     */
    public void commandUser(String userCommand)
    {
        String timestamp = getCurrentTime();

        // array used to get the commands and results of each user command to play in time (history)
        ArrayList<String> commands = dialogData.getHistoryCommands();
        ArrayList<String> results = dialogData.getHistoryResults();

        // relaxation with counting technic
        if (userCommand.contains("decrease") || userCommand.contains("Decrease") || userCommand.contains("technic") || userCommand.contains("Technic")  || userCommand.contains("continue") || userCommand.contains("Continue"))
        {
            System.out.println("**COMMANDS : decrease anger methods/technics");

            fission.setRelaxationState(11); // set the countdown

            results.add(timestamp+ ";" + "successful" + ";" + "decrease_anger");

            commands.add(timestamp + ";" + userCommand);  // save command history
            dialogData.setHistoryCommands(commands);
        }
        else if (userCommand.contains("terminate") || userCommand.contains("Terminate"))  // if the user want to finish
        {
            System.out.println("**COMMANDS : " + "terminate");

            if(fission.getAudion_on())
                fissionLogic.speak_result("Relaxation session terminate, have a nice day...");

            dialogData.setHistoryCommands(commands);
        }
        else if (userCommand.contains("relax") || userCommand.contains("Relax") || userCommand.contains("music") || userCommand.contains("Music"))  // if the user want to listener relaxation music
        {
            System.out.println("**COMMANDS : " + "relaxation_music_relax");

            fissionLogic.relaxationMethod(1);  // relaxation music
            results.add(timestamp+ ";" + "successful" + ";" + "relaxation_music_relax");

            commands.add(timestamp + ";" + userCommand);  // save the command
            dialogData.setHistoryCommands(commands);
        }
        else if (userCommand.contains("motivation") || userCommand.contains("Motivation"))    // play the motivation sound
        {
            System.out.println("**COMMANDS : " + "relaxation_music_motivation");

            fissionLogic.relaxationMethod(2);  // motivation sound
            results.add(timestamp+ ";" + "successful" + ";" + "relaxation_music_motivation");

            commands.add(timestamp + ";" + userCommand);  // save the command
            dialogData.setHistoryCommands(commands);
        }
        else if (userCommand.contains("stop") || userCommand.contains("Stop"))  // stop the sound player
        {
            System.out.println("**COMMANDS : " + "relaxation_music_stop");

            fissionLogic.relaxationMethod(0);  // stop sound
            results.add(timestamp+ ";" + "successful" + ";" + "relaxation_music_stop");

            commands.add(timestamp + ";" + userCommand);  // save the command
            dialogData.setHistoryCommands(commands);
        }
        else if (userCommand.contains("victory") || userCommand.contains("Victory"))  // play the victory music
        {
            System.out.println("**COMMANDS : " + "relaxation_music_victory");

            fissionLogic.relaxationMethod(3);  // victory music
            results.add(timestamp+ ";" + "successful" + ";" + "relaxation_music_victory");

            commands.add(timestamp + ";" + userCommand);  // save the user command
            dialogData.setHistoryCommands(commands);
        }
        else if (userCommand.contains("clean") || userCommand.contains("wipe") || userCommand.contains("Clean") || userCommand.contains("Wipe"))  // clean the result of the screen
        {
            System.out.println("**COMMANDS : " + " Clean the display");
            ArrayList<String> tmp = dialogData.getUserQueryResult();
            tmp.clear();  // reset the array list
            ArrayList<String> tmp2 = dialogData.getUserRefinedQueryResult();
            tmp2.clear();  // reset the array list
            dialogData.setUserQueryResult(tmp);
            dialogData.setUserRefinedQueryResult(tmp2);

            if(fission.getAudion_on())
                fissionLogic.speak_result("The query " + userCommand + " has been passed successfully");

            results.add(timestamp+ ";" + "successful" + ";" + "cleaning");

            commands.add(timestamp + ";" + userCommand);  // save the command
            dialogData.setHistoryCommands(commands);
        }
        else if (userCommand.contains("log") || userCommand.contains("Log") || userCommand.contains("get") || userCommand.contains("Get") || userCommand.contains("times") || userCommand.contains("Times"))  // display the x last angry detection
        {
            System.out.println("**COMMANDS : " + " Display X Times a was Angry");

            interpret_history(userCommand, 6);
            results.add(timestamp+ ";" + "successful" + ";" + dialogData.getUserRefinedQueryResult());

            commands.add(timestamp + ";" + userCommand);  // save the command
            dialogData.setHistoryCommands(commands);
        }
        else if (userCommand.contains("date") || userCommand.contains("Date"))  // display the X last occurrence with a date condition
        {
            System.out.println("**COMMANDS : Display times with date");

            String value = "";
            int number = 10;
            if(userCommand.contains("yesterday"))  // the user wants yesterday data
            {
                value = "yesterday";
            }
            else if(userCommand.contains("for"))  // the user wants specific hour data
            {
                value = "hour";
                number = interpret_number(userCommand);
            }
            else if(userCommand.contains("last"))  // the user wants last hour data
            {
                value = "last_hour";
            }

            System.out.println("Value of the user " + value + " and its number " + number);

            interpret_dialog_for_database_by_date(10, value, ""+number, 6);

            results.add(timestamp+ ";" + "successful" + ";" + dialogData.getUserRefinedQueryResult());

            commands.add(timestamp + ";" + userCommand);  // save the result
            dialogData.setHistoryCommands(commands);
        }
        else if (userCommand.contains("result") || userCommand.contains("Result") || userCommand.contains("call") || userCommand.contains("Call"))  // history management
        {
            if (userCommand.contains("despite") || userCommand.contains("Despite") || userCommand.contains("replace") || userCommand.contains("Replace"))  // change display variable
            {
                System.out.println("**COMMANDS : " + " Display a different variable");

                int indexPercentage = 0;
                int indexHeartbeat = 0;
                int indexAmplitude = 0;
                int indexPitch = 0;

                // set the position in the sentence to know which one is to replace and which one is to remove
                if (userCommand.contains("percentage")) {
                    indexPercentage = userCommand.indexOf("percentage");
                }
                if (userCommand.contains("heart") || userCommand.contains("beat")) {
                    indexHeartbeat = userCommand.indexOf("heart");
                }
                if (userCommand.contains("amplitude")) {
                    indexAmplitude = userCommand.indexOf("amplitude");
                }
                if (userCommand.contains("pitch")) {
                    indexPitch = userCommand.indexOf("pitch");
                }

                // calculate the place in the sentence to know which one is to replace and which one is to remove
                int max = Integer.MIN_VALUE;
                int secondMax = Integer.MIN_VALUE;

                int to_display = 0;
                int to_replace = 0;

                if (indexPercentage > max) {
                    secondMax = max;
                    max = indexPercentage;
                    to_replace = 6;
                    to_display = 6;
                } else if (indexPercentage > secondMax) {
                    secondMax = indexPercentage;
                    to_replace = 6;
                }

                if (indexHeartbeat > max) {
                    secondMax = max;
                    max = indexHeartbeat;
                    to_replace = 2;
                    to_display = 2;
                } else if (indexHeartbeat > secondMax) {
                    secondMax = indexHeartbeat;
                    to_replace = 2;
                }

                if (indexAmplitude > max) {
                    secondMax = max;
                    max = indexAmplitude;
                    to_replace = 4;
                    to_display = 4;
                } else if (indexAmplitude > secondMax) {
                    secondMax = indexAmplitude;
                    to_replace = 4;
                }

                if (indexPitch > max) {
                    secondMax = max;
                    max = indexPitch;
                    to_replace = 3;
                    to_display = 3;
                } else if (indexPitch > secondMax) {
                    secondMax = indexPitch;
                    to_replace = 3;
                }

                System.out.println("(HISTORY): to replace : " + to_replace);
                System.out.println("(HISTORY): to display : " + to_display);

                // get in the history the last command to return the result with different variable to display
                ArrayList<String> historyCommand = dialogData.getHistoryCommands();
                String lastCommand = historyCommand.get(historyCommand.size() - 1).split(";")[1];
                System.out.println("(HISTORY): last command : " + lastCommand);

                interpret_history(lastCommand, to_display);
                results.add(timestamp + ";" + "successful" + ";" + dialogData.getUserRefinedQueryResult());

                commands.add(timestamp + ";" + userCommand);  // save result
                dialogData.setHistoryCommands(commands);
            }
        }
        else if (userCommand.contains("history") || userCommand.contains("History") || userCommand.contains("command") || userCommand.contains("Command"))  // history command (ask for past result and command)
        {
            System.out.println("**COMMANDS : " + " Run a history command");
            ArrayList<String> historyCommand = dialogData.getHistoryCommands();

            // get which command the user want to redo (last ? first ? Xrd ?)
            int num = 0;
            if(userCommand.contains("last"))
                num = historyCommand.size() - 1;
            else if(userCommand.contains("top"))
                num = 0;
            else
                num = interpret_number(userCommand);

            System.out.println("****COMMANDS : historyCommand.get("+num+").split(;)[1]");

            // get the desired command in the array (skip the timestamp [0])
            String desiredCommand = historyCommand.get(num).split(";")[1];

            if(!desiredCommand.equals(""))  // real command ?
            {
                System.out.println("Desired command run");

                if(fission.getAudion_on())
                    fissionLogic.speak_result("Desired command run " + desiredCommand);

                commandUser(desiredCommand); // run the command
            }
            else
            {
                System.out.println("Not found in the history");
                fissionLogic.speak_result("Not found in the history");
            }
        }
        else
        {
            Log.d(TAG, "Pattern not understood... Sorry bro...");

            if(fission.getAudion_on())
                fissionLogic.speak_result("Pattern not understood...");
        }
    }


    private void interpret_history(String command, int to_display)
    {
        System.out.println("****COMMANDS : interpret_history = " + command + " - " + to_display);

        // Interpret the number of lines desired by the user ==================================
        int num = interpret_number(command);

        // Read database to get desired information (Standard : DATE+%)
        interpret_dialog_for_database(num, to_display);

        if(fission.getAudion_on())
            fissionLogic.speak_result("The query " + command + " has been passed successfully");
    }


    /**
     * Get the number inside the command the get a value cast as integer
     *
     * @param userCommand String - Value of the user command
     * @autor Quentin Nater
     */
    private int interpret_number(String userCommand)
    {
        System.out.println("****COMMANDS : interpret_number = " + userCommand);

        int num = 0;

        String[] words = userCommand.split(" ");  // each word of the user command

        for (int i = 0; i < words.length; i++)
        {
            if (words[i].matches("\\d+"))   // if it is a number (integer)
            {
                num = Integer.parseInt(words[i]);
                break;
            }
        }
        System.out.println("The number is: " + num);

        if (num > 25)  // if it is to much, reduce to 25
            num = 25;

        return num;
    }

    /**
     * Handle every command and get last angry times and handle the database
     *
     * @param num : int - number of line to display
     * @param to_display : int 1=level / 2=heart / 3=pitch / 4=amplitude / 5=noise / 6=auc
     */
    private void interpret_dialog_for_database(int num, int to_display)
    {
        System.out.println("****COMMANDS : interpret_dialog_for_database = " + num + " / " + to_display);

        ArrayList<String> result = persistenceLogic.getLastAngry(num);  // get the result of angry times
        ArrayList<String> refinedResult = new ArrayList<>();

        for (String stressLine : result)  // for each angry time
        {
            System.out.println("LOG REPORT : " + stressLine);

            String[] fields = stressLine.split(",");

            // get the date of the angry time and the % of the AI
            String date = fields[0];
            String value = fields[to_display];

            // Format the date
            String[] dateDetails = date.split("-");
            String month = dateDetails[1];
            String day = dateDetails[2];
            String hour = dateDetails[3];
            String minute = dateDetails[4];

            // Final result to display
            refinedResult.add(day+"."+month + " at " + hour+ ":"+ minute + " = " + value);
        }

        dialogData.setUserQueryResult(result);  // save the result for history
        dialogData.setUserRefinedQueryResult(refinedResult);  // save the final result for history

        if(fission.getAudion_on())  // speak the result
            fissionLogic.speak_result("The query has been passed successfully");
    }


    /**
     * Handle every command and get last angry times (by date) and handle the database
     * @param num : int - number of line to display
     * @param type String - Search by "yesterday", "hour" or "last_hour"
     * @param number String - if needed the specific day or hour
     * @param to_display : int 1=level / 2=heart / 3=pitch / 4=amplitude / 5=noise / 6=auc
     */
    private void interpret_dialog_for_database_by_date(int num, String type, String number, int to_display)
    {
        System.out.println("****COMMANDS : interpret_dialog_for_database_by_date = " + num + " / " + to_display + " / " + type + " / " + number);

        ArrayList<String> result = persistenceLogic.getLastAngryByDate(10, type, number);   // get the result of angry times
        ArrayList<String> refinedResult = new ArrayList<>();


        for (String stressLine : result)   // for each angry time
        {
            System.out.println("LOG REPORT : " + stressLine);

            String[] fields = stressLine.split(",");

            // get the date of the angry time and the desired value
            String date = fields[0];
            String value = fields[to_display];

            // Format the date
            String[] dateDetails = date.split("-");
            String month = dateDetails[1];
            String day = dateDetails[2];
            String hour = dateDetails[3];
            String minute = dateDetails[4];

            // Final result to display
            refinedResult.add(day+"."+month + " at " + hour+ ":"+ minute + " = " + value);
        }

        dialogData.setUserQueryResult(result);  // save the result for history
        dialogData.setUserRefinedQueryResult(refinedResult);  // save the final result for history

        if(fission.getAudion_on())  // speak the result
            fissionLogic.speak_result("The date query has been passed successfully");
    }


    /**
     * Function that pauses the recording of the voice
     *
     * @autor Quentin Nater
     */
    public void pauseRecording()
    {
        // below method will stop
        // the audio recording.
        mRecorder.stop();

        // the media recorder class.
        Log.d(TAG, "Stop recording");
        mRecorder.release();


        mRecorder = null;
    }


    /**
     * Function that handles the user request on the main application
     *
     * @param request int - Request the user command
     * @param code int - System code the user command
     * @param data Intent - Date receive by the user command
     * @autor Quentin Nater
     */
    public void handleUserRequest(int request, int code, @Nullable Intent data)
    {
        switch (request)
        {
            case RESULT_SPEECH:  // result of speech request
                if (code == RESULT_OK && data != null)
                {
                    // get the value of the voice
                    ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    // get the value of the text
                    String userCommand = text.get(0);
                    Log.d(TAG, "Sphinx get : " + userCommand);

                    // start to analyze the text of the user voice
                    commandUser(userCommand);
                }
                break;
        }
    }


    /**
     * Function that handles the system right of user request on the main application
     *
     * @param requestCode int -  System code the user command
     * @param permissions String[] - List of permissions requested
     * @param grantResults int[] - List of permissions granted
     * @autor Quentin Nater
     */
    public void requestUser(int requestCode, String[] permissions, int[] grantResults)
    {
        switch (requestCode)
        {
            case REQUEST_AUDIO_PERMISSION_CODE: // if the user asked for audio permission
                if (grantResults.length > 0)
                {
                    // both permission needed
                    boolean permissionToRecord = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean permissionToStore = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    // display the result
                    if (permissionToRecord && permissionToStore)
                        Toast.makeText(recorderActivity.getApplicationContext(), "Permission Granted", Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(recorderActivity.getApplicationContext(), "Permission Denied", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }


    /**
     * Check permission on the system hardware
     *
     * @autor Quentin Nater
     */
    public boolean CheckPermissions()
    {
        // this method is used to check permission
        int result = ContextCompat.checkSelfPermission(context, WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(context, RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Request permission on the system hardware
     *
     * @autor Quentin Nater
     */
    private void RequestPermissions()
    {
        // permission for audio recording and storage
        ActivityCompat.requestPermissions(recorderActivity, new String[]{RECORD_AUDIO, WRITE_EXTERNAL_STORAGE}, REQUEST_AUDIO_PERMISSION_CODE);
    }
}
