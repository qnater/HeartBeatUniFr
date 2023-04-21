package com.example.checksensoravailability;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;

import com.example.checksensoravailability.ContextUserModelHistory.PersistenceLogic;
import com.example.checksensoravailability.DialogManagement.DialogLogic;
import com.example.checksensoravailability.InputHeartBeat.HeartBeatData;
import com.example.checksensoravailability.InputHeartBeat.HeartBeatLogic;
import com.example.checksensoravailability.InputProsody.ProsodyData;
import com.example.checksensoravailability.InputProsody.ProsodyLogic;
import com.example.checksensoravailability.ModalitiesFission.FissionLogic;
import com.example.checksensoravailability.ModalitiesFusion.FusionData;
import com.example.checksensoravailability.ModalitiesFusion.FusionLogic;
import com.example.checksensoravailability.databinding.ActivityMainBinding;
import java.util.ArrayList;


public class MainActivity extends Activity implements SensorEventListener
{
    private TextView tbxHeartRate;

    private ImageView imgView;


    private static final String TAG = "____Main___";

    protected static final int RESULT_SPEECH = 1;

    // =====================================================================
    // Initializing all variables..
    private ImageView imgMic;
    private Boolean mode = true;
    private int state = 0;
    private Boolean picState = true;
    private static final int REQUEST_AUDIO_PERMISSION_CODE = 1;
    private MediaPlayer mediaPlayer;
    private HeartBeatData heartData;
    private ProsodyData prosodyData;
    private ProsodyLogic prosodyLogic;
    private FusionData fusionData;
    private FusionLogic fusionLogic;
    private FissionLogic fissionLogic;
    private DialogLogic dialogLogic;
    private PersistenceLogic persistenceLogic;
    private HeartBeatLogic heartBeatLogic;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        heartData = new HeartBeatData();
        prosodyData = new ProsodyData();
        fusionData = new FusionData();
        fusionData.setLevel("calm");

        persistenceLogic = new PersistenceLogic();
        fissionLogic = new FissionLogic(getApplicationContext());
        prosodyLogic = new ProsodyLogic(this, prosodyData);
        dialogLogic = new DialogLogic(this, getApplicationContext(), fissionLogic);
        fusionLogic = new FusionLogic(this, heartData, prosodyData, fusionData, persistenceLogic);
        heartBeatLogic = new HeartBeatLogic(this, heartData, fusionLogic);

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        tbxHeartRate = binding.tbxHeartRate;
        imgView = binding.imgBackground;
        imgMic = binding.imgMic;

        ImageView imgSphinx = binding.imgSphinx;

        checkPermission();
        checkSensorAvailability();

        prosodyLogic.extractFeatures();

        imgView.setOnClickListener(new View.OnClickListener()
        {
           @Override
           public void onClick(View v)
           {
               if(state!=3)
                   state = state + 1;
               else
                   state = 0;
           }
        });


        imgView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view)
            {
                picState = !picState;
                return false;
            }
        });


        imgMic.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "MODE :" + mode.toString());

                Drawable drawable;

                if (mode) {
                    Log.d(TAG, "START RECORDING___");
                    dialogLogic.startRecording();
                    drawable = getDrawable(R.drawable.close_mic);
                    imgMic.setImageDrawable(drawable);
                    mode = false;
                } else {
                    Log.d(TAG, "STOP RECORDING___");
                    dialogLogic.pauseRecording();
                    drawable = getDrawable(R.drawable.open_mic);
                    imgMic.setImageDrawable(drawable);
                    mode = true;
                }
            }
        });

        imgSphinx.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialogLogic.sphinxCall();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode)
        {
            case RESULT_SPEECH:
                if (resultCode == RESULT_OK && data != null)
                {
                    ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    String userCommand = text.get(0);
                    Log.d(TAG, "Sphinx get : " + userCommand);

                    dialogLogic.commandUser(userCommand);
                }
                break;
        }

    }



    private void checkSensorAvailability()
    {
        SensorManager mSensorManager = ((SensorManager) getSystemService(SENSOR_SERVICE));
        Sensor mHeartRateSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        mSensorManager.registerListener(this, mHeartRateSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }


    public void onSensorChanged(SensorEvent event)
    {
        if (event.sensor.getType() == Sensor.TYPE_HEART_RATE)
            heartData.setHeartbeat((int) event.values[0]);

        fusionLogic.sensorLogicProcessing();
        updateDisplay();
    }



    private void updateDisplay()
    {
        Drawable drawable;

        if(fusionData.getLevel().equals("calm")) // Ereshkigal
        {
            if(picState)
                drawable = getDrawable(R.drawable.relax);
            else
                drawable = getDrawable(R.drawable.offifical_calmness);

            imgView.setImageDrawable(drawable);
            tbxHeartRate.setTextColor(Color.BLACK);

            if(state == 1)
                tbxHeartRate.setText((int)prosodyData.getPitch() + "Hz");
            else if(state == 2)
                tbxHeartRate.setText((int)prosodyData.getAmplitude() + "amp");
            else if(state == 3)
                tbxHeartRate.setText((int)fusionData.getAuc() + "%");
        }
        else if (fusionData.getLevel().equals("anger"))
        {
            if(picState)
                drawable = getDrawable(R.drawable.anger);
            else
                drawable = getDrawable(R.drawable.offifical_anger);

            imgView.setImageDrawable(drawable);

            tbxHeartRate.setTextColor(Color.BLACK);

            if(state == 1)
                tbxHeartRate.setText((int)prosodyData.getPitch() + "Hz");
            else if(state == 2)
                tbxHeartRate.setText((int)prosodyData.getAmplitude() + "amp");
            else if(state == 3)
                tbxHeartRate.setText((int)fusionData.getAuc() + "%");


            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            long[] vibrationPattern = {0, 500, 50, 300};
            vibrator.vibrate(vibrationPattern, -1);
        }
        else
        {
            if(picState)
                drawable = getDrawable(R.drawable.embarassed);
            else
                drawable = getDrawable(R.drawable.offifical_stress);

            imgView.setImageDrawable(drawable);

            tbxHeartRate.setTextColor(Color.WHITE);

            if(state == 1)
                tbxHeartRate.setText((int)prosodyData.getPitch() + "Hz");
            else if(state == 2)
                tbxHeartRate.setText((int)prosodyData.getAmplitude() + "amp");
            else if(state == 3)
                tbxHeartRate.setText((int)fusionData.getAuc() + "%");
        }

        if(state == 0)
            tbxHeartRate.setText((int)heartData.getHeartbeat() + "bpm");
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {
        Log.d(TAG, "onAccuracyChanged - accuracy: " + accuracy);
    }


    private void checkPermission()
    {
        // Runtime permission ------------
        if (checkSelfPermission(Manifest.permission.BODY_SENSORS) != PackageManager.PERMISSION_GRANTED)
            requestPermissions(new String[]{Manifest.permission.BODY_SENSORS}, 1);
        else
            Log.d(TAG, "ALREADY GRANTED");
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        // this method is called when user will
        // grant the permission for audio recording.
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case REQUEST_AUDIO_PERMISSION_CODE:
                if (grantResults.length > 0)
                {
                    boolean permissionToRecord = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean permissionToStore = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (permissionToRecord && permissionToStore)
                    {
                        Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }
}
