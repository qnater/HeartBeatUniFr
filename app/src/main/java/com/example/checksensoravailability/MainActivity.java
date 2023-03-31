package com.example.checksensoravailability;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor; //step 4 : import library
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager; //step 4 : import library
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.checksensoravailability.databinding.ActivityMainBinding;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;




public class MainActivity extends Activity implements SensorEventListener
{
    private TextView tbxHeartRate;
    private ImageView imgView;
    private Boolean createFile = true;

    private int i = 0;

    private ActivityMainBinding binding;
    private static final String TAG = "____Main___";

    // =====================================================================
    // Initializing all variables..
    private ImageView imgMic;

    private Boolean mode = true;
    private int start = 0;

    // creating a variable for media recorder object class.
    private MediaRecorder mRecorder;

    // creating a variable for mediaplayer class
    private MediaPlayer mPlayer;

    // string variable is created for storing a file name
    private static String mFileName = null;

    // constant for storing audio permission
    public static final int REQUEST_AUDIO_PERMISSION_CODE = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater()); // binder for framelayout
        setContentView(binding.getRoot());

        tbxHeartRate = binding.tbxHeartRate;
        imgView = binding.imgBackground;
        imgMic = binding.imgMic;

        checkPermission();
        checkSensorAvailability();

        imgMic.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.d(TAG, "MODE :" + mode.toString());

                Drawable drawable;

                if(mode)
                {
                    Log.d(TAG, "START RECORDING___");
                    startRecording();
                    drawable = getDrawable(R.drawable.close_mic);
                    imgMic.setImageDrawable(drawable);
                    mode = false;
                }
                else
                {
                    Log.d(TAG, "STOP RECORDING___");
                    pauseRecording();
                    drawable = getDrawable(R.drawable.open_mic);
                    imgMic.setImageDrawable(drawable);
                    mode = true;
                }
            }
        });

    }

    private void checkPermission()
    {
        // Runtime permission ------------
        if (checkSelfPermission(Manifest.permission.BODY_SENSORS) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{Manifest.permission.BODY_SENSORS}, 1);
        }
        else
        {
            Log.d(TAG, "ALREADY GRANTED");
        }
    }


    private void checkSensorAvailability()
    {
        SensorManager mSensorManager = ((SensorManager) getSystemService(SENSOR_SERVICE));

        //List of integrated sensor of device---------
        List<Sensor> sensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        ArrayList<String> arrayList = new ArrayList<String>();
        for (Sensor sensor : sensors)
        {
            arrayList.add(sensor.getName());
        }
        Log.d(TAG, " " + arrayList);

        Sensor mHeartRateSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        mSensorManager.registerListener(this, mHeartRateSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }


    public void onSensorChanged(SensorEvent event)
    {
        if (event.sensor.getType() == Sensor.TYPE_HEART_RATE)
        {
            int heatBeat = (int) event.values[0];

            if (heatBeat < 80)
            {
                Drawable drawable = getDrawable(R.drawable.relax);
                imgView.setImageDrawable(drawable);
                tbxHeartRate.setTextColor(Color.BLACK);

            }
            else if (heatBeat > 80 && heatBeat < 100)
            {
                Drawable drawable = getDrawable(R.drawable.embarassed);
                imgView.setImageDrawable(drawable);
                tbxHeartRate.setTextColor(Color.WHITE);

            }
            else
            {
                Drawable drawable = getDrawable(R.drawable.angry);
                imgView.setImageDrawable(drawable);
                tbxHeartRate.setTextColor(Color.YELLOW);

            }
            String msg = "" + heatBeat;

            tbxHeartRate.setText(msg + " bpm");

            Log.d(TAG, msg);

            writeDataset(heatBeat);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {
        Log.d(TAG, "onAccuracyChanged - accuracy: " + accuracy);
    }

    private void writeDataset(int heatBeat)
    {
        try
        {
            File extDir = Environment.getExternalStorageDirectory();
            String filename = "heartbeat_data_set.csv";
            File fullFilename = new File(extDir, filename);

            if (createFile && i == 0)
            {
                fullFilename.delete();
                fullFilename.createNewFile();
                fullFilename.setWritable(Boolean.TRUE);
                i++;
            }

            // Write on a new file
            FileWriter fileWriter = new FileWriter(fullFilename, true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            // set the timestamp
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
            String timestamp = now.format(formatter);

            // write the timestamp and value to the file
            bufferedWriter.write(timestamp + "," + heatBeat);
            Log.d(TAG, "timestamp : " + timestamp + ", " + heatBeat);
            bufferedWriter.newLine();

            // close the buffered writer
            bufferedWriter.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }


    private void startRecording()
    {
        // check permission method is used to check
        // that the user has granted permission
        // to record and store the audio.
        if (CheckPermissions())
        {
            // we are here initializing our filename variable
            // with the path of the recorded audio file.
            mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();

            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
            String timestamp = now.format(formatter);

            mFileName += "/"+timestamp+"record.3gp";

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
            } catch (IOException e) {
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


    public void pauseRecording()
    {
         /*
        stopTV.setBackgroundColor(getResources().getColor(R.color.gray));
        startTV.setBackgroundColor(getResources().getColor(R.color.purple_200));
        playTV.setBackgroundColor(getResources().getColor(R.color.purple_200));
        stopplayTV.setBackgroundColor(getResources().getColor(R.color.purple_200));
        */
        // below method will stop
        // the audio recording.
        mRecorder.stop();

        // below method will release
        // the media recorder class.
        Log.d(TAG, "Stop recording");
        mRecorder.release();
        mRecorder = null;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // this method is called when user will
        // grant the permission for audio recording.
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_AUDIO_PERMISSION_CODE:
                if (grantResults.length > 0) {
                    boolean permissionToRecord = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean permissionToStore = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (permissionToRecord && permissionToStore) {
                        Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    public boolean CheckPermissions() {
        // this method is used to check permission
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void RequestPermissions() {
        // this method is used to request the
        // permission for audio recording and storage.
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{RECORD_AUDIO, WRITE_EXTERNAL_STORAGE}, REQUEST_AUDIO_PERMISSION_CODE);
    }


}
