package com.example.checksensoravailability;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
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


public class MainActivity extends Activity
{
    // =====================================================================
    // Initializing all variables..
    private static final String TAG = "____Main___";


    // ==== Graphical objects ==============================================
    private TextView tbxHeartRate;
    private ImageView imgView;
    private ImageView imgMic;

    // ==== Display logic ==================================================
    private Boolean mode = true;
    private int state = 0;
    private Boolean picState = true;

    // ==== Logic as library ===============================================
    private HeartBeatData heartData;
    private ProsodyData prosodyData;
    private FusionData fusionData;
    private DialogLogic dialogLogic;

    /**
     * Function that handles the creation of the application
     *
     * @param savedInstanceState Bundle - Instance of the application
     * @autor Quentin Nater
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // logical initialization of objects
        heartData = new HeartBeatData();
        prosodyData = new ProsodyData();
        fusionData = new FusionData();
        fusionData.setLevel("calm");

        PersistenceLogic persistenceLogic = new PersistenceLogic();
        FissionLogic fissionLogic = new FissionLogic(getApplicationContext());
        ProsodyLogic prosodyLogic = new ProsodyLogic(this, prosodyData);
        dialogLogic = new DialogLogic(this, getApplicationContext(), fissionLogic);
        FusionLogic fusionLogic = new FusionLogic(this, heartData, prosodyData, fusionData, persistenceLogic);
        HeartBeatLogic heartBeatLogic = new HeartBeatLogic(this, getApplicationContext(), heartData, fusionLogic);

        // Initialize the activity
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get the graphical objects
        tbxHeartRate = binding.tbxHeartRate;
        imgView = binding.imgBackground;
        imgMic = binding.imgMic;
        ImageView imgSphinx = binding.imgSphinx;

        // Start extract voice of the user
        prosodyLogic.extractFeatures();

        // ========================================================================================
        // LISTERNER OF GRAPHICAL OBJECTS
        imgView.setOnClickListener(new View.OnClickListener()
        {
           @Override
           public void onClick(View v)
           {
               // Switch data (variables) to display
               if(state!=3)
                   state = state + 1;
               else
                   state = 0;
           }
        });


        imgView.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View view)
            {
                // Change background
                picState = !picState;
                return picState;
            }
        });


        imgMic.setOnClickListener(new View.OnClickListener()
        {
            // Mic and record
            @Override
            public void onClick(View v)
            {
                Log.d(TAG, "MODE :" + mode.toString());

                Drawable drawable;

                if (mode)
                {
                    Log.d(TAG, "START RECORDING___");
                    dialogLogic.startRecording();
                    drawable = getDrawable(R.drawable.close_mic);
                    imgMic.setImageDrawable(drawable);
                    mode = false;
                } else
                {
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


        // Handle refresh threshold of data
        int refreshThreshold = 500;
        Handler handler = new Handler();
        Runnable runnable = new Runnable()
        {
            @Override
            public void run()
            {
                updateDisplay();
                handler.postDelayed(this, refreshThreshold); // Call this runnable again after 2 seconds
            }
        };
        handler.postDelayed(runnable, refreshThreshold); // Call this runnable after 2 seconds
    }

    /**
     * Change the display based on the receive data
     *
     * @autor Quentin Nater
     */
    public void updateDisplay()
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


    /**
     * Function that handles the user request on the main application
     *
     * @param requestCode int - Request the user command
     * @param resultCode int - System code the user command
     * @param data Intent - Date receive by the user command
     * @autor Quentin Nater
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        // result of the record
        super.onActivityResult(requestCode, resultCode, data);
        dialogLogic.handleUserRequest(requestCode, resultCode, data);
    }

    /**
     * Function that handles the system right of user request on the main application
     *
     * @param requestCode int -  System code the user command
     * @param permissions String[] - List of permissions requested
     * @param grantResults int[] - List of permissions granted
     * @autor Quentin Nater
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        // request for the record
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        dialogLogic.requestUser(requestCode, permissions, grantResults);
    }
}
