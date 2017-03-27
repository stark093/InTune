package capstone.splash;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    public Button but1;
    public Button but2;
    public Button but3;
    public Button but4;
    public Button but5;
    public Button but6;

    public void init(){
        but1 = (Button)findViewById(R.id.button13);
        but2 = (Button)findViewById(R.id.button12);
        but3 = (Button)findViewById(R.id.button16);
        but4 = (Button)findViewById(R.id.button15);
        but5 = (Button)findViewById(R.id.button14);
        but6 = (Button)findViewById(R.id.button5);
        but1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent toy = new Intent(MainActivity.this, MainActivity2.class);
                startActivity(toy);
            }
        });
        but2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent toy = new Intent(MainActivity.this, MainActivity3.class);
                startActivity(toy);
            }
        });
        but3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent toy = new Intent(MainActivity.this, MainActivityG.class);
                startActivity(toy);
            }
        });
        but4.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent toy = new Intent(MainActivity.this, MainActivityB.class);
                startActivity(toy);
            }
        });
        but5.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent toy = new Intent(MainActivity.this, MainActivityE.class);
                startActivity(toy);
            }
        });
        but6.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent toy = new Intent(MainActivity.this, MainActivityE_1.class);
                startActivity(toy);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        init();
    }
}
