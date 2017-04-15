package capstone.splash;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
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
    private String deviceName = "INTUNE2";
    private boolean initialConnection = true;
    public double desiredFrequency;


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

        if (btSocket != null) {
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

    public void setFrequency(int string){
        if (MainActivity.spinner_item == "Standard Tuning"){
            if (string == 6){ // String E_1
                desiredFrequency = 82.4;
            } else if (string == 5){ // String A
                desiredFrequency = 110.0;
            } else if (string == 4){ // String D
                desiredFrequency = 146.8;
            } else if (string == 3){ // String G
                desiredFrequency = 196.0;
            } else if (string == 2){ // String B
                desiredFrequency = 246.9;
            } else if (string == 1){ // String E
                desiredFrequency = 329.6;
            }
        } else if (MainActivity.spinner_item == "Drop D"){
            if (string == 6){ // String E_1
                desiredFrequency = 73.4;
            } else if (string == 5){ // String A
                desiredFrequency = 110.0;
            } else if (string == 4){ // String D
                desiredFrequency = 146.8;
            } else if (string == 3){ // String G
                desiredFrequency = 196.0;
            } else if (string == 2){ // String B
                desiredFrequency = 246.9;
            } else if (string == 1){ // String E
                desiredFrequency = 329.6;
            }
        } else if (MainActivity.spinner_item == "Double drop D"){
            if (string == 6){ // String E_1
                desiredFrequency = 73.4;
            } else if (string == 5){ // String A
                desiredFrequency = 110.0;
            } else if (string == 4){ // String D
                desiredFrequency = 146.8;
            } else if (string == 3){ // String G
                desiredFrequency = 196.0;
            } else if (string == 2){ // String B
                desiredFrequency = 246.9;
            } else if (string == 1){ // String E
                desiredFrequency = 293.7;
            }
        }
    }

    public double getFrequency(){
        return desiredFrequency;
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
