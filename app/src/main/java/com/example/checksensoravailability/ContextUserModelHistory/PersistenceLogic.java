package com.example.checksensoravailability.ContextUserModelHistory;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;

public class PersistenceLogic
{

    private static final String TAG = "PersistenceLogic";
    private final Boolean createFile = true;
    private int i = 0;


    /**
     * Write dataset of the fusion data with prediction
     *
     * @param heartBeat int - Value of the heartbeat
     * @param pitch float - Value of prosody pitch
     * @param amplitude float - Value of prosody amplitude
     * @param auc int - Value of Area Under the Curve
     * @param noise float - Value of the noise of the prediction
     * @param level String - prediction of the logic
     * @throws IOException e - Cannot write
     * @autor Quentin Nater
     */
    public void writeDataset(int heartBeat, float pitch, float amplitude, int auc, float noise, String level)
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

            // test
            // timestamp = "2023-05-04-16-37-03";


            // write the timestamp and value to the file
            bufferedWriter.write(timestamp + "," + level + "," + heartBeat + "bpm," + pitch + "Hz," + amplitude + "dBm," + noise + "%," + auc + "%");
            Log.d(TAG, "timestamp , level, heatBeat, pitch, amplitude, noise, auc");
            Log.d(TAG, "" + timestamp + "," + level + "," + heartBeat + "bpm," + pitch + "Hz," + amplitude + "dBm," +  noise + "%," + auc + "%");
            bufferedWriter.newLine();

            // close the buffered writer
            bufferedWriter.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }


    public ArrayList<String> getLastAngryByDate(int num, String type, String value)
    {
        System.out.println(">> Get in storage 'Number of angry times' with time");

        System.out.println("******COMMANDS : Look for data of " + type);

        File extDir = Environment.getExternalStorageDirectory();
        String filename = "heartbeat_data_set.csv";
        File fullFilename = new File(extDir, filename);


        ArrayList<String> stressLines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(fullFilename)))
        {
            String line;
            while ((line = br.readLine()) != null)
            {
                String[] fields = line.split(",");

                String date = fields[0];
                String[] dateDetails = date.split("-");
                String month = dateDetails[1];
                String day = dateDetails[2];
                String hour = dateDetails[3];
                String minute = dateDetails[4];


                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
                String timestamp = now.format(formatter);
                String[] dateNowDetails = timestamp.split("-");
                String monthNow = dateNowDetails[1];
                String dayNow = dateNowDetails[2];
                String hourNow = dateNowDetails[3];
                String minuteNow = dateNowDetails[4];

                if(type.equals("yesterday"))
                {
                    int dayNowLessOne = Integer.parseInt(dayNow) - 1;
                    int dayInt = Integer.parseInt(day);

                    System.out.println("fields[1].equals(anger) && dayInt == dayNowLessOne && month.equals(monthNow)) = "+fields[1]+".equals(anger) && "+dayInt+" == "+dayNowLessOne+" && "+month+".equals("+monthNow+")");
                    if (fields[1].equals("anger") && dayInt == dayNowLessOne && month.equals(monthNow))
                    {
                        stressLines.add(line);
                    }
                }
                else if(type.equals("hour"))
                {
                    int valueInt = Integer.parseInt(value);
                    int hourInt = Integer.parseInt(hour);

                    System.out.println("fields[1].equals(anger) && hourInt == valueInt && day.equals(dayNow) && month.equals(monthNow) = "+fields[1]+".equals(anger) && "+hourInt+" == "+valueInt+" && "+day+".equals("+dayNow+") && "+month+".equals("+monthNow+")");

                    if (fields[1].equals("anger") && hourInt == valueInt && day.equals(dayNow) && month.equals(monthNow))
                    {
                        stressLines.add(line);
                    }
                }
                else if(type.equals("last_hour"))
                {
                    int dayTimeStampInt = Integer.parseInt(hourNow) - 1;
                    int hourInt = Integer.parseInt(hour);

                    System.out.println("fields[1].equals(anger) &&  hourInt == dayTimeStampInt && month.equals(monthNow)) = ("+fields[1]+".equals(anger) && " + hourInt + " == " + dayTimeStampInt + " && " + day + ".equals(" + dayNow + ") && " + month + ".equals(" + monthNow + "))");

                    if (fields[1].equals("anger") && hourInt == dayTimeStampInt && day.equals(dayNow) && month.equals(monthNow))
                    {
                        System.out.println("\t PUT INSIDE");
                        stressLines.add(line);
                    }
                }
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        Collections.reverse(stressLines);
        ArrayList<String> result = new ArrayList<>();


        int count = 0;
        for (String stressLine : stressLines)
        {
            result.add(stressLine);
            count++;
            if (count == num)
                break;
        }

        return result;
    }


    public ArrayList<String> getLastAngry(int num)
    {
        System.out.println(">> Get in storage 'Number of angry times'");

        File extDir = Environment.getExternalStorageDirectory();
        String filename = "heartbeat_data_set.csv";
        File fullFilename = new File(extDir, filename);


        ArrayList<String> stressLines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(fullFilename)))
        {
            String line;
            while ((line = br.readLine()) != null)
            {
                String[] fields = line.split(",");
                if (fields[1].equals("anger"))
                {
                    stressLines.add(line);
                }
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        Collections.reverse(stressLines);
        ArrayList<String> result = new ArrayList<>();


        int count = 0;
        for (String stressLine : stressLines)
        {
            result.add(stressLine);
            count++;
            if (count == num)
                break;
        }

        return result;
    }
}
