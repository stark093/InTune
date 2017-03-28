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

public class MainActivityG extends AppCompatActivity {
    TextView up_freq;
    ImageView tuned_notify;
    Button start_tune;
    private volatile Thread th;
    boolean stop_start;
    double i = 130.0;
    double amp = 16.0;
    private Draw_Graph graphingCanvas;
    private Handler updateGraphHandler;
    private Handler switchHandler;
    double [] q = new double [100];
    double [] t = new double [100];
    int p = 0;
    boolean flag = false;
    boolean flag_switch_activity = false;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_g);


        updateGraphHandler = new Handler();
        switchHandler = new Handler();
        graphingCanvas = (Draw_Graph) findViewById(R.id.graphG);

        // Declare Objects
        up_freq = (TextView) findViewById(R.id.freq);
        tuned_notify = (ImageView) findViewById(R.id.tune_notify);

        // Set up button
        start_tune = (Button) findViewById(R.id.start_button);
        start_tune.setOnClickListener(startListener);

        // Set up variables
        double y, x;
        x = -5.0;
        stop_start = false;
        runningLoop_Switch.run();
    }

    Runnable runningLoop = new Runnable(){
        @Override
        public void run(){
            int delay = 50;
            try {
                for (int i=0;i <100;i++){
                    q[i]=i + p;
                }
                p = p + 1;
                if (!flag) {
                    for (int i = 0; i < 100; i++) {
                        t[i] = amp*Math.sin(q[i]/4.0);
                    }
                } else if (flag) {
                    for (int i = 0; i < 100; i++) {
                        t[i] = 0;
                    }
                    flag_switch_activity = true;
                }
                graphingCanvas.updateGraph(t,true,false);
            } finally {
                if(stop_start)
                    updateGraphHandler.postDelayed(runningLoop, delay);
            }
        }
    };


    Runnable runningLoop_Switch = new Runnable(){
        @Override
        public void run(){
            int delay1 = 50;
            try {
                if (flag_switch_activity) {
                    SystemClock.sleep(1000);
                    Intent toy = new Intent(MainActivityG.this, MainActivityB.class);
                    startActivity(toy);
                }
            } finally {
                if (!flag_switch_activity) {
                    switchHandler.postDelayed(runningLoop_Switch, delay1);
                }
            }
        }
    };


    private void startThread() {
        th = new Thread(new Runnable() {
            public void run() {
                while (true) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateFrequency(i);
                            if (i < 146) {
                                amp = 146.0 - i;
                                i = i + 1;
                                flag = false;
                            } else {
                                amp = 0;
                                flag = true;
                                updateFrequency(146.3);
                                updateImage(R.drawable.pink_circ);
                            }

                        }
                    });
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        th.start();
    }

    public void updateFrequency(double frequencyToDisplay) {
        String printStr = Double.toString(frequencyToDisplay) + " Hz";
        up_freq.setText(printStr);
    }

    public void updateImage(int tuneImage) {
        tuned_notify.setImageDrawable(getResources().getDrawable(tuneImage));
    }

    private View.OnClickListener startListener = new View.OnClickListener() {
        public void onClick(View v) {
            if (stop_start == false) {
                startThread();
                stop_start = true;
                runningLoop.run();
                start_tune.setText("STOP");
            } else {
                stop_start = false;
                start_tune.setText("TUNE");
            }
        }
    };
}

