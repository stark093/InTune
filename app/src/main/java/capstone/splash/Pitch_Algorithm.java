package capstone.splash;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.widget.TextView;

import static java.lang.Thread.sleep;

/**
 * Created by Nick on 2017-01-27.
 */


public class Pitch_Algorithm {
    private Handler pitchAlgorithmMainHandler;
    private Audio_Record_Implementation audioRecorder;
    private double[] audioData;

    private double updatedFrequency = 0;
    private int updateSequenceNumber = 1;
    private double desiredFrequency;


    boolean run;
    public Pitch_Algorithm(double desiredFrequencyIn){
        pitchAlgorithmMainHandler = new Handler();
        audioRecorder = new Audio_Record_Implementation();
        audioRecorder.startRecording((int)(Math.pow(2,15+1)));
        audioData = new double[audioRecorder.getAudioBufferSize()];
        desiredFrequency = desiredFrequencyIn;


        try {
            sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        run = true;
        System.out.println("starting algorithm...");
        pitchAlgorithmRunningLoop.run();
    }

    public synchronized double[] getFreq(){
        double[] retArr = {updatedFrequency, updateSequenceNumber};
        return retArr;
    }

    private synchronized void updateValues(double freqVal){
        updatedFrequency = freqVal;
        updateSequenceNumber++;
    }

    public void kill(){
        audioRecorder.stopRecording();
        run = false;
    }

    private Runnable pitchAlgorithmRunningLoop = new Runnable(){
        @Override
        public void run(){
            int delay = 50;
            double freq[];
            try {
                audioData = audioRecorder.getAudioData();
                if(audioData!=null) {
                    freq = Signal_Processing.getMaxFreqMagnitude(audioData, desiredFrequency);

                }else{
                    freq = new double[]{0,0};
                }
                if(freq[1]>1.5){
                    updateValues(freq[0]);
                    System.out.println(freq[0]);
                }
//                graphingCanvas.updateGraph(signal_processor.getPlot(data,"cepstral"), false, false);
                //endTime = System.currentTimeMillis();
                //System.out.println((endTime-startTime) + ", " + num + ", " + largestFreq);



            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                if(run) {
                    pitchAlgorithmMainHandler.postDelayed(pitchAlgorithmRunningLoop, delay);
                }
            }
        }



    };
}
