package capstone.splash;

        import android.content.Context;
        import android.graphics.Canvas;
        import android.graphics.Color;
        import android.graphics.Paint;
        import android.graphics.Path;
        import android.util.AttributeSet;
        import android.view.View;

public class MyView extends View {

    Paint paint1;
    Paint paint2;
    Path path;


    public MyView(Context context) {
        super(context);
        init();
    }

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init(){
        paint1 = new Paint();
        paint1.setColor(Color.MAGENTA);
        paint1.setStrokeWidth(50);
        paint1.setStyle(Paint.Style.STROKE);
        paint2 = new Paint();
        paint2.setColor(Color.rgb(150, 0, 250));
        paint2.setStyle(Paint.Style.STROKE);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);

        paint1.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(300, 300, 200, paint1);
        paint2.setStyle(Paint.Style.FILL);
        canvas.drawCircle(300, 300, 200, paint2);
        //drawCircle(cx, cy, radius, paint)

    }

}