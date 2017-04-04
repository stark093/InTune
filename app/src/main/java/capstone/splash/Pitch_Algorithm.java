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
    double[] audioData;
    int highLim;
    int lowLim;
    int currentString;

    double[] currentWindowFrequencies = new double[2048];
    int zeroCount = 0;
    int currentWindowIndex = 0;
    boolean listening=false;

    /* TWEAK THESE VARIABLES*/
    int numToIgnore=3;
    int zerosToWaitFor = 2;

    double updatedFrequency = 0;
    int updateSequenceNumber = 1;

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
                lowLim = 72;
                highLim = 92;
                break;
            case 5:
                lowLim = 100;
                highLim = 120;
                break;
            case 4:
                lowLim = 136;
                highLim = 156;
                break;
            case 3:
                lowLim = 186;
                highLim = 206;
                break;
            case 2:
                lowLim = 237;
                highLim = 257;
                break;
            case 1://thinnest
                lowLim = 320;
                highLim=340;
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

    public synchronized void updateValues(double freqVal){
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
                //System.out.println(freq + ", " + currentString + ", " + zeroCount + " " + listening);


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
