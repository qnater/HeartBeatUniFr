package com.example.checksensoravailability.ContextUserModelHistory;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PersistenceLogic
{

    private static final String TAG = "PersistenceLogic";
    private final Boolean createFile = true;
    private int i = 0;


    public void writeDataset(int heatBeat, float pitch, float amplitude, int auc, float noise, String level)
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
            bufferedWriter.write(timestamp + "," + level + "," + heatBeat + "bpm," + pitch + "Hz," + amplitude + "dBm," + noise + "%," + auc + "%");
            Log.d(TAG, "timestamp , level, heatBeat, pitch, noise, auc, amplitude");
            Log.d(TAG, "" + timestamp + "," + level + "," + heatBeat + "bpm," + pitch + "Hz," + amplitude + "dBm," +  noise + "%," + auc + "%");
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
