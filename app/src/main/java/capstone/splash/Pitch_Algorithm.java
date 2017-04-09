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
    private int highLim;
    private int lowLim;
    private int currentString;

    private double[] currentWindowFrequencies = new double[2048];
    private int zeroCount = 0;
    private int currentWindowIndex = 0;
    private boolean listening=false;

    /* TWEAK THESE VARIABLES*/
    private int numToIgnore=4;
    private int zerosToWaitFor = 4;

    private double updatedFrequency = 0;
    private int updateSequenceNumber = 1;

    boolean run;
    public Pitch_Algorithm(int string){
        pitchAlgorithmMainHandler = new Handler();
        audioRecorder = new Audio_Record_Implementation();
        audioRecorder.startRecording(2*4096);
        audioData = new double[audioRecorder.getAudioBufferSize()];
        setString(string);


        try {
            sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        run = true;
        System.out.println("starting algorithm...");
        pitchAlgorithmRunningLoop.run();
    }
    public void setString(int string){
        currentString = string;
        switch (string){
            case 6://fattest
                lowLim = 60;
                highLim = 100;
                break;
            case 5:
                lowLim = 90;
                highLim = 130;
                break;
            case 4:
                lowLim = 126;
                highLim = 166;
                break;
            case 3:
                lowLim = 176;
                highLim = 216;
                break;
            case 2:
                lowLim = 227;
                highLim = 267;
                break;
            case 1://thinnest
                lowLim = 310;
                highLim=350;
                break;
        }
    }

    private double mean(double[] frequencies){
        double sum=0;
        int nonZeroElements = 0;
        for(int i=numToIgnore;i<frequencies.length;i++){
            sum+=frequencies[i];
            if(frequencies[i]>0){
                nonZeroElements++;
            }
        }
        if(nonZeroElements == 0){
            return 0;
        }else {
            return sum / nonZeroElements;
        }
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

            try {
                audioData = audioRecorder.getAudioData();
                double freq = Signal_Processing.getFrequency(audioData,currentString);

                if(freq<highLim && freq>lowLim) {
                    currentWindowFrequencies[currentWindowIndex++] = freq;
                    zeroCount = 0;
                    listening=true;
                }else if(listening){
                    zeroCount++;
                }
                if(zeroCount>zerosToWaitFor && listening){
                    updateValues(mean(currentWindowFrequencies));
                    for (int i = 0; i < currentWindowFrequencies.length; i++) {
                        currentWindowFrequencies[i] = 0;
                    }
                    currentWindowIndex = 0;
                    listening=false;
                    zeroCount=0;
                }
//                graphingCanvas.updateGraph(signal_processor.getPlot(data,"cepstral"), false, false);
                //endTime = System.currentTimeMillis();
                //System.out.println((endTime-startTime) + ", " + num + ", " + largestFreq);
                System.out.println(freq + ", " + currentString + ", " + zeroCount + " " + listening);


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
