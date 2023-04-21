package com.example.checksensoravailability.InputHeartBeat;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;

import com.example.checksensoravailability.ModalitiesFusion.FusionLogic;


public class HeartBeatLogic
{

    protected static final int REQUEST_AUDIO_PERMISSION_CODE = 1;

    private static final String TAG = "HeartBeatModality";

    private HeartBeatData heartData;
    private Activity mainActivity;
    private FusionLogic fusionLogic;

    public HeartBeatLogic(Activity mainActivity, HeartBeatData heartData, FusionLogic fusionLogic)
    {
        System.out.println(">> Listener On");

        this.heartData = heartData;
        this.mainActivity = mainActivity;
        this.fusionLogic = fusionLogic;
    }

    /*
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        checkSensorAvailability();
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
        {
            heartData.setHeartbeat((int) event.values[0]);
        }

        fusionLogic.sensorLogicProcessing();
    }


    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {
       Log.d(TAG, "onAccuracyChanged - accuracy: " + accuracy);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture)
    {
        super.onPointerCaptureChanged(hasCapture);
    }
    */

}
