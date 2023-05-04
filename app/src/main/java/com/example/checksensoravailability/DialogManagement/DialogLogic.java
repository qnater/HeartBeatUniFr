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

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");

        try
        {
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
     * Function that starts a recording of the user voice for storage
     *
     * @autor Quentin Nater
     */
    public void startRecording()
    {
        // check permission method is used to check
        // that the user has granted permission
        // to record and store the audio.
        if (CheckPermissions())
        {
            // we are here initializing our filename variable
            // with the path of the recorded audio file.
            String mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();

            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
            String timestamp = now.format(formatter);

            mFileName += "/" + timestamp + "record.3gp";

            // below method is used to initialize
            // the media recorder class
            mRecorder = new MediaRecorder();

            // below method is used to set the audio
            // source which we are using a mic.
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);

            // below method is used to set
            // the output format of the audio.
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

            // below method is used to set the
            // audio encoder for our recorded audio.
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            // below method is used to set the
            // output file location for our recorded audio
            mRecorder.setOutputFile(mFileName);
            try {
                // below method will prepare
                // our audio recorder class
                mRecorder.prepare();
            }
            catch (IOException e)
            {
                Log.e("TAG", "prepare() failed");
            }
            // start method will start
            // the audio recording.
            Log.d(TAG, "Start recording");

            mRecorder.start();

        }
        else
        {
            // if audio recording permissions are
            // not granted by user below method will
            // ask for runtime permission for mic and storage.
            RequestPermissions();
        }
    }

    public void handleRelaxationResult()
    {
        if(fission.getRelaxationState() == 15)
        {
            Toast.makeText(recorderActivity.getApplicationContext(), "Result " + fission.getHeartBeat() + "bmps... 'Continue',  'Terminate' or 'Music' ?", Toast.LENGTH_LONG).show();

            fissionLogic.speak_result("Your result is" + fission.getHeartBeat() +" ... Do you want to 'continue' or 'terminate' the session ? Do you want to listen 'relaxation music' ?");

            fission.setRelaxationState(14);
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
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
        String timestamp = now.format(formatter);

        ArrayList<String> commands = dialogData.getHistoryCommands();
        ArrayList<String> results = dialogData.getHistoryResults();

        if (userCommand.contains("decrease") || userCommand.contains("Decrease") || userCommand.contains("technic") || userCommand.contains("Technic")  || userCommand.contains("continue") || userCommand.contains("Continue"))
        {
            System.out.println("**COMMANDS : decrease anger methods/technics");

            fission.setRelaxationState(11);

            results.add(timestamp+ ";" + "successful" + ";" + "decrease_anger");

            commands.add(timestamp + ";" + userCommand);
            dialogData.setHistoryCommands(commands);
        }
        else if (userCommand.contains("terminate") || userCommand.contains("Terminate"))
        {
            System.out.println("**COMMANDS : " + "terminate");

            if(fission.getAudion_on())
                fissionLogic.speak_result("Relaxation session terminate, have a nice day...");

            dialogData.setHistoryCommands(commands);
        }
        else if (userCommand.contains("relax") || userCommand.contains("Relax") || userCommand.contains("music") || userCommand.contains("Music"))
        {
            System.out.println("**COMMANDS : " + "relaxation_music_relax");

            fissionLogic.relaxationMethod(1);
            results.add(timestamp+ ";" + "successful" + ";" + "relaxation_music_relax");

            commands.add(timestamp + ";" + userCommand);
            dialogData.setHistoryCommands(commands);
        }
        else if (userCommand.contains("motivation") || userCommand.contains("Motivation"))
        {
            System.out.println("**COMMANDS : " + "relaxation_music_motivation");

            fissionLogic.relaxationMethod(2);
            results.add(timestamp+ ";" + "successful" + ";" + "relaxation_music_motivation");

            commands.add(timestamp + ";" + userCommand);
            dialogData.setHistoryCommands(commands);
        }
        else if (userCommand.contains("stop") || userCommand.contains("Stop"))
        {
            System.out.println("**COMMANDS : " + "relaxation_music_stop");

            fissionLogic.relaxationMethod(0);
            results.add(timestamp+ ";" + "successful" + ";" + "relaxation_music_stop");

            commands.add(timestamp + ";" + userCommand);
            dialogData.setHistoryCommands(commands);
        }
        else if (userCommand.contains("victory") || userCommand.contains("Victory"))
        {
            System.out.println("**COMMANDS : " + "relaxation_music_victory");

            fissionLogic.relaxationMethod(3);
            results.add(timestamp+ ";" + "successful" + ";" + "relaxation_music_victory");

            commands.add(timestamp + ";" + userCommand);
            dialogData.setHistoryCommands(commands);
        }
        else if (userCommand.contains("clean") || userCommand.contains("wipe") || userCommand.contains("Clean") || userCommand.contains("Wipe"))
        {
            System.out.println("**COMMANDS : " + " Clean the display");
            ArrayList<String> tmp = dialogData.getUserQueryResult();
            tmp.clear();
            ArrayList<String> tmp2 = dialogData.getUserRefinedQueryResult();
            tmp2.clear();
            dialogData.setUserQueryResult(tmp);
            dialogData.setUserRefinedQueryResult(tmp2);

            if(fission.getAudion_on())
                fissionLogic.speak_result("The query " + userCommand + " has been passed successfully");

            results.add(timestamp+ ";" + "successful" + ";" + "cleaning");

            commands.add(timestamp + ";" + userCommand);
            dialogData.setHistoryCommands(commands);
        }
        else if (userCommand.contains("log") || userCommand.contains("Log") || userCommand.contains("get") || userCommand.contains("Get") || userCommand.contains("times") || userCommand.contains("Times"))
        {
            System.out.println("**COMMANDS : " + " Display X Times a was Angry");

            interpret_history(userCommand, 6);
            results.add(timestamp+ ";" + "successful" + ";" + dialogData.getUserRefinedQueryResult());

            commands.add(timestamp + ";" + userCommand);
            dialogData.setHistoryCommands(commands);
        }
        else if (userCommand.contains("date") || userCommand.contains("Date"))
        {
            System.out.println("**COMMANDS : Display times with date");

            String value = "";
            int number = 10;
            if(userCommand.contains("yesterday"))
            {
                value = "yesterday";
            }
            else if(userCommand.contains("for"))
            {
                value = "hour";
                number = interpret_number(userCommand);
            }
            else if(userCommand.contains("last"))
            {
                value = "last_hour";
            }

            System.out.println("Value of the user " + value + " and its number " + number);

            interpret_dialog_for_database_by_date(10, value, ""+number, 6);

            results.add(timestamp+ ";" + "successful" + ";" + dialogData.getUserRefinedQueryResult());

            commands.add(timestamp + ";" + userCommand);
            dialogData.setHistoryCommands(commands);
        }
        else if (userCommand.contains("result") || userCommand.contains("Result") || userCommand.contains("call") || userCommand.contains("Call"))
        {
            System.out.println("**COMMANDS : " + " Display from a result");

            if (userCommand.contains("by") || userCommand.contains("By") || userCommand.contains("replace") || userCommand.contains("Replace"))
            {
                System.out.println("**COMMANDS : " + " Replace");

                if (userCommand.contains("despite") || userCommand.contains("Despite") || userCommand.contains("replace") || userCommand.contains("Replace"))
                {
                    System.out.println("**COMMANDS : " + " Display a different variable");

                    int indexPercentage = 0;
                    int indexHeartbeat = 0;
                    int indexAmplitude = 0;
                    int indexPitch = 0;

                    if (userCommand.contains("percentage"))
                    {
                        indexPercentage = userCommand.indexOf("percentage");
                    }
                    if (userCommand.contains("heart") || userCommand.contains("beat"))
                    {
                        indexHeartbeat = userCommand.indexOf("heart");
                    }
                    if (userCommand.contains("amplitude"))
                    {
                        indexAmplitude = userCommand.indexOf("amplitude");
                    }
                    if (userCommand.contains("pitch"))
                    {
                        indexPitch = userCommand.indexOf("pitch");
                    }


                    int max = Integer.MIN_VALUE; // initialize max to the smallest possible value
                    int secondMax = Integer.MIN_VALUE; // initialize secondMax to the smallest possible value

                    int to_display = 0; // initialize max to the smallest possible value
                    int to_replace = 0; // initialize secondMax to the smallest possible value

                    if (indexPercentage > max)
                    {
                        secondMax = max;
                        max = indexPercentage;
                        to_replace = 6;
                        to_display = 6;
                    }
                    else if (indexPercentage > secondMax)
                    {
                        secondMax = indexPercentage;
                        to_replace = 6;
                    }

                    if (indexHeartbeat > max)
                    {
                        secondMax = max;
                        max = indexHeartbeat;
                        to_replace = 2;
                        to_display = 2;
                    }
                    else if (indexHeartbeat > secondMax)
                    {
                        secondMax = indexHeartbeat;
                        to_replace = 2;
                    }

                    if (indexAmplitude > max)
                    {
                        secondMax = max;
                        max = indexAmplitude;
                        to_replace = 4;
                        to_display = 4;
                    }
                    else if (indexAmplitude > secondMax)
                    {
                        secondMax = indexAmplitude;
                        to_replace = 4;
                    }

                    if (indexPitch > max)
                    {
                        secondMax = max;
                        max = indexPitch;
                        to_replace = 3;
                        to_display = 3;
                    }
                    else if (indexPitch > secondMax)
                    {
                        secondMax = indexPitch;
                        to_replace = 3;
                    }

                    System.out.println("(HISTORY): to replace : " + to_replace);
                    System.out.println("(HISTORY): to display : " + to_display);

                    ArrayList<String> historyCommand = dialogData.getHistoryCommands();
                    String lastCommand = historyCommand.get(historyCommand.size() - 1).split(";")[1];

                    System.out.println("(HISTORY): last command : " + lastCommand);

                    interpret_history(lastCommand, to_display);
                    results.add(timestamp+ ";" + "successful" + ";" + dialogData.getUserRefinedQueryResult());

                    commands.add(timestamp + ";" + userCommand);
                    dialogData.setHistoryCommands(commands);
                }
            }
        }
        else if (userCommand.contains("history") || userCommand.contains("History") || userCommand.contains("command") || userCommand.contains("Command"))
        {
            System.out.println("**COMMANDS : " + " Run a history command");
            ArrayList<String> historyCommand = dialogData.getHistoryCommands();

            int num = 0;
            if(userCommand.contains("last"))
                num = historyCommand.size() - 1;
            else if(userCommand.contains("top"))
                num = 0;
            else
                num = interpret_number(userCommand);

            System.out.println("****COMMANDS : historyCommand.get("+num+").split(;)[1]");
            String desiredCommand = historyCommand.get(num).split(";")[1];

            if(!desiredCommand.equals(""))
            {
                System.out.println("Desired command run");

                if(fission.getAudion_on())
                    fissionLogic.speak_result("Desired command run " + desiredCommand);

                commandUser(desiredCommand);
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

    private int interpret_number(String userCommand)
    {
        System.out.println("****COMMANDS : interpret_number = " + userCommand);

        int num = 0;

        String[] words = userCommand.split(" ");

        for (int i = 0; i < words.length; i++) {
            if (words[i].matches("\\d+")) {
                num = Integer.parseInt(words[i]);
                break;
            }
        }
        System.out.println("The number is: " + num);

        if (num > 25)
            num = 25;

        return num;
    }

    /**
     *
     * @param num : int - number of line to display
     * @param to_display : int 1=level / 2=heart / 3=pitch / 4=amplitude / 5=noise / 6=auc
     */
    private void interpret_dialog_for_database(int num, int to_display)
    {
        System.out.println("****COMMANDS : interpret_dialog_for_database = " + num + " / " + to_display);

        ArrayList<String> result = persistenceLogic.getLastAngry(num);
        ArrayList<String> refinedResult = new ArrayList<>();


        for (String stressLine : result)
        {
            System.out.println("LOG REPORT : " + stressLine);

            String[] fields = stressLine.split(",");

            String date = fields[0];
            String value = fields[to_display];

            String[] dateDetails = date.split("-");
            String month = dateDetails[1];
            String day = dateDetails[2];
            String hour = dateDetails[3];
            String minute = dateDetails[4];

            refinedResult.add(day+"."+month + " at " + hour+ ":"+ minute + " = " + value);
        }

        dialogData.setUserQueryResult(result);
        dialogData.setUserRefinedQueryResult(refinedResult);


    }


    /**
     *
     * @param num : int - number of line to display
     * @param to_display : int 1=level / 2=heart / 3=pitch / 4=amplitude / 5=noise / 6=auc
     */
    private void interpret_dialog_for_database_by_date(int num, String type, String number, int to_display)
    {
        System.out.println("****COMMANDS : interpret_dialog_for_database_by_date = " + num + " / " + to_display + " / " + type + " / " + number);

        ArrayList<String> result = persistenceLogic.getLastAngryByDate(10, type, number);
        ArrayList<String> refinedResult = new ArrayList<>();


        for (String stressLine : result)
        {
            System.out.println("LOG REPORT : " + stressLine);

            String[] fields = stressLine.split(",");

            String date = fields[0];
            String value = fields[to_display];

            String[] dateDetails = date.split("-");
            String month = dateDetails[1];
            String day = dateDetails[2];
            String hour = dateDetails[3];
            String minute = dateDetails[4];

            refinedResult.add(day+"."+month + " at " + hour+ ":"+ minute + " = " + value);
        }

        dialogData.setUserQueryResult(result);
        dialogData.setUserRefinedQueryResult(refinedResult);

        if(fission.getAudion_on())
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
            case RESULT_SPEECH:
                if (code == RESULT_OK && data != null)
                {
                    ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    String userCommand = text.get(0);
                    Log.d(TAG, "Sphinx get : " + userCommand);

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
            case REQUEST_AUDIO_PERMISSION_CODE:
                if (grantResults.length > 0)
                {
                    boolean permissionToRecord = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean permissionToStore = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (permissionToRecord && permissionToStore)
                    {
                        Toast.makeText(recorderActivity.getApplicationContext(), "Permission Granted", Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        Toast.makeText(recorderActivity.getApplicationContext(), "Permission Denied", Toast.LENGTH_LONG).show();
                    }
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
        // this method is used to request the
        // permission for audio recording and storage.
        ActivityCompat.requestPermissions(recorderActivity, new String[]{RECORD_AUDIO, WRITE_EXTERNAL_STORAGE}, REQUEST_AUDIO_PERMISSION_CODE);
    }
}
