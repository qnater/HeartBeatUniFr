package com.example.checksensoravailability.InputHeartBeat;


public class HeartBeatData
{
    // FIXED DATA
    private int calmnessHeartBeat = 80;
    private int angerHeartBeat = 100;



    public HeartBeatData()
    {
        System.out.println(">> You have called the threshold information variables");
    }

    public int getCalmnessHeartBeat()
    {
        return calmnessHeartBeat;
    }

    public void setCalmnessHeartBeat(int calmnessHeartBeat)
    {
        this.calmnessHeartBeat = calmnessHeartBeat;
    }

    public int getAngerHeartBeat()
    {
        return angerHeartBeat;
    }

    public void setAngerHeartBeat(int angerHeartBeat)
    {
        this.angerHeartBeat = angerHeartBeat;
    }


}
