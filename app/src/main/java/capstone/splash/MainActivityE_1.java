package capstone.splash;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import java.lang.Math;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import android.content.Intent;
import android.widget.Button;
import android.widget.ImageView;

import static java.lang.Thread.sleep;

public class MainActivityE_1 extends AppCompatActivity {
    TextView currentFreqTextView;
    TextView desiredTextView;
    ImageView doneTuningCircle;
    Button tuneButton;
    boolean running = false;
    boolean done = false;

    private long mLastClickTime = 0;

    double desiredFrequency;

    int directionChoice = 1;
    int rotationNumber = 0;
    double lastDifference = 0;


    private Draw_Graph graphingCanvas;
    private Handler updateGraphHandler;
    double [] graphArray = new double [100];
    int phase = 0;
    Pitch_Algorithm pitch_algorithm;
    int mostRecentSequenceNumber = 0;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get Desired Frequency
        desiredFrequency = ((BaseApplication) getApplicationContext()).getFrequency(6);

        setContentView(R.layout.activity_main_e_1);

        updateGraphHandler = new Handler();
        graphingCanvas = (Draw_Graph) findViewById(R.id.graphE_1);

        // Declare Objects
        desiredTextView = (TextView) findViewById(R.id.desired_freq_val);
        desiredTextView.setText(String.format("%.1f Hz",desiredFrequency));
        currentFreqTextView = (TextView) findViewById(R.id.freq);
        doneTuningCircle = (ImageView) findViewById(R.id.tune_notify);

        // Set up button
        tuneButton = (Button) findViewById(R.id.start_button);
        tuneButton.setOnClickListener(startListener);

        startTuning();
    }

    @Override
    protected void onStop() {
        super.onStop();
        running = false;
        if (pitch_algorithm != null) {
            pitch_algorithm.kill();
            pitch_algorithm = null;
        }
    }

    private void rotate(double difference){
        lastDifference = difference;
        rotationNumber++;

        //After the first rotation, check what happened.
        if((Math.abs(lastDifference-difference))>1) {
            if (lastDifference > difference) {
                //we know it's going in the right direction. Do nothing.

            } else {
                //it's going in the wrong direction. Reverse it.
                directionChoice = -1;
            }
        }
        if(difference>0){
            if(difference>3) {
                ((BaseApplication) getApplicationContext()).turnX((int) (directionChoice * difference * 13.8851));
            }else if(difference>2){
                ((BaseApplication) getApplicationContext()).turnX((int) (directionChoice * 10));
            }else{
                ((BaseApplication) getApplicationContext()).turnX((int) (directionChoice * 5));
            }
        }else{
            if(difference<-3) {
                ((BaseApplication) getApplicationContext()).turnX((int) (directionChoice * difference * 10.5));
            }else if(difference<-2){
            ((BaseApplication) getApplicationContext()).turnX((int) (directionChoice * 10));
        }else{
            ((BaseApplication) getApplicationContext()).turnX((int) (directionChoice * 5));
        }
        }
    }

    Runnable runningLoop = new Runnable(){
        @Override
        public void run(){
            int delay = 50;
            try {


                if(pitch_algorithm!=null) {
                    double[] frequencyInformation = pitch_algorithm.getFreq();
                    double freq = frequencyInformation[0];
                    int sequenceNumber = (int) frequencyInformation[1];

                    if (freq != 0) {
                        double freqDifference = (desiredFrequency - freq);
                        if (Math.abs(freqDifference) < 0.5) {
                            done = true;
                            updateGraph(0.0);
                            doneTuning();
                        }


                        if (sequenceNumber != mostRecentSequenceNumber) {
                            if (!done) {
                                rotate(freqDifference);
                            }
                            updateFrequency(freq);
                            mostRecentSequenceNumber = sequenceNumber;
                        }
                    }
                    if ((desiredFrequency - freq) < 0.5) {
                        updateGraph(0.0);
                    } else {
                        updateGraph(Math.abs(lastDifference));
                    }
                }
            } finally {
                if(running)
                    updateGraphHandler.postDelayed(runningLoop, delay);
            }
        }
    };

    private void doneTuning(){
        running = false;
        if(pitch_algorithm!=null) {
            pitch_algorithm.kill();
            pitch_algorithm = null;
        }
        updateImage();

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Intent i_auto = new Intent(MainActivityE_1.this, MainActivityA.class);
        Intent i_main = new Intent(MainActivityE_1.this, MainActivity.class);

        if (((BaseApplication) getApplicationContext()).getAutomateSwitch() == true){
            startActivity(i_auto);
        } else {

            startActivity(i_main);
        }
        finish();
    }

    public void updateGraph(double graphAmplitude){
        if(phase>43){
            phase=0;
        }else{
            phase++;
        }
        for (int i = 0; i < 100; i++) {
            graphArray[i] = graphAmplitude*Math.sin((i/4. + (phase)/7.));
        }
        graphingCanvas.updateGraph(graphArray,true,false);
        graphingCanvas.postInvalidate();
    }

    public void updateFrequency(double frequencyToDisplay) {

        currentFreqTextView.setText(String.format("%.3f Hz", frequencyToDisplay));
    }

    public void updateImage() {
        doneTuningCircle.setImageDrawable(getResources().getDrawable(R.drawable.tuned_pink));
    }

    public void stopTuning(){
        running = false;
        if(pitch_algorithm!=null) {
            pitch_algorithm.kill();
            pitch_algorithm = null;
        }
        tuneButton.setText("TUNE");
    }

    public void startTuning(){
        running = true;
        pitch_algorithm = new Pitch_Algorithm(desiredFrequency);
        runningLoop.run();
        tuneButton.setText("STOP");
    }

    private View.OnClickListener startListener = new View.OnClickListener() {
        public void onClick(View v) {
            // mis-clicking prevention, using threshold of 1000 ms
            if (SystemClock.elapsedRealtime() - mLastClickTime < 3000){
                return;

            }
            mLastClickTime = SystemClock.elapsedRealtime();
            if (running == false) {
                startTuning();
            } else {
                stopTuning();
            }
        }
    };
}

