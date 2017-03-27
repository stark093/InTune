package capstone.splash;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import capstone.splash.Draw_Graph;
import capstone.splash.R;

import java.lang.Math;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import static java.lang.Thread.sleep;

public class MainActivityB extends AppCompatActivity {
    ImageView drawingImageView;
    TextView up_freq;
    ImageView tuned_notify;
    Button start_tune;
    Thread th;
    Thread th1;
    boolean stop_start;
    double i = 130.0;
    double d, y;
    double amp = 16.0;
    double x = -0.5;
    GraphView graph;
    LineGraphSeries<DataPoint> series;
    private Draw_Graph graphingCanvas;
    private Handler updateGraphHandler;
    double [] q = new double [100];
    double [] t = new double [100];
    int p = 0;
    long z = 1;
    boolean flag = false;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_b);


        updateGraphHandler = new Handler();
        graphingCanvas = (Draw_Graph) findViewById(R.id.graphB);

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


        // Set up Grap
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("MainActivity2 Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    private static class SampleView extends View {

        public SampleView(Context context) {
            super(context);
            setFocusable(true);

        }

        @Override
        protected void onDraw(Canvas canvas) {

            canvas.drawColor(Color.CYAN);
            Paint p = new Paint();
            // smooths
            p.setAntiAlias(true);
            p.setColor(Color.RED);
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(4.5f);
            // opacity
            //p.setAlpha(0x80); //
            canvas.drawCircle(50, 50, 30, p);
        }
    }

    Runnable runningLoop = new Runnable(){
        @Override
        public void run(){
            int delay = 50;
            int dataPointsPowerOf2 = 0;
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
                }
                graphingCanvas.updateGraph(t,true,false);
            } finally {
                updateGraphHandler.postDelayed(runningLoop, delay);
            }
        }



    };


    private void stopThread() throws InterruptedException {
        th.stop();
    }

    private void stopThread2() throws InterruptedException {
        th1.stop();
    }

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
                                System.out.println(i);
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
                runningLoop.run();
                //startThread();
                start_tune.setText("STOP");
                stop_start = true;
            } else {
                stop_start = false;
                start_tune.setText("TUNE");
                th.interrupt();
            }
        }
    };
}

