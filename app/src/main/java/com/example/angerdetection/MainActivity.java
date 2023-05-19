package com.example.angerdetection;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.angerdetection.ContextUserModelHistory.PersistenceLogic;
import com.example.angerdetection.DialogManagement.DialogData;
import com.example.angerdetection.DialogManagement.DialogLogic;
import com.example.angerdetection.Display.ViewHolderAdapter;
import com.example.angerdetection.InputHeartBeat.HeartBeatData;
import com.example.angerdetection.InputHeartBeat.HeartBeatLogic;
import com.example.angerdetection.InputProsody.ProsodyData;
import com.example.angerdetection.InputProsody.ProsodyLogic;
import com.example.angerdetection.ModalitiesFission.Fission;
import com.example.angerdetection.ModalitiesFission.FissionLogic;
import com.example.angerdetection.ModalitiesFusion.Fusion;
import com.example.angerdetection.ModalitiesFusion.FusionData;
import com.example.angerdetection.ModalitiesFusion.FusionLogic;
import com.example.angerdetection.databinding.ActivityMainBinding;

import java.io.IOException;


/**
 * Anger Detection
 * This project is directed by the University of Fribourg in the context of the course FS2023: 03035/33035 Multimodal User Interfaces
 * Matilde De Luigi / Quentin Nater
 */
public class MainActivity extends Activity
{
    // =====================================================================
    // Initializing all variables...
    private static final String TAG = "____Main___";

    // ==== Graphical objects ==============================================
    private TextView tbxHeartRate;
    private ImageView imgView;
    private ImageView imgMic;

    // ==== Display logic ==================================================
    private Boolean mode = false;
    private int state = 0;
    private Boolean picState = false;

    // ==== Logic as library ===============================================
    private FusionData fusionData;
    private DialogData dialogData;
    private DialogLogic dialogLogic;
    private Fusion fusion;
    private Fission fission;
    private RecyclerView myQueryView;
    private ActivityMainBinding binding;
    private int buttonPressCount = 0;

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
        fission = new Fission();
        fusion = new Fusion(fission);
        HeartBeatData heartData = new HeartBeatData();
        ProsodyData prosodyData = new ProsodyData();
        dialogData = new DialogData();
        fusionData = new FusionData();
        fusionData.setLevel("calm");

        PersistenceLogic persistenceLogic = null;
        try // security to reach the database
        {
            persistenceLogic = new PersistenceLogic();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        // set all modules of the project
        DialogLogic first = new DialogLogic(this, getApplicationContext(), fission, dialogData, null, persistenceLogic);
        FissionLogic fissionLogic = new FissionLogic(getApplicationContext(), fission, fusion, first);
        ProsodyLogic prosodyLogic = new ProsodyLogic(fusion);
        dialogLogic = new DialogLogic(this, getApplicationContext(), fission, dialogData, fissionLogic, persistenceLogic);
        fissionLogic.setDialogLogic(dialogLogic);
        FusionLogic fusionLogic = new FusionLogic(fusion, heartData, prosodyData, fusionData, persistenceLogic);
        HeartBeatLogic heartBeatLogic = new HeartBeatLogic(this, getApplicationContext(), fusion, fusionLogic);


        // Initialize the activity
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get the graphical objects
        tbxHeartRate = binding.tbxHeartRate;
        imgView = binding.imgBackground;
        imgMic = binding.imgMic;
        ImageView imgSphinx = binding.imgSphinx;
        myQueryView = binding.displayLog;

        // Start extract voice of the user
        prosodyLogic.extractFeatures();

        // ========================================================================================
        // LISTENER OF GRAPHICAL OBJECTS
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
                // Draw the number on the screen for relaxation method or change background
                int relaxation_state = fission.getRelaxationState();
                if(relaxation_state < 13 && relaxation_state > 0)
                {
                    // call the logic for relaxation counting method (even number)
                    fissionLogic.relaxationMethod(5);
                }
                else
                {
                    // Change background
                    picState = !picState;
                }
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

                if (mode)  // change the mode (if the watch is speaking the result loud or not)
                {
                    Log.d(TAG, "START RECORDING___");
                    fission.setAudion_on(false);
                    drawable = getDrawable(R.drawable.close_mic);
                    imgMic.setImageDrawable(drawable);
                    mode = false;
                }
                else
                {
                    Log.d(TAG, "STOP RECORDING___");
                    fission.setAudion_on(true);
                    fissionLogic.speak_result("Fission Audion On ! Welcome to anger detection !");
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
                dialogLogic.sphinxCall();  // register the voice of the user
            }
        });

        int refreshThreshold = 500;  // Handle refresh threshold of data
        Handler handler = new Handler();

        Runnable runnable = new Runnable() // change the display from fission and fusion data
        {
            @Override
            public void run()
            {
                fissionLogic.relaxationMethod(4); // handle all user modularity touch in relaxation method (odd number)

                updateDisplay();
                updateInteraction();
                handler.postDelayed(this, refreshThreshold); // Call this runnable again after 2 seconds
            }
        };
        handler.postDelayed(runnable, refreshThreshold); // Call this runnable after 2 seconds
    }

    /**
     * Change the interaction result with the user
     *
     * @autor Quentin Nater
     */
    private void updateInteraction()
    {
        if(!dialogData.getUserQueryResult().isEmpty())
        {
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            myQueryView.setLayoutManager(layoutManager);

            ViewHolderAdapter adapter = new ViewHolderAdapter(dialogData.getUserRefinedQueryResult());
            myQueryView.setAdapter(adapter);
        }

    }

    /**
     * Change the display based on the receive data
     *
     * @autor Quentin Nater
     */
    public void updateDisplay()
    {
        Drawable drawable = null;

        if (fission.getRelaxationState() < 13) {
            switch (fission.getRelaxationState()) {
                case 1:
                    drawable = getDrawable(R.drawable.one);
                    break;
                case 2:
                    drawable = getDrawable(R.drawable.two);
                    break;
                case 3:
                    drawable = getDrawable(R.drawable.three);
                    break;
                case 4:
                    drawable = getDrawable(R.drawable.four);
                    break;
                case 5:
                    drawable = getDrawable(R.drawable.five);
                    break;
                case 6:
                    drawable = getDrawable(R.drawable.six);
                    break;
                case 7:
                    drawable = getDrawable(R.drawable.seven);
                    break;
                case 8:
                    drawable = getDrawable(R.drawable.eight);
                    break;
                case 9:
                    drawable = getDrawable(R.drawable.nine);
                    break;
                case 10:
                    drawable = getDrawable(R.drawable.ten);
                    break;
                case 11:
                case 12:
                    drawable = getDrawable(R.drawable.eleven);
                    break;
                default:
                    break;
            }

            imgView.setImageDrawable(drawable);
            tbxHeartRate.setTextColor(Color.BLACK);
            tbxHeartRate.setText("Count");
        }
        else if(fusionData.getLevel().equals("calm")) // Ereshkigal
        {
            if(picState)
                drawable = getDrawable(R.drawable.z_relax);
            else
                drawable = getDrawable(R.drawable.offifical_calmness);

            imgView.setImageDrawable(drawable);
            tbxHeartRate.setTextColor(Color.BLACK);

            if(state == 1)
                tbxHeartRate.setText((int)fusion.getPitch() + "Hz");
            else if(state == 2)
                tbxHeartRate.setText((int)fusion.getAmplitude() + "amp");
            else if(state == 3)
                tbxHeartRate.setText((int)fusionData.getAuc() + "%");
        }
        else if (fusionData.getLevel().equals("anger"))
        {
            if(picState)
                drawable = getDrawable(R.drawable.z_anger);
            else
                drawable = getDrawable(R.drawable.offifical_anger);

            imgView.setImageDrawable(drawable);

            tbxHeartRate.setTextColor(Color.BLACK);

            if(state == 1)
                tbxHeartRate.setText((int)fusion.getPitch() + "Hz");
            else if(state == 2)
                tbxHeartRate.setText((int)fusion.getAmplitude() + "amp");
            else if(state == 3)
                tbxHeartRate.setText((int)fusionData.getAuc() + "%");


            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            long[] vibrationPattern = {0, 500, 50, 300};
            vibrator.vibrate(vibrationPattern, -1);
        }
        else if (fusionData.getLevel().equals("stress"))
        {
            if(picState)
                drawable = getDrawable(R.drawable.z_stress);
            else
                drawable = getDrawable(R.drawable.offifical_stress);

            imgView.setImageDrawable(drawable);

            tbxHeartRate.setTextColor(Color.WHITE);

            if(state == 1)
                tbxHeartRate.setText((int)fusion.getPitch() + "Hz");
            else if(state == 2)
                tbxHeartRate.setText((int)fusion.getAmplitude() + "amp");
            else if(state == 3)
                tbxHeartRate.setText((int)fusionData.getAuc() + "%");
        }

        if(state == 0)
            tbxHeartRate.setText((int)fusion.getHeartBeat() + "bpm");
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
