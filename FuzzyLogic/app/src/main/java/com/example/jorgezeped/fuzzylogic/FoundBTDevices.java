package com.example.jorgezeped.fuzzylogic;

/**
 * Created by jorgezeped on 16/05/17.
 */

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

import static java.security.AccessController.getContext;

public class FoundBTDevices extends ListActivity {
    private BluetoothAdapter mBluetoothAdapter;
    private ArrayList<BluetoothObject> arrayOfFoundBTDevices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        displayListOfFoundDevices();
    }

    private void displayListOfFoundDevices() {
        arrayOfFoundBTDevices = new ArrayList<BluetoothObject>();

        // start looking for bluetooth devices
        mBluetoothAdapter.startDiscovery();

        // Discover new devices
        // Create a BroadcastReceiver for ACTION_FOUND
        final BroadcastReceiver mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                // When discovery finds a device
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    // Get the bluetoothDevice object from the Intent
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                    // Get the "RSSI" to get the signal strength as integer,
                    // but should be displayed in "dBm" units
                    int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE);

                    // Create the device object and add it to the arrayList of devices
                    BluetoothObject bluetoothObject = new BluetoothObject();
                    bluetoothObject.setBluetooth_name(device.getName());
                    bluetoothObject.setBluetooth_address(device.getAddress());
                    bluetoothObject.setBluetooth_state(device.getBondState());
                    bluetoothObject.setBluetooth_type(device.getType());    // requires API 18 or higher
                    bluetoothObject.setBluetooth_uuids(device.getUuids());
                    bluetoothObject.setBluetooth_rssi(rssi);

                    arrayOfFoundBTDevices.add(bluetoothObject);

                    // 1. Pass context and data to the custom adapter
                    FoundBTDevicesAdapter adapter = new FoundBTDevicesAdapter(getApplicationContext(), arrayOfFoundBTDevices);

                    // 2. setListAdapter
                    setListAdapter(adapter);
                }
            }
        };
        // Register the BroadcastReceiver
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();

        mBluetoothAdapter.cancelDiscovery();
    }

}