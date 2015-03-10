package c.mars.bluetoothsample;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.util.Set;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * Created by Constantine Mars on 3/10/15.
 */
public class BtConnector {
    private static final int REQUEST_ENABLE_BT_DISCOVER = 1;
    private static final int REQUEST_ENABLE_BT_PAIRED = 2;
    private BluetoothAdapter adapter;
    private Activity a;
    private Dialogs d;
    private Set<BluetoothDevice> pairedDevices;
    private Display display;
    private boolean discoveryActive = false;

    @Inject
    public BtConnector(Activity a, Dialogs d, Display display) {
        this.a = a;
        this.d = d;
        this.display = display;
        adapter = BluetoothAdapter.getDefaultAdapter();
        Timber.d("bt adapter "+(adapter==null?"is null":"not null"));
    }

    public boolean isDiscoveryActive() {
        return discoveryActive;
    }

    public void discover() {
        if ( !setup(REQUEST_ENABLE_BT_DISCOVER) ) {
            return;
        }

        doDiscover();
    }

    public void cancelDiscovery() {
        Timber.d("cancel discovery");
        discoveryActive = false;
        adapter.cancelDiscovery();
        a.unregisterReceiver(discoveryReceiver);
    }

    private void doDiscover() {
        Timber.d("discover");
        discoveryActive = true;
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);

        a.registerReceiver(discoveryReceiver, filter);
        display.show("discovery:\n");
        adapter.startDiscovery();
    }

    public void paired() {
        Timber.d("paired");

        if ( !setup(REQUEST_ENABLE_BT_PAIRED) ) {
            return;
        }

        doPaired();
    }

    private void doPaired() {
        pairedDevices = adapter.getBondedDevices();
        display.show("paired devices:\n");
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                String s = device.getName() + " [" + device.getAddress()+"]\n";
                Timber.d(s);
                display.add(s);
            }
        }
    }

    private boolean setup(int requestCode) {
        if (adapter == null) {
            d.showError("(!) no bluetooth adapter available");
            return false;
        }
        if (!adapter.isEnabled()) {
            Timber.d("(!) bt is disabled");
            enable(requestCode);
            return false;
        }
        return true;
    }

    public void cleanup() {
        Timber.d("cleanup");
        adapter.cancelDiscovery();
        a.unregisterReceiver(discoveryReceiver);
    }

    private void enable(int requestCode) {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        a.startActivityForResult(enableBtIntent, requestCode);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT_DISCOVER || requestCode == REQUEST_ENABLE_BT_PAIRED) {
            Timber.d("enable bt result: "+(resultCode==a.RESULT_OK?"ok":"canceled"));

            switch (requestCode) {
                case REQUEST_ENABLE_BT_DISCOVER:
                    doDiscover();
                    break;
                case REQUEST_ENABLE_BT_PAIRED:
                    doPaired();
                    break;
            }
        }
    }

    // Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver discoveryReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Add the name and address to an array adapter to show in a ListView
                String s = device.getName() + " [" + device.getAddress()+"]\n";
                Timber.d(s);
                display.add(s);
            }
        }
    };
}
