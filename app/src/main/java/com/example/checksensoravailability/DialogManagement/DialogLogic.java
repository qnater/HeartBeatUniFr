package com.example.checksensoravailability.DialogManagement;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

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

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.checksensoravailability.MainActivity;
import com.example.checksensoravailability.ModalitiesFission.FissionLogic;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DialogLogic
{
    protected static final int RESULT_SPEECH = 1;


    private Boolean deadLock = false;
    private Activity recorderActivity;
    private Context context;
    private MediaRecorder mRecorder;

    private FissionLogic fissionLogic;
    private static final String TAG = "DialogLogic";

    private static final int REQUEST_AUDIO_PERMISSION_CODE = 1;


    public DialogLogic(Activity recorderActivity, Context context, FissionLogic fissionLogic)
    {
        this.fissionLogic = fissionLogic;
        this.recorderActivity = recorderActivity;
        this.context = context;
    }

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


    public boolean CheckPermissions()
    {
        // this method is used to check permission
        int result = ContextCompat.checkSelfPermission(context, WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(context, RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

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
        else
            Log.d(TAG, "Pattern not understood... Sorry bro...");
    }


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


    private void RequestPermissions()
    {
        // this method is used to request the
        // permission for audio recording and storage.
        ActivityCompat.requestPermissions(recorderActivity, new String[]{RECORD_AUDIO, WRITE_EXTERNAL_STORAGE}, REQUEST_AUDIO_PERMISSION_CODE);
    }
}
