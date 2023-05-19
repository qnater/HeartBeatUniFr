package com.example.angerdetection.InputProsody;

import com.example.angerdetection.ModalitiesFusion.Fusion;
import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;
import be.tarsos.dsp.util.fft.FFT;

/**
 * Anger Detection
 * This project is directed by the University of Fribourg in the context of the course FS2023: 03035/33035 Multimodal User Interfaces
 * Matilde De Luigi / Quentin Nater
 */
public class ProsodyLogic
{
    private Fusion fusion;

    public ProsodyLogic(Fusion fusion)
    {
        this.fusion = fusion;
    }

    /**
     * Function that extract pitch, noise and amplitude from user voice
     *
     * @autor Quentin Nater
     */
    public void extractFeatures()
    {
        // set the base of the audio dispatcher for the prosody
        AudioDispatcher dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050,1024,0);

        PitchDetectionHandler pdh = new PitchDetectionHandler()
        {
            @Override
            public void handlePitch(PitchDetectionResult res, AudioEvent e)
            {
                final float pitchInHz = res.getPitch();         // get the pitch (Hz) from the result
                final float pitchNoise = res.getProbability();  // get the noise (%) from the result
                final float[] amp = {0};                        // get the first information about brut amplitude
                final float[] amplitudes = new float[e.getBufferSize()]; // get information to mine amplitude


                float[] audioFloatBuffer = e.getFloatBuffer(); // set the buffer of the sound
                float[] transformBuffer = new float[e.getBufferSize() * 2];
                FFT fft = new FFT(e.getBufferSize());  // set the object to extract the amplitude from the raw sound
                System.arraycopy(audioFloatBuffer, 0, transformBuffer, 0, audioFloatBuffer.length);
                fft.forwardTransform(transformBuffer);  // transform the raw sound
                fft.modulus(transformBuffer, amplitudes); // set the modulus of the  sound

                for (int index = 0; index < amplitudes.length; index++)
                {
                    if (amplitudes[index] > amp[0]) // get the amplitude
                    {
                        amp[0] = amplitudes[index]; // set the result
                    }
                }

                int pitch = (int) pitchInHz; // cast the pitch
                int amplitude = (int) amp[0];  // cast the amplitude
                float noise = ((1-pitchNoise)*100); // get the noise %

                fusion.setProsodyModality(pitch, amplitude, noise); // send the value for the fusion with heart beat
            }
        };

        // set the processor for the pitch
        AudioProcessor pitchProcessor = new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, 22050, 1024, pdh);
        dispatcher.addAudioProcessor(pitchProcessor);

        // launch the thread in the for prosody modality.
        Thread audioThread = new Thread(dispatcher, "Audio Thread");
        audioThread.start();
    }
}
