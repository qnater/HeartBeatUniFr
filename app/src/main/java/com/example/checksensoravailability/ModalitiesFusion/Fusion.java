package com.example.checksensoravailability.ModalitiesFusion;

import com.example.checksensoravailability.ModalitiesFission.Fission;

public class Fusion
{
    private int heartBeat = 0;
    private float pitch = 0;
    private float amplitude = 0;
    private float noise = 0;
    private Fission fission;

    public Fusion(Fission fission)
    {
        this.fission = fission;
    }

    public Fusion(int heartBeat, float pitch, float amplitude, float noise)
    {
        this.heartBeat = heartBeat;
        this.pitch = pitch;
        this.amplitude = amplitude;
        this.noise = noise;
    }

    public void setFusion(int heartBeat, float pitch, float amplitude, float noise)
    {
        setHeartBeat(heartBeat);
        setPitch(pitch);
        setAmplitude(amplitude);
        setNoise(noise);
    }

    public int getHeartBeat()
    {
        return heartBeat;
    }

    public void setHeartBeat(int heartBeat) {
        this.heartBeat = heartBeat;
        fission.setHeartBeat(heartBeat);
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
        fission.setPitch(pitch);
    }

    public float getAmplitude() {
        return amplitude;
    }

    public void setAmplitude(float amplitude) {
        this.amplitude = amplitude;
        fission.setAmplitude(amplitude);
    }


    public float getNoise() {
        return noise;
    }

    public void setNoise(float noise) {
        this.noise = noise;
        fission.setNoise(noise);
    }

    public void setHeartModality(float heartbeat)
    {
        setHeartBeat((int) heartbeat);
        fission.setHeartBeat((int) heartbeat);
    }


    public void setProsodyModality(float pitch, float amplitude, float noise)
    {
        setPitch(pitch);
        setAmplitude(amplitude);
        setNoise(noise);
    }
}
