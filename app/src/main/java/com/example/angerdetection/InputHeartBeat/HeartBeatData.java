package com.example.angerdetection.InputHeartBeat;


/**
 * Anger Detection
 * This project is directed by the University of Fribourg in the context of the course FS2023: 03035/33035 Multimodal User Interfaces
 * Matilde De Luigi / Quentin Nater
 */
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
    public int getAngerHeartBeat()
    {
        return angerHeartBeat;
    }


}
