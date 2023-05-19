package com.example.angerdetection.ModalitiesFusion;

import com.example.angerdetection.ContextUserModelHistory.PersistenceLogic;
import com.example.angerdetection.InputHeartBeat.HeartBeatData;
import com.example.angerdetection.InputProsody.ProsodyData;

/**
 * Anger Detection
 * This project is directed by the University of Fribourg in the context of the course FS2023: 03035/33035 Multimodal User Interfaces
 * Matilde De Luigi / Quentin Nater
 */
public class FusionLogic
{
    private HeartBeatData heartData;
    private ProsodyData prosodyData;
    private FusionData fusionData;
    private PersistenceLogic persistenceLogic;
    private Fusion fusion;

    public FusionLogic(Fusion fusion, HeartBeatData heartData, ProsodyData prosodyData, FusionData fusionData, PersistenceLogic persistenceLogic)
    {
        this.fusion = fusion;
        this.heartData = heartData;
        this.prosodyData = prosodyData;
        this.fusionData = fusionData;
        this.persistenceLogic = persistenceLogic;
    }

    /**
     * Function that merges the recollect of data from the fusion listener and get the % of prediction
     *
     * @param heatBeat int - Value of the heartbeat
     * @param pitch float - Value of prosody pitch
     * @param amplitude float - Value of prosody amplitude
     * @param level String - prediction of the logic
     * @return result int - Value (%) of the prediction
     * @autor Quentin Nater
     */
    public int fusion_result(int heatBeat, float pitch, float amplitude, String level)
    {
        int result = 0;

        if(level.equals("calm")) // if the level detected is calm
        {
            // ALL VALUES
            if (heatBeat < heartData.getCalmnessHeartBeat() && pitch < prosodyData.getCalmnessPitch() && amplitude < prosodyData.getCalmnessAmplitude())
                result = 100;
        }
        else if (level.equals("stress")) // if the level detected is stress
        {
            // ALL VALUES
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
        else if (level.equals("anger"))  // if the level detected is anger
        {
            // ALL VALUES
            if (heatBeat >= heartData.getAngerHeartBeat() && pitch >= prosodyData.getAngerPitch() && amplitude >= prosodyData.getAngerAmplitude())
                result = 100;
        }

        System.out.println("Result : " +  result);

        return  result;
    }


    /**
     * Function that collects the data from the inputs and set their logic to their data object
     * Also calculate the percentage of prediction correctness and write the fusion in a dataset
     *
     * @autor Quentin Nater
     */
    public void sensorLogicProcessing()
    {
        // if the condition is calm
        if (fusion.getHeartBeat() < heartData.getCalmnessHeartBeat() && fusion.getPitch() < prosodyData.getCalmnessPitch()  && fusion.getAmplitude() < prosodyData.getCalmnessAmplitude())
        {
            fusionData.setLevel("calm");  // set the level to calm

            // calculate the percentage result that calm is the right level
            float fusionProcess = fusion_result((int)fusion.getHeartBeat(),
                    fusion.getPitch(),
                    fusion.getAmplitude(),
                    fusionData.getLevel());

            // set the result
            fusionData.setAuc(fusionProcess);
        }
        else if (fusion.getHeartBeat() >= heartData.getAngerHeartBeat() && fusion.getPitch() >= prosodyData.getAngerPitch() && fusion.getAmplitude() >= prosodyData.getAngerAmplitude())
        {
            fusionData.setLevel("anger");  // set the level to anger

            // calculate the percentage result that anger is the right level
            float fusionProcess = fusion_result((int)fusion.getHeartBeat(),
                    fusion.getPitch(),
                    fusion.getAmplitude(),
                    fusionData.getLevel());
            fusionData.setAuc(fusionProcess);
        }
        else
        {
            fusionData.setLevel("stress"); // set the level to stress

            // calculate the percentage result that stress is the right level
            float fusionProcess = fusion_result((int)fusion.getHeartBeat(),
                    fusion.getPitch(),
                    fusion.getAmplitude(),
                    fusionData.getLevel());
            fusionData.setAuc(fusionProcess);

        }

        System.out.println("Sensors Features - Heart Beats : \t" + fusion.getHeartBeat() + " bpm");
        System.out.println("Prosody Features - Pitch in Hz : \t" + fusion.getPitch() + " Hz");
        System.out.println("Prosody Features - Amplitudes  : \t" + fusion.getAmplitude() + " dBm");
        System.out.println("Prosody Features - Noise       : \t" + fusion.getNoise() + "%");
        System.out.println("Multimodal value - LEVEL       : \t" + fusionData.getLevel());
        System.out.println("Multimodal value - AUC         : \t" + fusionData.getAuc() + "%");

        // write the result in the database
        persistenceLogic.writeDataset((int)fusion.getHeartBeat(),
                fusion.getPitch(),
                fusion.getAmplitude(),
                (int)fusionData.getAuc(),
                fusion.getNoise(),
                fusionData.getLevel());

    }
}
