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
    private File fullFilename;

    public PersistenceLogic() throws IOException
    {
        String timestamp = getCurrentTime();

        File extDir = Environment.getExternalStorageDirectory();
        String filename = timestamp + "-heartbeat_data_set.csv";
        File fullFilename = new File(extDir, filename);

        fullFilename.delete();
        fullFilename.createNewFile();
        fullFilename.setWritable(Boolean.TRUE);

        this.fullFilename = fullFilename;
    }


    /**
     * Get the current time
     *
     * @return String - the right date yyyy-MM-dd-HH-mm-ss
     * @autor Quentin Nater
     */
    public String getCurrentTime()
    {
        // set the timestamp
        // test to test yesterday data : timestamp = "2023-05-XX-16-37-03";
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
        return now.format(formatter);
    }


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
            String timestamp = getCurrentTime();

            // Write on a new file
            FileWriter fileWriter = new FileWriter(fullFilename, true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

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


    /**
     * Get in the database the last angry time (where by date)
     *
     * @param num int - the number of occurrence asked by the user
     * @param type String - Search by "yesterday", "hour" or "last_hour"
     * @param value String - if needed the specific day or hour
     * @return result ArrayList<String> - all line of results to display for the asked query
     * @autor Quentin Nater
     */
    public ArrayList<String> getLastAngryByDate(int num, String type, String value)
    {
        System.out.println(">> Get in storage 'Number of angry times' with time");
        System.out.println("******COMMANDS : Look for data of " + type);

        File extDir = Environment.getExternalStorageDirectory();
        String filename = "heartbeat_data_set.csv";
        File fullFilename = new File(extDir, filename);

        ArrayList<String> stressLines = new ArrayList<>();

        // read the database
        try (BufferedReader br = new BufferedReader(new FileReader(fullFilename)))
        {
            String line;  // first line of the table
            while ((line = br.readLine()) != null)
            {
                String[] fields = line.split(",");

                // split the date of the table
                String date = fields[0];
                String[] dateDetails = date.split("-");
                String month = dateDetails[1];
                String day = dateDetails[2];
                String hour = dateDetails[3];

                // split the same why for the current date
                String timestamp = getCurrentTime();
                String[] dateNowDetails = timestamp.split("-");
                String monthNow = dateNowDetails[1];
                String dayNow = dateNowDetails[2];
                String hourNow = dateNowDetails[3];

                if(type.equals("yesterday"))  // if the user ask the anger value of yesterday
                {
                    // parse to prevent 01 != 1
                    int dayNowLessOne = Integer.parseInt(dayNow) - 1;
                    int dayInt = Integer.parseInt(day);

                    System.out.println("fields[1].equals(anger) && dayInt == dayNowLessOne && month.equals(monthNow)) = "+fields[1]+".equals(anger) && "+dayInt+" == "+dayNowLessOne+" && "+month+".equals("+monthNow+")");

                    // check the anger value and the right day (in the right month)
                    if (fields[1].equals("anger") && dayInt == dayNowLessOne && month.equals(monthNow))
                    {
                        stressLines.add(line);
                    }
                }
                else if(type.equals("hour"))   // if the user ask the anger value of a special hour
                {
                    // parse to prevent 01 != 1
                    int valueInt = Integer.parseInt(value);
                    int hourInt = Integer.parseInt(hour);

                    System.out.println("fields[1].equals(anger) && hourInt == valueInt && day.equals(dayNow) && month.equals(monthNow) = "+fields[1]+".equals(anger) && "+hourInt+" == "+valueInt+" && "+day+".equals("+dayNow+") && "+month+".equals("+monthNow+")");

                    // check the anger value and the right hour (in the right day (in the right month))
                    if (fields[1].equals("anger") && hourInt == valueInt && day.equals(dayNow) && month.equals(monthNow))
                    {
                        stressLines.add(line);
                    }
                }
                else if(type.equals("last_hour"))   // if the user ask the anger value of the last hour
                {
                    // parse to prevent 01 != 1
                    int dayTimeStampInt = Integer.parseInt(hourNow) - 1;
                    int hourInt = Integer.parseInt(hour);

                    System.out.println("fields[1].equals(anger) &&  hourInt == dayTimeStampInt && month.equals(monthNow)) = ("+fields[1]+".equals(anger) && " + hourInt + " == " + dayTimeStampInt + " && " + day + ".equals(" + dayNow + ") && " + month + ".equals(" + monthNow + "))");

                    // check the anger value and the right hour (in the right day (in the right month))
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

        // reverse the list to get the most recent first
        Collections.reverse(stressLines);
        ArrayList<String> result = new ArrayList<>();

        // list only the number asked by the user
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


    /**
     * Get in the database the last angry time (where by occurrence)
     *
     * @return result ArrayList<String> - all line of results to display for the asked query
     * @autor Quentin Nater
     */
    public ArrayList<String> getLastAngry(int num)
    {
        System.out.println(">> Get in storage 'Number of angry times'");

        File extDir = Environment.getExternalStorageDirectory();
        String filename = "heartbeat_data_set.csv";
        File fullFilename = new File(extDir, filename);

        ArrayList<String> stressLines = new ArrayList<>();

        // read the database
        try (BufferedReader br = new BufferedReader(new FileReader(fullFilename)))
        {
            String line;
            while ((line = br.readLine()) != null)  // each row of the file
            {
                String[] fields = line.split(",");
                if (fields[1].equals("anger"))
                {
                    stressLines.add(line);  // save if it is anger state
                }
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        // reverse the list for getting first the last ones
        Collections.reverse(stressLines);
        ArrayList<String> result = new ArrayList<>();

        // limit the result by the user will
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
