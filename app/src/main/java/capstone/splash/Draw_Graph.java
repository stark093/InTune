package capstone.splash;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Process;
import android.support.annotation.Dimension;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import java.lang.Math;


/**
 * Created by Nick on 2016-11-16.
 *
 * Class responsible for drawing and updating graph continually.
 *
 */

public class Draw_Graph extends View {

    private Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Path path = new Path();
    private Thread graphThread = null;

    private int wGraph; //Width of graph area
    private int hGraph; //Height of graph area

    private int ticks;//number of times user pressed button.

    public Draw_Graph(Context context, AttributeSet attributes) {
        super(context, attributes);


        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(3);
        linePaint.setColor(Color.MAGENTA);

        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(30);
        borderPaint.setColor(Color.rgb(150, 0, 250));
        linePaint.setStrokeWidth(3);
        //set start, end

        callGraphingThread();
    }

    //create a thread that will run continually on the side
    private void callGraphingThread() {
        graphThread = new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            public void run() {
                Process.setThreadPriority(Process.THREAD_PRIORITY_DISPLAY);
            }

        }, "Thread to plot data"
        );
        graphThread.start();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(0,0,wGraph,hGraph,borderPaint);
        canvas.drawPath(path, linePaint);
        postInvalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        this.wGraph = w;
        this.hGraph = h;
        path.moveTo(0,hGraph/2);
        path.lineTo(wGraph,hGraph/2);
        super.onSizeChanged(w, h, oldw, oldh);
    }

    //calculates new points on grap, sets them and updates graph. Takes a set of points, and a boolean
    //indicating whether it should use fixed y-axis bounds or relative bounds. (i.e -5000 to 5000, or from
    // -maxY to +maxY calculated  from incoming points. Requires limits, enter limits if using fixed
    //or any arbitrary int if not.

    public void updateGraph(double[] points, boolean useFixedBounds,boolean isTimeDomain){
        double  maxY;
        boolean firstPoint = true;
        int numPoints = points.length;
        int xPointsArray[] = new int[numPoints];
        int yPointsArray[] = new int[numPoints];

        if(useFixedBounds){
            //For some reason max seems to be slightly above 2^14 = 16384
            if(isTimeDomain) {
                maxY = 16384;
            }else{
                maxY = 10;
            }
        }else{
            maxY = maxYInDataset(points);
        }

        path.reset();
        path.moveTo(0,0);

        for(int i=0;i<numPoints;i++){
            if(firstPoint){
                path.moveTo((float)normalizeX(i,numPoints),(float)normalizeY(points[i],maxY));
                firstPoint = false;
            }else {
                path.lineTo((float)normalizeX(i,numPoints),(float)normalizeY(points[i],maxY));
            }
        }

        invalidate();

    }

    //normalize the X values, given the number of points and knowing the total width.
    public double normalizeX(int x,int numPoints){
        double portionOfTotal = 0;
        portionOfTotal = (double) x / (double) numPoints;
        return portionOfTotal * this.wGraph;
    }



    //calculates the value of Y based on its value, the range of Y values, and the total height.
    public double normalizeY(double y, double maxY){
        double returnVal;
        int midPoint = this.hGraph/2;
        int availableAmplitudeSpace = this.hGraph/2;
        double portionOfMaxY = 0;
        portionOfMaxY = Math.abs((double) y)/(2*maxY);

        if(y>0){
            //y>=, we want to go "up" on the page, i.e subtract portion of "amplitude space(h/2) from the midpoint.
            returnVal = (midPoint)-(portionOfMaxY * (availableAmplitudeSpace));
        }else if(y<0){
            returnVal = (midPoint)+(portionOfMaxY * (availableAmplitudeSpace));
        }else {
            //else y = 0
            returnVal = midPoint;
        }

        return returnVal;

    }

    //find the maximum value of y in the given dataset
    public double maxYInDataset(double[] points){
        double maxY = 0;
        for(int i=0;i<points.length;i++){
            if( Math.abs(points[i])>maxY){
                maxY = Math.abs(points[i]);
            }
        }
        return maxY;
    }
}
