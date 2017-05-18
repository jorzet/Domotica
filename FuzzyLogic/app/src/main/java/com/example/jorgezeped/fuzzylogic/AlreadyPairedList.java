package com.example.jorgezeped.fuzzylogic;

/**
 * Created by jorgezeped on 16/05/17.
 */
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;

public class AlreadyPairedList extends ListActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        ArrayList<BluetoothObject> arrayOfPairedDevices = intent.getParcelableArrayListExtra("arrayOfPairedDevices");
        AlreadyPairedAdapter myAdapter = new AlreadyPairedAdapter(getApplicationContext(), arrayOfPairedDevices);
        setListAdapter(myAdapter);
    }
}
