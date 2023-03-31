package com.example.checksensoravailability;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor; //step 4 : import library
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager; //step 4 : import library
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
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

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater()); // binder for framelayout
        setContentView(binding.getRoot());

        tbxHeartRate = binding.tbxHeartRate;
        imgView = binding.imgBackground;

        checkPermission();
        checkSensorAvailability();
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
}
