package com.twins.homeautomation;

import android.os.Bundle;

public class MainActivity extends android.app.Activity implements android.view.View.OnClickListener {

    private android.widget.Button mConnectBtn;
    private android.widget.ImageView bulbOnOff;

    private android.bluetooth.BluetoothAdapter btAdapter = null;
    private android.bluetooth.BluetoothSocket btSocket = null;
    private java.io.OutputStream outStream = null;

    private static final java.util.UUID MY_UUID = java.util.UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private static String address = "30:14:09:25:09:78";

    private boolean isOn;
    private boolean isConnect;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        bulbOnOff = (android.widget.ImageView) findViewById(R.id.bulb_on_off);
        mConnectBtn = (android.widget.Button) findViewById(R.id.bluetooth_connect);

        btAdapter = android.bluetooth.BluetoothAdapter.getDefaultAdapter();
        checkBTState();

        mConnectBtn.setOnClickListener(this);
        bulbOnOff.setOnClickListener(this);
    }

    private android.bluetooth.BluetoothSocket createBluetoothSocket(android.bluetooth.BluetoothDevice device) throws java.io.IOException {
        if (android.os.Build.VERSION.SDK_INT >= 10) {
            try {
                final java.lang.reflect.Method m = device.getClass().getMethod("", new Class[]{java.util.UUID.class});
                return (android.bluetooth.BluetoothSocket) m.invoke(device, MY_UUID);
            } catch (Exception e) {

            }
        }
        return device.createRfcommSocketToServiceRecord(MY_UUID);
    }

    private void disconnectBt() {
        if (btSocket != null) {
            try {
                btSocket.close();
                mConnectBtn.setText("Connect");
            } catch (java.io.IOException e) {
            }
        }
    }

    private void checkBTState() {
        if (btAdapter == null) {
        } else {
            if (btAdapter.isEnabled()) {

            } else {
                android.content.Intent enableBtIntent = new android.content.Intent(android.bluetooth.BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    private void sendData(String message) {
        byte[] msgBuffer = message.getBytes();

        try {
            outStream.write(msgBuffer);
        } catch (java.io.IOException e) {

        }
    }

    @Override
    public void onClick(android.view.View view) {
        switch (view.getId()) {
            case com.twins.homeautomation.R.id.bulb_on_off:
                if (btSocket != null) {
                    if (isOn) {
                        isOn = false;
                        sendData("0");
                        bulbOnOff.setImageResource(com.twins.homeautomation.R.drawable.bulb_off);
                    } else {
                        isOn = true;
                        sendData("1");
                        bulbOnOff.setImageResource(com.twins.homeautomation.R.drawable.bulb_on);
                    }
                } else {
                    android.widget.Toast.makeText(getApplicationContext(), "Please connect to device", android.widget.Toast.LENGTH_SHORT).show();
                }
                break;
            case com.twins.homeautomation.R.id.bluetooth_connect:
                if (isConnect) {
                    isConnect = false;
                    disconnectBt();
                } else {
                    isConnect = true;
                    new com.twins.homeautomation.MainActivity.ConnectBT().execute();
                }
                break;
            default:
                break;
        }
    }

    private class ConnectBT extends android.os.AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mConnectBtn.setText("Please Wait");
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                android.bluetooth.BluetoothDevice device = btAdapter.getRemoteDevice(address);

                btSocket = createBluetoothSocket(device);
            } catch (java.io.IOException e1) {

            }
            btAdapter.cancelDiscovery();

            try {
                btSocket.connect();
                isConnect = true;
            } catch (java.io.IOException e) {
                try {
                    btSocket.close();
                } catch (java.io.IOException e2) {

                }
            }
            try {
                outStream = btSocket.getOutputStream();
            } catch (java.io.IOException e) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (isConnect) {
                mConnectBtn.setText("Disconnect");
            } else {

            }
        }
    }
}