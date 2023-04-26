package com.example.checksensoravailability.InputHeartBeat;

import static android.content.Context.SENSOR_SERVICE;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;

import com.example.checksensoravailability.ModalitiesFusion.FusionLogic;


public class HeartBeatLogic implements SensorEventListener
{

    protected static final int REQUEST_AUDIO_PERMISSION_CODE = 1;

    private static final String TAG = "HeartBeatModality";

    private HeartBeatData heartData;
    private Activity mainActivity;
    private FusionLogic fusionLogic;
    private Context context;

    public HeartBeatLogic(Activity mainActivity, Context context, HeartBeatData heartData, FusionLogic fusionLogic)
    {
        System.out.println(">> Listener On");

        this.mainActivity = mainActivity;
        this.context = context;

        this.heartData = heartData;
        this.fusionLogic = fusionLogic;

        checkSensorAvailability();
        checkPermission();
    }

    /**
     * Function that lists and checks the sensors on the hardware to register them
     *
     * @autor Quentin Nater
     */
    public void checkSensorAvailability()
    {
        SensorManager mSensorManager = ((SensorManager) mainActivity.getSystemService(SENSOR_SERVICE));
        Sensor mHeartRateSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        mSensorManager.registerListener(this, mHeartRateSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }


    /**
     * Check permission on the system hardware
     *
     * @autor Quentin Nater
     */
    private void checkPermission()
    {
        // Runtime permission ------------
        if (context.checkSelfPermission(Manifest.permission.BODY_SENSORS) != PackageManager.PERMISSION_GRANTED)
            mainActivity.requestPermissions(new String[]{Manifest.permission.BODY_SENSORS}, 1);
        else
            Log.d(TAG, "ALREADY GRANTED");
    }

    /**
     * Function that is called every time the sensor has changed
     *
     * @param event SensorEvent - Result with the sensor data
     * @autor Quentin Nater
     */
    public void onSensorChanged(SensorEvent event)
    {
        if (event.sensor.getType() == Sensor.TYPE_HEART_RATE)
            heartData.setHeartbeat((int) event.values[0]);

        fusionLogic.sensorLogicProcessing();
    }

    /**
     * Function that is called every time the sensor has changed
     *
     * @param sensor Sensor - Result with the sensor data
     * @param i - int - Accuracy of the sensor
     * @autor Quentin Nater
     */
    public void onAccuracyChanged(Sensor sensor, int i)
    {
        Log.d(TAG, "onAccuracyChanged - accuracy: " + i);
    }
}
