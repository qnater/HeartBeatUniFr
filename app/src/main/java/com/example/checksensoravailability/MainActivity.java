package com.example.checksensoravailability;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
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

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;
import be.tarsos.dsp.pitch.PitchProcessor.PitchEstimationAlgorithm;
import be.tarsos.dsp.util.fft.FFT;


public class MainActivity extends Activity implements SensorEventListener
{
    private TextView tbxHeartRate;

    private ImageView imgView;
    private ImageView imgSphinx;
    private Boolean createFile = true;

    private int i = 0;

    private ActivityMainBinding binding;
    private static final String TAG = "____Main___";

    protected static final int RESULT_SPEECH = 1;

    // =====================================================================
    // Initializing all variables..
    private ImageView imgMic;

    private Boolean mode = true;

    private Boolean deadLock = false;
    private int state = 0;
    private Boolean picState = true;

    // creating a variable for media recorder object class.
    private MediaRecorder mRecorder;

    // creating a variable for mediaplayer class
    private MediaPlayer mPlayer;

    // string variable is created for storing a file name
    private static String mFileName = null;
    private static String mFileNameSound = null;

    private float pitch = 0;
    private float amplitude = 0;
    private float pitchProbability = 0;

    private int auc = 0;


    // constant for storing audio permission
    public static final int REQUEST_AUDIO_PERMISSION_CODE = 1;

    public static final int calmnessHeartBeat = 80;
    public static final int angerHeartBeat = 100;

    public static final int calmnessPitch = 132;
    public static final int angerPitch = 200;

    public static final int calmnessAmplitude = 40;
    public static final int angerAmplitude = 50;


    MediaPlayer mediaPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.dcl);

        setContentView(binding.getRoot());

        tbxHeartRate = binding.tbxHeartRate;
        imgView = binding.imgBackground;
        imgMic = binding.imgMic;
        imgSphinx = binding.imgSphinx;

        checkPermission();
        checkSensorAvailability();


        extractFeatures();

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


        imgMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "MODE :" + mode.toString());

                Drawable drawable;

                if (mode) {
                    Log.d(TAG, "START RECORDING___");
                    startRecording();
                    drawable = getDrawable(R.drawable.close_mic);
                    imgMic.setImageDrawable(drawable);
                    mode = false;
                } else {
                    Log.d(TAG, "STOP RECORDING___");
                    pauseRecording();
                    drawable = getDrawable(R.drawable.open_mic);
                    imgMic.setImageDrawable(drawable);
                    mode = true;
                }
            }
        });

        imgSphinx.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                sphynxCall();
            }
        });

    }


    private void sphynxCall()
    {
        deadLock = false;

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");

        try {
            startActivityForResult(intent, RESULT_SPEECH);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getApplicationContext(), "Your device doesn't support Speach to Text", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        deadLock = true;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RESULT_SPEECH:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    String userCommand = text.get(0);
                    Log.d(TAG, "Sphynx get : " + userCommand);


                    if (userCommand.contains("relax")) {
                        relaxationMethod(1);
                    } else if (userCommand.contains("motivation")) {
                        relaxationMethod(2);
                    } else if (userCommand.contains("stop")) {
                        relaxationMethod(0);
                    } else if (userCommand.contains("victory")) {
                        relaxationMethod(3);
                    } else {
                        Log.d(TAG, "Pattern not understood... Sorry bro...");
                    }
                }
                break;
        }

    }

    private void relaxationMethod(int played)
    {
        switch (played) {
            case 1:
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.dcl);
                mediaPlayer.start();
                break;
            case 2:
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.op);
                mediaPlayer.start();
                break;
            case 3:
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.bip);
                mediaPlayer.start();
                break;
            default:
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                break;
        }
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

        Sensor mHeartRateSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        mSensorManager.registerListener(this, mHeartRateSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }


    public void onSensorChanged(SensorEvent event)
    {
        if (event.sensor.getType() == Sensor.TYPE_HEART_RATE)
        {
            int heatBeat = (int) event.values[0];
            Drawable drawable;
            String level = "";

            if (heatBeat < calmnessHeartBeat && pitch < calmnessPitch && amplitude < calmnessAmplitude)  // Erishkigal
            {
                level = "calm";

                if(picState)
                    drawable = getDrawable(R.drawable.relax);
                else
                    drawable = getDrawable(R.drawable.offifical_calmness);

                imgView.setImageDrawable(drawable);

                tbxHeartRate.setTextColor(Color.BLACK);

                auc = asses_result(heatBeat, pitch, amplitude, level);

                if(state == 1)
                    tbxHeartRate.setText((int)pitch + "Hz");
                else if(state == 2)
                    tbxHeartRate.setText((int)amplitude + "amp");
                else if(state == 3)
                    tbxHeartRate.setText(auc + "%");
            }
            else if (heatBeat >= angerHeartBeat && pitch >= angerPitch && amplitude >= angerAmplitude)  // Eriri
            {
                level = "anger";

                if(picState)
                    drawable = getDrawable(R.drawable.anger);
                else
                    drawable = getDrawable(R.drawable.offifical_anger);

                imgView.setImageDrawable(drawable);

                tbxHeartRate.setTextColor(Color.BLACK);

                auc = asses_result(heatBeat, pitch, amplitude, level);

                if(state == 1)
                    tbxHeartRate.setText((int)pitch + "Hz");
                else if(state == 2)
                    tbxHeartRate.setText((int)amplitude + "amp");
                else if(state == 3)
                    tbxHeartRate.setText(auc + "%");

                Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                long[] vibrationPattern = {0, 500, 50, 300};
                vibrator.vibrate(vibrationPattern, -1);
            }
            else // Tohsaka
           // else if ((heatBeat >= calmnessHeartBeat && heatBeat < angerHeartBeat) || (pitch >= calmnessPitch && pitch < angerPitch) || (amplitude >= calmnessAmplitude && amplitude < angerAmplitude))
            {
                level = "stress";

                if(picState)
                    drawable = getDrawable(R.drawable.embarassed);
                else
                    drawable = getDrawable(R.drawable.offifical_stress);

                imgView.setImageDrawable(drawable);

                tbxHeartRate.setTextColor(Color.WHITE);

                auc = asses_result(heatBeat, pitch, amplitude, level);

                if(state == 1)
                    tbxHeartRate.setText((int)pitch + "Hz");
                else if(state == 2)
                    tbxHeartRate.setText((int)amplitude + "amp");
                else if(state == 3)
                    tbxHeartRate.setText(auc + "%");
            }

            String msg = "" + heatBeat;

            float noise = ((1-pitchProbability)*100);

            System.out.println("Sensors Features - Heart Beats : \t" + heatBeat + " bpms");
            System.out.println("Prosody Features - Pitch in Hz : \t" + pitch + " Hz");
            System.out.println("Prosody Features - Amplitudes  : \t" + amplitude + " dBm");
            System.out.println("Prosody Features - Noise       : \t" + noise + "%");
            System.out.println("Multimodal value - LEVEL       : \t" + level);
            System.out.println("Multimodal value - AUC         : \t" + auc + "%");

            if(state == 0)
                tbxHeartRate.setText(msg + "bpm");

            writeDataset(heatBeat, pitch, amplitude, auc, noise, level);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {
        Log.d(TAG, "onAccuracyChanged - accuracy: " + accuracy);
    }

    private void writeDataset(int heatBeat, float pitch, float amplitude, int auc, float noise, String level)
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
            bufferedWriter.write(timestamp + "," + level + "," + heatBeat + "bpms," + pitch + "Hz," + amplitude + "dBm," + noise + "%," + auc + "%");
            Log.d(TAG, "timestamp , level, heatBeat, pitch, noise, auc, amplitude");
            Log.d(TAG, "" + timestamp + "," + level + "," + heatBeat + "bpms," + pitch + "Hz," + amplitude + "dBm," +  noise + "%," + auc + "%");
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
            } catch (IOException e) {
                Log.e("TAG", "prepare() failed");
            }
            // start method will start
            // the audio recording.
            Log.d(TAG, "Start recording");

            mRecorder.start();

        } else {
            // if audio recording permissions are
            // not granted by user below method will
            // ask for runtime permission for mic and storage.
            RequestPermissions();
        }
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

    public boolean CheckPermissions()
    {
        // this method is used to check permission
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void RequestPermissions()
    {
        // this method is used to request the
        // permission for audio recording and storage.
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{RECORD_AUDIO, WRITE_EXTERNAL_STORAGE}, REQUEST_AUDIO_PERMISSION_CODE);
    }

    private void extractFeatures()
    {
        mFileNameSound = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileNameSound += "/" ;

        Log.d("ProsodyFeatures", "mFileNameSound (): " + mFileNameSound);


        AudioDispatcher dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050,1024,0);
        PitchDetectionHandler pdh = new PitchDetectionHandler()
        {
            @Override
            public void handlePitch(PitchDetectionResult res, AudioEvent e)
            {
                final float pitchInHz = res.getPitch();
                final float pitchNoise = res.getProbability();
                final float[] amp = {0};
                final float[] amplitudes = new float[e.getBufferSize()];


                float[] audioFloatBuffer = e.getFloatBuffer();
                float[] transformBuffer = new float[e.getBufferSize() * 2];
                FFT fft = new FFT(e.getBufferSize());
                System.arraycopy(audioFloatBuffer, 0, transformBuffer, 0, audioFloatBuffer.length);
                fft.forwardTransform(transformBuffer);
                fft.modulus(transformBuffer, amplitudes);

                for (int index = 0; index < amplitudes.length; index++)
                {
                    if (amplitudes[index] > amp[0])
                    {
                        amp[0] = amplitudes[index];
                    }
                }


                pitch = (int) pitchInHz;
                amplitude= (int) amp[0];
                pitchProbability = pitchNoise;

                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {

                    }
                });
            }
        };
        AudioProcessor pitchProcessor = new PitchProcessor(PitchEstimationAlgorithm.FFT_YIN, 22050, 1024, pdh);
        dispatcher.addAudioProcessor(pitchProcessor);

        Thread audioThread = new Thread(dispatcher, "Audio Thread");
        audioThread.start();
    }

    private int asses_result(int heatbeat, float pitch, float amplitude, String level)
    {
        int result = 0;

        if(level.equals("calm"))
        {
            if (heatbeat < calmnessHeartBeat && pitch < calmnessPitch && amplitude < calmnessAmplitude)
                result = 100;
        }
        else if (level.equals("stress"))
        {
            // ALL
            if ((heatbeat >= calmnessHeartBeat && heatbeat < angerHeartBeat) && (pitch >= calmnessPitch && pitch < angerPitch) && (amplitude >= calmnessAmplitude && amplitude < angerAmplitude))
                result = 100;

            // NO HEART BEAT
            else if ((heatbeat < calmnessHeartBeat || heatbeat > angerHeartBeat) && (pitch >= calmnessPitch && pitch < angerPitch) && (amplitude >= calmnessAmplitude && amplitude < angerAmplitude))
                result = 83;

            // NO PITCH
            else if ((heatbeat >= calmnessHeartBeat && heatbeat < angerHeartBeat) && (pitch < calmnessPitch || pitch > angerPitch) && (amplitude >= calmnessAmplitude && amplitude < angerAmplitude))
                result = 83;

            // NO AMPLITUDE
            else if ((heatbeat >= calmnessHeartBeat && heatbeat < angerHeartBeat) && (pitch >= calmnessPitch && pitch < angerPitch) && (amplitude < calmnessAmplitude || amplitude > angerAmplitude))
                result = 83;


                // NO HEART BEAT && NO PITCH
                else if ((heatbeat < calmnessHeartBeat || heatbeat > angerHeartBeat) && (pitch < calmnessPitch || pitch > angerPitch) && (amplitude >= calmnessAmplitude && amplitude < angerAmplitude))
                    result = 67;

                // NO HEART BEAT && NO AMPLITUDE
                else if ((heatbeat < calmnessHeartBeat || heatbeat > angerHeartBeat) && (pitch >= calmnessPitch && pitch < angerPitch) && (amplitude < calmnessAmplitude || amplitude > angerAmplitude))
                    result = 67;

                // NO PITCH && NO AMPLITUDE
                else if ((heatbeat >= calmnessHeartBeat && heatbeat < angerHeartBeat) && (pitch < calmnessPitch || pitch > angerPitch) && (amplitude < calmnessAmplitude || amplitude > angerAmplitude))
                    result = 67;

                    else
                        result = 50;
        }
        else if (level.equals("anger"))
        {
            if (heatbeat >= angerHeartBeat && pitch >= angerPitch && amplitude >= angerAmplitude)
                result = 100;
        }

        System.out.println("Result : " +  result);

        return  result;
    }

}
