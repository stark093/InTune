package capstone.splash;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    public Button but1;
    public Button but2;
    public Button but3;
    public Button but4;
    public Button but5;
    public Button but6;
    public Button but7;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        init();
    }


    public void init(){
        but1 = (Button)findViewById(R.id.button13);
        but2 = (Button)findViewById(R.id.button12);
        but3 = (Button)findViewById(R.id.button16);
        but4 = (Button)findViewById(R.id.button15);
        but5 = (Button)findViewById(R.id.button14);
        but6 = (Button)findViewById(R.id.button5);
        but7 = (Button)findViewById(R.id.DeviceButton);

        but1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                but1.setEnabled(false);
                Intent toy = new Intent(MainActivity.this, MainActivityD.class);
                startActivity(toy);
            }
        });
        but2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                but2.setEnabled(false);
                Intent toy = new Intent(MainActivity.this, MainActivityA.class);
                startActivity(toy);
            }
        });
        but3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                but3.setEnabled(false);
                Intent toy = new Intent(MainActivity.this, MainActivityG.class);
                startActivity(toy);
            }
        });
        but4.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                but4.setEnabled(false);
                Intent toy = new Intent(MainActivity.this, MainActivityB.class);
                startActivity(toy);
            }
        });
        but5.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                but5.setEnabled(false);
                Intent toy = new Intent(MainActivity.this, MainActivityE.class);
                startActivity(toy);
            }
        });
        but6.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                but6.setEnabled(false);
                Intent toy = new Intent(MainActivity.this, MainActivityE_1.class);
                startActivity(toy);
            }
        });
        but7.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                but7.setEnabled(false);
                Intent toy = new Intent(MainActivity.this, DeviceList.class);
                startActivity(toy);
            }
        });
    }


}
