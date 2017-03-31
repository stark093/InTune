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

public class MainActivityE extends AppCompatActivity {
    TextView currentFreqTextView;
    TextView desiredTextView;
    ImageView doneTuningCircle;
    Button tuneButton;
    boolean running = false;

    double desiredFrequency = 329.6;
    int currentString = 1;

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
        setContentView(R.layout.activity_main_e);

        updateGraphHandler = new Handler();
        graphingCanvas = (Draw_Graph) findViewById(R.id.graphE);

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



    Runnable runningLoop = new Runnable(){
        @Override
        public void run(){
            int delay = 50;
            try {
                if(pitch_algorithm!=null) {
                    double[] frequencyInformation = pitch_algorithm.getFreq();
                    double freq = frequencyInformation[0];
                    int sequenceNumber = (int)frequencyInformation[1];
                    if(sequenceNumber!=mostRecentSequenceNumber){
                        updateFrequency(freq);
                        mostRecentSequenceNumber = sequenceNumber;
                    }
                    if(freq!=0) {
                        double freqDifference = Math.abs(desiredFrequency - freq);
                        if(freqDifference<0.5){
                            doneTuning();
                        }
                        updateGraph(freqDifference);
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
        Intent i = new Intent(MainActivityE.this, MainActivity.class);
        startActivity(i);
        finish();
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

    private View.OnClickListener startListener = new View.OnClickListener() {
        public void onClick(View v) {
            if (running == false) {
                startTuning();
            } else {
                stopTuning();
            }
        }
    };
}

