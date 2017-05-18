package com.example.jorgezeped.fuzzylogic;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private String LOG_TAG; // Just for logging purposes. Could be anything. Set to app_name
    private int REQUEST_ENABLE_BT = 99; // Any positive integer should work.
    private BluetoothAdapter mBluetoothAdapter;

    private Button button_displayPairedBT;
    private Button button_scanBT;
    Handler bluetoothIn;
    final int handlerState = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LOG_TAG = getResources().getString(R.string.app_name);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothIn = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == handlerState) {          //if message is what we want
                    /*String readMessage = (String) msg.obj;                                                                // msg.arg1 = bytes from connect thread
                    recDataString.append(readMessage);              //keep appending to string until ~
                    int endOfLineIndex = recDataString.indexOf("~");                    // determine the end-of-line
                    if (endOfLineIndex > 0) {                                           // make sure there data before ~
                        String dataInPrint = recDataString.substring(0, endOfLineIndex);    // extract string
                        txtString.setText("Datos recibidos = " + dataInPrint);
                        int dataLength = dataInPrint.length();       //get length of data received
                        txtStringLength.setText("Tamaño del String = " + String.valueOf(dataLength));

                        if (recDataString.charAt(0) == '#')        //if it starts with # we know it is what we are looking for
                        {
                            String sensor0 = recDataString.substring(1, 5);             //get sensor value from string between indices 1-5
                            String sensor1 = recDataString.substring(6, 10);            //same again...
                            String sensor2 = recDataString.substring(11, 15);
                            String sensor3 = recDataString.substring(16, 20);

                            if(sensor0.equals("1.00"))
                                sensorView0.setText("Encendido"); //update the textviews with sensor values
                            else
                                sensorView0.setText("Apagado"); //update the textviews with sensor values
                            sensorView1.setText(sensor1);
                            sensorView2.setText(sensor2);
                            sensorView3.setText(sensor3);
                            //sensorView3.setText(" Sensor 3 Voltage = " + sensor3 + "V");
                        }
                        recDataString.delete(0, recDataString.length());      //clear all string data
                        // strIncom =" ";
                        dataInPrint = " ";
                    }
                    */
                }
            }
        };

        button_displayPairedBT = (Button) findViewById(R.id.button_alreadyPairedBT);
        button_scanBT = (Button) findViewById(R.id.button_scanBT);

        enableBluetoothOnDevice();

        button_displayPairedBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getArrayOfAlreadyPairedBluetoothDevices() != null) {
                    Intent intent = new Intent(MainActivity.this, AlreadyPairedList.class);
                    intent.putParcelableArrayListExtra("arrayOfPairedDevices", (ArrayList<BluetoothObject>) getArrayOfAlreadyPairedBluetoothDevices());
                    startActivity(intent);
                }
            }
        });

        button_scanBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                scanForBluetoothDevices();
            }
        });


    }

    private void enableBluetoothOnDevice() {
        if (mBluetoothAdapter == null) {
            Log.e(LOG_TAG, "Este dispositivo no tiene bluetooth");
            finish();
        }

        if( !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            scanForBluetoothDevices();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == 0) {
                Toast.makeText(this, "No se encendio el dispositivo bluetooth", Toast.LENGTH_LONG).show();
            }
            else
                Toast.makeText(this, "Dispositivo bluetooth encendido", Toast.LENGTH_LONG).show();
        }
    }

    private List<BluetoothObject> getArrayOfAlreadyPairedBluetoothDevices() {
        List<BluetoothObject> listOfAlreadyPairedBTDevices = null;

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            listOfAlreadyPairedBTDevices = new ArrayList<BluetoothObject>();

            for (BluetoothDevice device : pairedDevices) {
                BluetoothObject bluetoothObject = new BluetoothObject();
                bluetoothObject.setBluetooth_name(device.getName());
                bluetoothObject.setBluetooth_address(device.getAddress());
                bluetoothObject.setBluetooth_state(device.getBondState());
                bluetoothObject.setBluetooth_type(device.getType());
                bluetoothObject.setBluetooth_uuids(device.getUuids());

                listOfAlreadyPairedBTDevices.add(bluetoothObject);
            }
        }
        return listOfAlreadyPairedBTDevices;
    }

    private void scanForBluetoothDevices() {
        Intent intent = new Intent(this, FoundBTDevices.class);
        startActivity(intent);
    }


    //create new class for connect thread
    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        //creation of the connect thread
        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                //Create I/O streams for connection
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }


        public void run() {
            byte[] buffer = new byte[256];
            int bytes;

            // Keep looping to listen for received messages
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);         //read bytes from input buffer
                    String readMessage = new String(buffer, 0, bytes);
                    // Send the obtained bytes to the UI Activity via handler
                    bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }
        //write method
        public void write(String input) {
            byte[] msgBuffer = input.getBytes();           //converts entered String into bytes
            try {
                mmOutStream.write(msgBuffer);                //write bytes over BT connection via outstream
            } catch (IOException e) {
                //if you cannot write, close the application
                Toast.makeText(getBaseContext(), "La Conexión fallo", Toast.LENGTH_LONG).show();
                finish();

            }
        }

    }
}
