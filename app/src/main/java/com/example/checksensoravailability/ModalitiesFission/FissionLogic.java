package com.example.checksensoravailability.ModalitiesFission;

import android.content.Context;
import android.media.MediaPlayer;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;
import com.example.checksensoravailability.DialogManagement.DialogLogic;
import com.example.checksensoravailability.ModalitiesFusion.Fusion;
import com.example.checksensoravailability.R;

import java.util.Locale;

/**
 * Anger Detection
 * This project is directed by the University of Fribourg in the context of the course FS2023: 03035/33035 Multimodal User Interfaces
 * Matilde De Luigi / Quentin Nater
 */
public class FissionLogic implements TextToSpeech.OnInitListener
{
    private Context applicationContext;
    private MediaPlayer mediaPlayer;
    private TextToSpeech tts;
    private Fission fission;
    private Boolean hasSpoken = false;

    private final int[] temporality = {0};
    private DialogLogic dialogLogic;
    private Fusion fusion;

    public FissionLogic(Context applicationContext, Fission fission, Fusion fusion, DialogLogic dialogLogic)
    {
        this.applicationContext = applicationContext;

        // prepare the music of the watch to relax the user.
        mediaPlayer = MediaPlayer.create(applicationContext, R.raw.dcl);

        // prepare the text speech of the watch to speak to the user.
        tts = new TextToSpeech(applicationContext, this);

        this.fission = fission;
        this.fusion = fusion;
        this.dialogLogic = dialogLogic;
    }

    /**
     * Set more information about the dialog
     * @param dialogLogic - DialogLogic set more information about the dialog
     *
     * @autor Quentin Nater
     */
    public void setDialogLogic(DialogLogic dialogLogic)
    {
        this.dialogLogic = dialogLogic;
    }


    /**
     * On initialization of the fission, preparation of speech and language analysis
     * @param status - int result of the speech initialization
     *
     * @autor Quentin Nater
     */
    @Override
    public void onInit(int status)
    {
        System.out.println("Fission initialization");

        if (status == TextToSpeech.SUCCESS)
        {
            int result = tts.setLanguage(Locale.getDefault());  // set the english language

            // display information in case of problem
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED)
            {
                Toast.makeText(applicationContext, "Language not supported", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Dictate a sentence by the watch
     * @param text - String text to make the watch speak
     *
     * @autor Quentin Nater
     */
    public void speak_result(String text)
    {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    /**
     * run on the destruction of the class to stop the text to speak.
     *
     * @autor Quentin Nater
     */
    protected void onDestroy()
    {
        if (tts != null) {
            // Release the Text-To-Speech engine resources
            tts.stop();
            tts.shutdown();
        }
    }

    /**
     * Function that run a relaxation method chosen by the user
     *
     * @param played int - Which type of music or relaxation method will be played
     * @autor Quentin Nater
     */
    public void relaxationMethod(int played)
    {
        switch (played)  // play the chosen music
        {
            case 1:  // play music relax
                if (mediaPlayer.isPlaying())
                    mediaPlayer.stop();
                mediaPlayer = MediaPlayer.create(applicationContext, R.raw.relax);
                mediaPlayer.start();
                break;
            case 2:  // play music victory
                if (mediaPlayer.isPlaying())
                    mediaPlayer.stop();
                mediaPlayer = MediaPlayer.create(applicationContext, R.raw.op);
                mediaPlayer.start();
                break;
            case 3:  // play victory music
                if (mediaPlayer.isPlaying())
                    mediaPlayer.stop();
                mediaPlayer = MediaPlayer.create(applicationContext, R.raw.bip);
                mediaPlayer.start();
                break;
            case 4: // listen if the user is speaking the number aloud
                int relaxation_state = fission.getRelaxationState();
                System.out.println("fission.getRelaxationState() :" + relaxation_state);
                System.out.println("temporality[0] :" + temporality[0]);

                // if the state has been reach, wait 2 seconds for displaying the number (odd number)
                if(relaxation_state < 13 && relaxation_state > 0 && relaxation_state % 2 != 0)
                {
                    temporality[0] = temporality[0] + 1;

                    if(temporality[0] % 4 == 0)  // each 2 seconds
                    {
                        fission.setRelaxationState(relaxation_state - 1); // display other number
                    }
                }
                else if (relaxation_state < 13 && relaxation_state > 0 && relaxation_state % 2 == 0)
                {
                    // if the number has been spoken by the user (even number)
                    if(fission.getPitch() > 0)
                        hasSpoken = true;
                }
                else if (relaxation_state == 0)
                {
                    fission.setRelaxationState(15); // handling the end of the relaxation analysis
                    handleRelaxationResult();
                }
                else if (relaxation_state == 14 && temporality[0] > 40)
                {
                    temporality[0] = 0; // set ready for the next one
                    fission.setRelaxationState(13);  // set ready for the next one
                    dialogLogic.sphinxCall(); // call sphinx to handle the user next wish
                }
                else if (relaxation_state == 14)
                {
                    temporality[0] = temporality[0] + 1;  // wait time for sphinx before the end of the speech information
                }
                break;
            case 5:  // check if the user is speaking the number and press the screen right
                // only with even number
                int relaxationstate = fission.getRelaxationState();

                if(relaxationstate % 2 == 0 && (fusion.getPitch() > 0 || hasSpoken))
                {
                    hasSpoken = false;
                    fission.setRelaxationState(relaxationstate-1);
                }
                break;
            default:
                if (mediaPlayer.isPlaying())
                    mediaPlayer.stop();
                break;
        }
    }


    /**
     * Handle the end of the relaxation result with speak and toast (value 15 == handling)
     *
     * @autor Quentin Nater
     */
    public void handleRelaxationResult()
    {
        if(fission.getRelaxationState() == 15)
        {
            Toast.makeText(applicationContext.getApplicationContext(), "Result " + fission.getHeartBeat() + "bmps... 'Continue',  'Terminate' or 'Music' ?", Toast.LENGTH_LONG).show();

            speak_result("Your result is" + fission.getHeartBeat() +" ... Do you want to 'continue' or 'terminate' the session ? Do you want to listen 'relaxation music' ?");

            fission.setRelaxationState(14);  // handle pause time for sphinx answer
        }
    }
}
