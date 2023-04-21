package com.example.checksensoravailability.ModalitiesFission;

import android.content.Context;
import android.content.ContextWrapper;
import android.media.MediaPlayer;
import com.example.checksensoravailability.R;

public class FissionLogic
{

    private Context applicationContext;
    private MediaPlayer mediaPlayer;


    public FissionLogic(Context applicationContext)
    {
        this.applicationContext = applicationContext;
        mediaPlayer = MediaPlayer.create(applicationContext, R.raw.dcl);
    }





    public void relaxationMethod(int played)
    {
        switch (played)
        {
            case 1:
                if (mediaPlayer.isPlaying())
                    mediaPlayer.stop();
                mediaPlayer = MediaPlayer.create(applicationContext, R.raw.dcl);
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
