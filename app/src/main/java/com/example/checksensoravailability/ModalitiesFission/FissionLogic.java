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

        mediaPlayer = MediaPlayer.create(applicationContext, R.raw.dcl);

        tts = new TextToSpeech(applicationContext, this);

    }


    @Override
    public void onInit(int status)
    {
        System.out.println("Fission initialization");

        if (status == TextToSpeech.SUCCESS)
        {
            int result = tts.setLanguage(Locale.getDefault());

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED)
            {
                Toast.makeText(applicationContext, "Language not supported", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void speak_result(String text)
    {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    protected void onDestroy()
    {

        if (tts != null) {
            // Release the Text-To-Speech engine resources
            tts.stop();
            tts.shutdown();
        }
    }

    /**
     * Function that called pre-defined relaxation method of psychology
     *
     * @param played int - Which type of music or relaxation method will be played
     * @autor Quentin Nater
     */
    public void relaxationMethod(int played)
    {
        switch (played)
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
