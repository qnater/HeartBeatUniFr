package com.example.checksensoravailability.ModalitiesFission;

import com.example.checksensoravailability.ModalitiesFusion.Fusion;

public class Fission
{
    private int heartBeat = 0;
    private float pitch = 0;
    private float amplitude = 0;
    private float noise = 0;
    private Boolean audion_on = false;
    private int relaxationState = 13;

    public Fission()
    {
        setAudion_on(false);
    }

    public void setFusion(Fusion fusion)
    {
        setHeartBeat(fusion.getHeartBeat());
        setNoise(fusion.getNoise());
        setAmplitude(fusion.getAmplitude());
        setPitch(fusion.getPitch());
        setAudion_on(false);
    }

    public int getHeartBeat() {
        return heartBeat;
    }

    public void setHeartBeat(int heartBeat) {
        this.heartBeat = heartBeat;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public float getAmplitude() {
        return amplitude;
    }

    public void setAmplitude(float amplitude) {
        this.amplitude = amplitude;
    }

    public float getNoise() {
        return noise;
    }

    public void setNoise(float noise) {
        this.noise = noise;
    }

    public void setAudion_on(Boolean audion_on)
    {
        this.audion_on = audion_on;
    }

    public Boolean getAudion_on() {
        return audion_on;
    }


    public int getRelaxationState() {
        return relaxationState;
    }

    public void setRelaxationState(int relaxationState)
    {
        this.relaxationState = relaxationState;
    }


}
