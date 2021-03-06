package capstone.splash;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ArrayAdapter;
import android.view.View.OnClickListener;


public class MainActivity extends AppCompatActivity {

    public Button but1;
    public Button but2;
    public Button but3;
    public Button but4;
    public Button but5;
    public Button but6;
    public Button but7;
    public static String spinner_item;
    private Spinner spinner;
    public Switch auto_switch;
    public int position;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        addListenerOnSpinnerItemSelection();
        init();
    }

    public void addListenerOnSpinnerItemSelection() {
        spinner = (Spinner) findViewById(R.id.spinner1);
        spinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());
        spinner.setSelection(getIndex(spinner, ((BaseApplication) getApplicationContext()).getCurrentTuning()));
    }

    public class CustomOnItemSelectedListener implements AdapterView.OnItemSelectedListener, View.OnClickListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            spinner_item = parent.getItemAtPosition(pos).toString();
            ((BaseApplication) getApplicationContext()).setTuning(spinner_item);
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {

        }

        @Override
        public void onClick(View view) {
        }
    }

    public void init(){
        but1 = (Button)findViewById(R.id.button13);
        but2 = (Button)findViewById(R.id.button12);
        but3 = (Button)findViewById(R.id.button16);
        but4 = (Button)findViewById(R.id.button15);
        but5 = (Button)findViewById(R.id.button14);
        but6 = (Button)findViewById(R.id.button5);
        but7 = (Button)findViewById(R.id.DeviceButton);
        auto_switch = (Switch)findViewById(R.id.switch2);
        if (BaseApplication.switch_flag_state == true){
            auto_switch.toggle();
        }
        auto_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    ((BaseApplication) getApplicationContext()).setAutomateSwitch(true);
                } else {
                    ((BaseApplication) getApplicationContext()).setAutomateSwitch(false);
                }
            }
        });

        but1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                Intent toy = new Intent(MainActivity.this, MainActivityD.class);
                startActivity(toy);
            }
        });
        but2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent toy = new Intent(MainActivity.this, MainActivityA.class);
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
        but7.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent toy = new Intent(MainActivity.this, DeviceList.class);
                startActivity(toy);
            }
        });

    }

    private int getIndex(Spinner spinner, String myString){

        int index = 0;

        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).equals(myString)){
                index = i;
            }
        }
        return index;
    }


}
