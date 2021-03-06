package capstone.splash;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Nick on 2017-03-29.
 */

public class BaseApplication extends Application {
    String address = null;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    //SPP UUID. Look for it
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private String deviceName = "INTUNE";
    private boolean initialConnection = true;
    private double desiredFrequency_e = 82.4;
    private double desiredFrequency_a = 110.0;
    private double desiredFrequency_d = 146.8;
    private double desiredFrequency_g = 196.0;
    private double desiredFrequency_b = 246.9;
    private double desiredFrequency_e1 = 329.6;
    private boolean switch_flag = false;
    static boolean switch_flag_state;
    private boolean auto_toggle_switch = false;
    private String currentTuning = "Standard Tuning";

    @Override
    public void onCreate(){
        super.onCreate();

        new ConnectBT().execute();

    }
    // fast way to call Toast
    private void msg(String s)
    {
        Toast.makeText(getApplicationContext(),s, Toast.LENGTH_LONG).show();
    }

    public void connectNewBT(String newAddress){
        address = newAddress;
        resetConnection();
        new ConnectBT().execute();
    }

    private void resetConnection() {
        if (btSocket != null) {
        try {

                if (btSocket.getInputStream() != null) {
                    btSocket.getInputStream().close();
                }

        }
            catch (IOException e) {}

        try {
            if (btSocket.getOutputStream() != null) {
                btSocket.getOutputStream().close();
            }
        }
        catch (IOException e) {}

            try {btSocket.close();} catch (Exception e) {}
            btSocket = null;

        }
        isBtConnected = false;
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected


        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try
            {
                if (btSocket == null || !isBtConnected)
                {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    if(initialConnection) {
                        initialConnection = false;
                        Set<BluetoothDevice> pastDevices = myBluetooth.getBondedDevices();
                        if (pastDevices != null) {
                            for (BluetoothDevice device : pastDevices) {
                                if (deviceName.equals(device.getName())) {
                                    address = device.getAddress();
                                    break;
                                }
                            }
                        }
                    }
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//start connection
                }
            }
            catch (IOException | IllegalArgumentException e)
            {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess)
            {
                if(address!=null) {
                    msg("Connection Failed. Check the device and try again.");
                }else{
                    msg("Could not find your device.\n Please select new device");
                }
            }
            else
            {
                msg("Connected.");
                isBtConnected = true;
            }
        }
    }

    public String getCurrentTuning(){
        return currentTuning;
    }

    public void setAutomateSwitch(boolean auto_switch){
        auto_toggle_switch = auto_switch;
        if (auto_toggle_switch == true){
            switch_flag = true;
            switch_flag_state = switch_flag;
        } else {
            switch_flag = false;
            switch_flag_state = switch_flag;
        }
    }

    public boolean getAutomateSwitch(){
        return switch_flag;
    }

    public void setTuning(String tuning){
        currentTuning=tuning;
        if (tuning.equals("Standard Tuning")){
            desiredFrequency_e = 82.4;
            desiredFrequency_a = 110.0;
            desiredFrequency_d = 146.8;
            desiredFrequency_g = 196.0;
            desiredFrequency_b = 246.9;
            desiredFrequency_e1 = 329.6;
        } else if (tuning.equals("Drop D")){
            desiredFrequency_e = 73.4;
            desiredFrequency_a = 110.0;
            desiredFrequency_d = 146.8;
            desiredFrequency_g = 196.0;
            desiredFrequency_b = 246.9;
            desiredFrequency_e1 = 329.6;
        } else if (tuning.equals("Double drop D")){
            desiredFrequency_e = 73.4;
            desiredFrequency_a = 110.0;
            desiredFrequency_d = 146.8;
            desiredFrequency_g = 196.0;
            desiredFrequency_b = 246.9;
            desiredFrequency_e1 = 293.7;
        } else if (tuning.equals("Open E")){
            desiredFrequency_e = 82.4;
            desiredFrequency_a = 123.5;
            desiredFrequency_d = 164.8;
            desiredFrequency_g = 207.7;
            desiredFrequency_b = 246.9;
            desiredFrequency_e1 = 329.6;
        }else if (tuning.equals("Drop C#")){
            desiredFrequency_e = 69.3;
            desiredFrequency_a = 103.8;
            desiredFrequency_d = 138.6;
            desiredFrequency_g = 185;
            desiredFrequency_b = 233.1;
            desiredFrequency_e1 = 311.1;
        }else if (tuning.equals("Open G")){
            desiredFrequency_e = 73.4;
            desiredFrequency_a = 98;
            desiredFrequency_d = 146.8;
            desiredFrequency_g = 196;
            desiredFrequency_b = 246.9;
            desiredFrequency_e1 = 293.7;
        } else if (tuning.equals("Open D")){
            desiredFrequency_e = 73.4;
            desiredFrequency_a = 110;
            desiredFrequency_d = 146.8;
            desiredFrequency_g = 185;
            desiredFrequency_b = 220;
            desiredFrequency_e1 = 293.7;
        }else if (tuning.equals("Open C")){
            desiredFrequency_e = 65.4;
            desiredFrequency_a = 98;
            desiredFrequency_d = 130.8;
            desiredFrequency_g = 196;
            desiredFrequency_b = 261.6;
            desiredFrequency_e1 = 329.6;
        }else if (tuning.equals("Open A")){
            desiredFrequency_e = 82.4;
            desiredFrequency_a = 110;
            desiredFrequency_d = 138.6;
            desiredFrequency_g = 164.8;
            desiredFrequency_b = 110;
            desiredFrequency_e1 = 329.6;
        }
    }

    public double getFrequency(int string) {
        if (string == 6) {
            return desiredFrequency_e;
        } else if (string == 5) {
            return desiredFrequency_a;
        } else if (string == 4) {
            return desiredFrequency_d;
        } else if (string == 3) {
            return desiredFrequency_g;
        } else if (string == 2) {
            return desiredFrequency_b;
        } else {
            return desiredFrequency_e1;
        }
    }

    public void turnX(int X)
    {
        String XString = new Integer(X).toString();

        XString = XString.concat(":");
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write(XString.getBytes());
                System.out.println("sent: " + XString);
            }
            catch (IOException e)
            {
                msg("Error");
            }
        }
    }
}
