package com.example.checksensoravailability.InputProsody;

import android.app.Activity;
import android.os.Environment;
import android.util.Log;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;
import be.tarsos.dsp.util.fft.FFT;

public class ProsodyLogic
{
    private ProsodyData prosodyData;
    private Activity mainActivity;
    public ProsodyLogic(Activity mainActivity, ProsodyData prosodyData)
    {
        this.mainActivity = mainActivity;
        this.prosodyData = prosodyData;
    }

    public void extractFeatures()
    {
        AudioDispatcher dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050,1024,0);
        PitchDetectionHandler pdh = new PitchDetectionHandler()
        {
            @Override
            public void handlePitch(PitchDetectionResult res, AudioEvent e)
            {
                final float pitchInHz = res.getPitch();
                final float pitchNoise = res.getProbability();
                final float[] amp = {0};
                final float[] amplitudes = new float[e.getBufferSize()];


                float[] audioFloatBuffer = e.getFloatBuffer();
                float[] transformBuffer = new float[e.getBufferSize() * 2];
                FFT fft = new FFT(e.getBufferSize());
                System.arraycopy(audioFloatBuffer, 0, transformBuffer, 0, audioFloatBuffer.length);
                fft.forwardTransform(transformBuffer);
                fft.modulus(transformBuffer, amplitudes);

                for (int index = 0; index < amplitudes.length; index++)
                {
                    if (amplitudes[index] > amp[0])
                    {
                        amp[0] = amplitudes[index];
                    }
                }


                prosodyData.setPitch((int) pitchInHz);
                prosodyData.setAmplitude((int) amp[0]);
                prosodyData.setNoise(((1-pitchNoise)*100));

                /*
                mainActivity.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        prosodyData.setPitch((int) pitchInHz);
                        prosodyData.setAmplitude((int) amp[0]);
                        prosodyData.setNoise(((1-pitchNoise)*100));
                    }
                });
                */

            }
        };
        AudioProcessor pitchProcessor = new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, 22050, 1024, pdh);
        dispatcher.addAudioProcessor(pitchProcessor);

        Thread audioThread = new Thread(dispatcher, "Audio Thread");
        audioThread.start();
    }
}
