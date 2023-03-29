package com.example.checksensoravailability;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor; //step 4 : import library
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager; //step 4 : import library
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import com.example.checksensoravailability.databinding.ActivityMainBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class MainActivity extends Activity implements SensorEventListener {

    private TextView tv_heartRate;

    private ActivityMainBinding binding;
    private static final String TAG = "____Main___";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater()); // binder for framelayout
        setContentView(binding.getRoot());

        tv_heartRate = binding.textHEARTRATE;

        checkPermission();
        checkSensorAvailability();

    }

    private void checkPermission()
    { // step 3 started (according to content detail)

        // Runtime permission ------------
        if (checkSelfPermission(Manifest.permission.BODY_SENSORS) // check runtime permission for BODY_SENSORS
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                    new String[]{Manifest.permission.BODY_SENSORS}, 1); // If BODY_SENSORS permission has not been taken before then ask for the permission with popup
        } else {
            Log.d(TAG, "ALREADY GRANTED"); //if BODY_SENSORS is allowed for this app then print this line in log.
        }
    }


    private void checkSensorAvailability()
    {
        SensorManager mSensorManager = ((SensorManager) getSystemService(SENSOR_SERVICE)); //Step 5: SensorManager Instantiate

        //List of integrated sensor of device---------
        List<Sensor> sensors = mSensorManager.getSensorList(Sensor.TYPE_ALL); //Step 6: List of integrated sensors.
        ArrayList<String> arrayList = new ArrayList<String>();
        for (Sensor sensor : sensors)
        {
            arrayList.add(sensor.getName()); // put integrated sensor list in arraylist
        }
        Log.d(TAG, " " + arrayList); // print the arraylist in log.

        Sensor mHeartRateSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        mSensorManager.registerListener( this, mHeartRateSensor, SensorManager.SENSOR_DELAY_NORMAL);

    }

    private String currentTimeStr()
    {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        return df.format(c.getTime());
    }

    public void onSensorChanged(SensorEvent event)
    {
        if (event.sensor.getType() == Sensor.TYPE_HEART_RATE)
        {
            String msg = "" + (int)event.values[0];
            tv_heartRate.setText(msg + " bpm");
            Log.d(TAG, msg);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {
        Log.d(TAG, "onAccuracyChanged - accuracy: " + accuracy);
    }
}