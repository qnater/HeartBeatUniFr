package com.example.angerdetection.InputProsody;

/**
 * Anger Detection
 * This project is directed by the University of Fribourg in the context of the course FS2023: 03035/33035 Multimodal User Interfaces
 * Matilde De Luigi / Quentin Nater
 */
public class ProsodyData
{
    // FIXED DATA ================================================================================
    private int calmnessPitch = 132;
    private int angerPitch = 200;

    private int calmnessAmplitude = 40;
    private int angerAmplitude = 50;
    // ============================================================================================


    public int getCalmnessPitch()
    {
        return calmnessPitch;
    }

    public int getAngerPitch()
    {
        return angerPitch;
    }

    public int getCalmnessAmplitude()
    {
        return calmnessAmplitude;
    }

    public int getAngerAmplitude()
    {
        return angerAmplitude;
    }






}
