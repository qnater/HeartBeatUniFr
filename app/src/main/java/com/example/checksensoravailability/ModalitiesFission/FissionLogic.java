package com.example.checksensoravailability.ModalitiesFission;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.media.MediaPlayer;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import com.example.checksensoravailability.ContextUserModelHistory.PersistenceLogic;
import com.example.checksensoravailability.R;

import java.util.Locale;

public class FissionLogic implements TextToSpeech.OnInitListener
{
    private Context applicationContext;
    private MediaPlayer mediaPlayer;
    private TextToSpeech tts;

    public FissionLogic(Context applicationContext)
    {
        this.applicationContext = applicationContext;

        // prepare the music of the watch to relax the user.
        mediaPlayer = MediaPlayer.create(applicationContext, R.raw.dcl);

        // prepare the text speech of the watch to speak to the user.
        tts = new TextToSpeech(applicationContext, this);
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
            case 1:
                if (mediaPlayer.isPlaying())
                    mediaPlayer.stop();
                mediaPlayer = MediaPlayer.create(applicationContext, R.raw.relax);
                mediaPlayer.start();
                break;
            case 2:
                if (mediaPlayer.isPlaying())
                    mediaPlayer.stop();
                mediaPlayer = MediaPlayer.create(applicationContext, R.raw.op);
                mediaPlayer.start();
                break;
            case 3:
                if (mediaPlayer.isPlaying())
                    mediaPlayer.stop();
                mediaPlayer = MediaPlayer.create(applicationContext, R.raw.bip);
                mediaPlayer.start();
                break;
            default:
                if (mediaPlayer.isPlaying())
                    mediaPlayer.stop();
                break;
        }
    }
}
