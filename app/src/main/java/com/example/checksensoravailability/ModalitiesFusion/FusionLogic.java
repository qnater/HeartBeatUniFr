package com.example.checksensoravailability.ModalitiesFusion;

import static android.content.Context.VIBRATOR_SERVICE;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Vibrator;

import com.example.checksensoravailability.ContextUserModelHistory.PersistenceLogic;
import com.example.checksensoravailability.InputHeartBeat.HeartBeatData;
import com.example.checksensoravailability.InputProsody.ProsodyData;
import com.example.checksensoravailability.R;

public class FusionLogic
{
    private HeartBeatData heartData;
    private ProsodyData prosodyData;
    private FusionData fusionData;
    private PersistenceLogic persistenceLogic;
    private Activity mainActivity;

    public FusionLogic(Activity mainActivity, HeartBeatData heartData, ProsodyData prosodyData, FusionData fusionData, PersistenceLogic persistenceLogic)
    {
        this.mainActivity = mainActivity;
        this.heartData = heartData;
        this.prosodyData = prosodyData;
        this.fusionData = fusionData;
        this.persistenceLogic = persistenceLogic;
    }


    public int fusion_result(int heatBeat, float pitch, float amplitude, String level)
    {
        int result = 0;

        if(level.equals("calm"))
        {
            if (heatBeat < heartData.getCalmnessHeartBeat() && pitch < prosodyData.getCalmnessPitch() && amplitude < prosodyData.getCalmnessAmplitude())
                result = 100;
        }
        else if (level.equals("stress"))
        {
            // ALL
            if ((heatBeat >= heartData.getCalmnessHeartBeat() && heatBeat < heartData.getAngerHeartBeat()) && (pitch >= prosodyData.getCalmnessPitch() && pitch <  prosodyData.getAngerPitch()) && (amplitude >= prosodyData.getCalmnessAmplitude() && amplitude < prosodyData.getAngerAmplitude()))
                result = 100;

                // NO HEART BEAT
            else if ((heatBeat < heartData.getCalmnessHeartBeat() || heatBeat > heartData.getAngerHeartBeat()) && (pitch >= prosodyData.getCalmnessPitch() && pitch <  prosodyData.getAngerPitch()) && (amplitude >= prosodyData.getCalmnessAmplitude() && amplitude < prosodyData.getAngerAmplitude()))
                result = 83;

                // NO PITCH
            else if ((heatBeat >= heartData.getCalmnessHeartBeat() && heatBeat < heartData.getAngerHeartBeat()) && (pitch < prosodyData.getCalmnessPitch() || pitch >  prosodyData.getAngerPitch()) && (amplitude >= prosodyData.getCalmnessAmplitude() && amplitude < prosodyData.getAngerAmplitude()))
                result = 83;

                // NO AMPLITUDE
            else if ((heatBeat >= heartData.getCalmnessHeartBeat() && heatBeat < heartData.getAngerHeartBeat()) && (pitch >= prosodyData.getCalmnessPitch() && pitch <  prosodyData.getAngerPitch()) && (amplitude < prosodyData.getCalmnessAmplitude() || amplitude > prosodyData.getAngerAmplitude()))
                result = 83;


                // NO HEART BEAT && NO PITCH
            else if ((heatBeat < heartData.getCalmnessHeartBeat() || heatBeat > heartData.getAngerHeartBeat()) && (pitch < prosodyData.getCalmnessPitch() || pitch >  prosodyData.getAngerPitch()) && (amplitude >= prosodyData.getCalmnessAmplitude() && amplitude < prosodyData.getAngerAmplitude()))
                result = 67;

                // NO HEART BEAT && NO AMPLITUDE
            else if ((heatBeat < heartData.getCalmnessHeartBeat() || heatBeat > heartData.getAngerHeartBeat()) && (pitch >= prosodyData.getCalmnessPitch() && pitch <  prosodyData.getAngerPitch()) && (amplitude < prosodyData.getCalmnessAmplitude() || amplitude > prosodyData.getAngerAmplitude()))
                result = 67;

                // NO PITCH && NO AMPLITUDE
            else if ((heatBeat >= heartData.getCalmnessHeartBeat() && heatBeat < heartData.getAngerHeartBeat()) && (pitch < prosodyData.getCalmnessPitch() || pitch >  prosodyData.getAngerPitch()) && (amplitude < prosodyData.getCalmnessAmplitude() || amplitude > prosodyData.getAngerAmplitude()))
                result = 67;

            else
                result = 50;
        }
        else if (level.equals("anger"))
        {
            if (heatBeat >= heartData.getAngerHeartBeat() && pitch >= prosodyData.getAngerPitch() && amplitude >= prosodyData.getAngerAmplitude())
                result = 100;
        }

        System.out.println("Result : " +  result);

        return  result;
    }


    public void sensorLogicProcessing()
    {

        if (heartData.getHeartbeat() < heartData.getCalmnessHeartBeat() && prosodyData.getPitch() < prosodyData.getCalmnessPitch()  && prosodyData.getAmplitude() < prosodyData.getCalmnessAmplitude())  // Ereshkigal
        {
            fusionData.setLevel("calm");

            float fusion = fusion_result((int)heartData.getHeartbeat(),
                    prosodyData.getPitch(),
                    prosodyData.getAmplitude(),
                    fusionData.getLevel());
            fusionData.setAuc(fusion);

        }
        else if (heartData.getHeartbeat() >= heartData.getAngerHeartBeat() && prosodyData.getPitch() >= prosodyData.getAngerPitch() && prosodyData.getAmplitude() >= prosodyData.getAngerAmplitude()) // Eriri
        {
            fusionData.setLevel("anger");

            float fusion = fusion_result((int)heartData.getHeartbeat(),
                    prosodyData.getPitch(),
                    prosodyData.getAmplitude(),
                    fusionData.getLevel());
            fusionData.setAuc(fusion);

        }
        else // Tohsaka
        {
            fusionData.setLevel("stress");

            float fusion = fusion_result((int)heartData.getHeartbeat(),
                    prosodyData.getPitch(),
                    prosodyData.getAmplitude(),
                    fusionData.getLevel());
            fusionData.setAuc(fusion);

        }

        System.out.println("Sensors Features - Heart Beats : \t" + heartData.getHeartbeat() + " bpm");
        System.out.println("Prosody Features - Pitch in Hz : \t" + prosodyData.getPitch() + " Hz");
        System.out.println("Prosody Features - Amplitudes  : \t" + prosodyData.getAmplitude() + " dBm");
        System.out.println("Prosody Features - Noise       : \t" + prosodyData.getNoise() + "%");
        System.out.println("Multimodal value - LEVEL       : \t" + fusionData.getLevel());
        System.out.println("Multimodal value - AUC         : \t" + fusionData.getAuc() + "%");

        persistenceLogic.writeDataset((int)heartData.getHeartbeat(),
                prosodyData.getPitch(),
                prosodyData.getAmplitude(),
                (int)fusionData.getAuc(),
                prosodyData.getNoise(),
                fusionData.getLevel());

    }

}
