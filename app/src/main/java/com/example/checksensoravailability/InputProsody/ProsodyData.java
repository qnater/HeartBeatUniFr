package com.example.checksensoravailability.InputProsody;

public class ProsodyData
{
    // FIXED DATA
    private int calmnessPitch = 132;
    private int angerPitch = 200;

    private int calmnessAmplitude = 40;
    private int angerAmplitude = 50;

    // ===================================================================================== //
    // SHARED DATA
    private float pitch = 0;
    private float amplitude = 0;

    private float noise = 0;



    public int getCalmnessPitch()
    {
        return calmnessPitch;
    }

    public void setCalmnessPitch(int calmnessPitch)
    {
        this.calmnessPitch = calmnessPitch;
    }

    public int getAngerPitch()
    {
        return angerPitch;
    }

    public void setAngerPitch(int angerPitch)
    {
        this.angerPitch = angerPitch;
    }

    public int getCalmnessAmplitude()
    {
        return calmnessAmplitude;
    }

    public void setCalmnessAmplitude(int calmnessAmplitude)
    {
        this.calmnessAmplitude = calmnessAmplitude;
    }

    public int getAngerAmplitude()
    {
        return angerAmplitude;
    }

    public void setAngerAmplitude(int angerAmplitude)
    {
        this.angerAmplitude = angerAmplitude;
    }


    public float getPitch()
    {
        return pitch;
    }

    public void setPitch(float pitch)
    {
        this.pitch = pitch;
    }

    public float getAmplitude()
    {
        return amplitude;
    }

    public void setAmplitude(float amplitude)
    {
        this.amplitude = amplitude;
    }

    public float getNoise()
    {
        return noise;
    }

    public void setNoise(float noise)
    {
        this.noise = noise;
    }




}
