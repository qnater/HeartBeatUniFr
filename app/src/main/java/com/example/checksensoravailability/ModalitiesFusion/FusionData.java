package com.example.checksensoravailability.ModalitiesFusion;

/**
 * Anger Detection
 * This project is directed by the University of Fribourg in the context of the course FS2023: 03035/33035 Multimodal User Interfaces
 * Matilde De Luigi / Quentin Nater
 */
public class FusionData
{
    private String level = "";
    private float auc = 0;

    public String getLevel()
    {
        return level;
    }

    public void setLevel(String level)
    {
        this.level = level;
    }

    public float getAuc()
    {
        return auc;
    }

    public void setAuc(float auc)
    {
        this.auc = auc;
    }

}
