package capstone.splash;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import java.lang.Math;
import android.content.Intent;
import android.widget.Button;
import android.widget.ImageView;

import static java.lang.Thread.sleep;

public class MainActivityG extends AppCompatActivity {
    TextView currentFreqTextView;
    TextView desiredTextView;
    ImageView doneTuningCircle;
    Button tuneButton;
    private long mLastClickTime = 0;
    int test = 5;


    boolean running = false;

    int directionChoice = 1;
    int rotationNumber = 0;
    double lastDifference = 0;

    double desiredFrequency = 196.0;
    int currentString = 3;

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
        setContentView(R.layout.activity_main_g);

        updateGraphHandler = new Handler();
        graphingCanvas = (Draw_Graph) findViewById(R.id.graphG);

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
        if(pitch_algorithm!=null) {
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
            ((BaseApplication) getApplicationContext()).turnX((int)(directionChoice*difference*20.1+10));
        }else{
            ((BaseApplication) getApplicationContext()).turnX((int)(directionChoice*difference*10-10));
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
                    int sequenceNumber = (int)frequencyInformation[1];

                    if(freq!=0) {
                        double freqDifference = (desiredFrequency - freq);
                        if(Math.abs(freqDifference)<0.5){
                            doneTuning();
                        }


                        if(sequenceNumber!=mostRecentSequenceNumber){
                            rotate(freqDifference);
                            updateFrequency(freq);
                            mostRecentSequenceNumber = sequenceNumber;
                        }

                    }
                    updateGraph(Math.abs(lastDifference));

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
        Intent i = new Intent(MainActivityG.this, MainActivityB.class);
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        startActivity(i);
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
        pitch_algorithm = new Pitch_Algorithm(currentString);
        runningLoop.run();
        tuneButton.setText("STOP");
    }

    private View.OnClickListener startListener = new View.OnClickListener() {
        public void onClick(View v) {
            // mis-clicking prevention, using threshold of 1000 ms
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
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

