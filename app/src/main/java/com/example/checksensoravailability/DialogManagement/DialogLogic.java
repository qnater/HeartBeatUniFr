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
import com.example.checksensoravailability.MainActivity;
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

    private static final String TAG = "DialogLogic";

    private static final int REQUEST_AUDIO_PERMISSION_CODE = 1;


    public DialogLogic(Activity mainActivity, Context context, DialogData dialogData, FissionLogic fissionLogic, PersistenceLogic persistenceLogic)
    {
        this.dialogData = dialogData;
        this.fissionLogic = fissionLogic;
        this.persistenceLogic = persistenceLogic;
        this.recorderActivity = mainActivity;
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


    /**
     * Function that starts a recording of the user voice for storage
     *
     * @param userCommand String - Value of the user command
     * @autor Quentin Nater
     */
    public void commandUser(String userCommand)
    {
        if (userCommand.contains("relax") || userCommand.contains("Relax"))
            fissionLogic.relaxationMethod(1);
        else if (userCommand.contains("motivation") || userCommand.contains("Motivation"))
            fissionLogic.relaxationMethod(2);
        else if (userCommand.contains("stop") || userCommand.contains("Stop"))
            fissionLogic.relaxationMethod(0);
        else if (userCommand.contains("victory") || userCommand.contains("Victory"))
            fissionLogic.relaxationMethod(3);
        else if (userCommand.contains("clean") || userCommand.contains("wipe"))
        {
            ArrayList<String> tmp = dialogData.getUserQueryResult();
            tmp.clear();
            ArrayList<String> tmp2 = dialogData.getUserRafinedQueryResult();
            tmp2.clear();
            dialogData.setUserQueryResult(tmp);
            dialogData.setUserRafinedQueryResult(tmp2);
        }
        else if (userCommand.contains("log") ||
                userCommand.contains("Log") ||
                userCommand.contains("get") ||
                userCommand.contains("Get") ||
                userCommand.contains("times") ||
                userCommand.contains("Times")) {

            int num = 0;
            String[] words = userCommand.split(" ");

            for (int i = 0; i < words.length; i++)
            {
                if (words[i].matches("\\d+"))
                { // check if the word is a number
                    num = Integer.parseInt(words[i]); // convert the word to an integer
                    break; // exit the loop once the first number is found
                }
            }

            System.out.println("The number is: " + num);

            if (num > 10)
                num = 10;

            ArrayList<String> result = persistenceLogic.getAngryTimes(num);
            ArrayList<String> refinedResult = new ArrayList<>();

            for (String stressLine : result)
            {
                System.out.println("LOG REPORT : " + stressLine);

                String[] fields = stressLine.split(",");

                String date = fields[0];
                String percentage = fields[6];

                String[] dateDetails = date.split("-");
                String month = dateDetails[1];
                String day = dateDetails[2];
                String hour = dateDetails[3];
                String minute = dateDetails[4];

                refinedResult.add(day+"."+month + " at " + hour+ ":"+ minute + " = " + percentage);
            }

            dialogData.setUserQueryResult(result);
            dialogData.setUserRafinedQueryResult(refinedResult);

        }
        else
            Log.d(TAG, "Pattern not understood... Sorry bro...");
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
