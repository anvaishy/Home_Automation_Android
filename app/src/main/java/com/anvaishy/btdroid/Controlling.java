package com.anvaishy.btdroid;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.UUID;
public class Controlling extends Activity {
    private static final String TAG = "BlueTest5-Controlling";
    private int mMaxChars = 50000;
    private UUID mDeviceUUID;
    private BluetoothSocket mBTSocket;
    private ReadInput mReadThread = null;
    private boolean mIsUserInitiatedDisconnect = false;
    private boolean mIsBluetoothConnected = false;
    private Button mBtnDisconnect;
    private BluetoothDevice mDevice;
    CardView c1,c2,c3;
    final static String on1="a";
    final static String off1="b";
    final static String on2="c";
    final static String off2="d";
    final static String on3="e";
    final static String off3="f";
    private ProgressDialog progressDialog;
    Button b1o,b1f,b2o,b2f,b3o,b3f;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        helper.initialize(this);
        b1o=(Button)findViewById(R.id.on1);
        b1f=(Button)findViewById(R.id.off1);
        b2o=(Button)findViewById(R.id.on2);
        b2f=(Button)findViewById(R.id.off2);
        b3o=(Button)findViewById(R.id.on3);
        b3f=(Button)findViewById(R.id.off3);
        c1=(CardView)findViewById(R.id.view1);
        c2=(CardView)findViewById(R.id.view2);
        c3=(CardView)findViewById(R.id.view3);
        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        mDevice = b.getParcelable(MainActivity.DEVICE_EXTRA);
        mDeviceUUID = UUID.fromString(b.getString(MainActivity.DEVICE_UUID));
        mMaxChars = b.getInt(MainActivity.BUFFER_SIZE);
        Log.d(TAG, "Ready");
        b1o.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                try {
                    mBTSocket.getOutputStream().write(on1.getBytes());
                    c1.setCardBackgroundColor(Color.parseColor("#00FF3C"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }});

        b1f.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                try {
                    mBTSocket.getOutputStream().write(off1.getBytes());
                    c1.setCardBackgroundColor(Color.parseColor("#FF0000"));
                } catch (IOException e) {

                    e.printStackTrace();
                }
            }});
        b2o.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v) {
                try {
                    mBTSocket.getOutputStream().write(on2.getBytes());
                    c2.setCardBackgroundColor(Color.parseColor("#00FF3C"));

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }});

        b2f.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
// TODO Auto-generated method stub
                try {
                    mBTSocket.getOutputStream().write(off2.getBytes());
                    c2.setCardBackgroundColor(Color.parseColor("#FF0000"));
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }});
        b3o.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v) {
// TODO Auto-generated method stub



                try {
                    mBTSocket.getOutputStream().write(on3.getBytes());
                    c3.setCardBackgroundColor(Color.parseColor("#00FF3C"));

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }});

        b3f.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
// TODO Auto-generated method stub
                try {
                    mBTSocket.getOutputStream().write(off3.getBytes());
                    c3.setCardBackgroundColor(Color.parseColor("#FF0000"));
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }});
    }
    private class ReadInput implements Runnable {
        private boolean bStop = false;
        private Thread t;

        public ReadInput() {
            t = new Thread(this, "Input Thread");
            t.start();
        }

        public boolean isRunning() {
            return t.isAlive();
        }

        @Override
        public void run() {
            InputStream inputStream;

            try {
                inputStream = mBTSocket.getInputStream();
                while (!bStop) {
                    byte[] buffer = new byte[256];
                    if (inputStream.available() > 0) {
                        inputStream.read(buffer);
                        int i = 0;
                        for (i = 0; i < buffer.length && buffer[i] != 0; i++) {
                        }
                        final String strInput = new String(buffer, 0, i);


                    }
                    Thread.sleep(500);
                }
            } catch (IOException e) {
// TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InterruptedException e) {
// TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        public void stop() {
            bStop = true;
        }

    }

    private class DisConnectBT extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Void... params) {

            if (mReadThread != null) {
                mReadThread.stop();
                while (mReadThread.isRunning())
                    ;
                mReadThread = null;

            }

            try {
                mBTSocket.close();
            } catch (IOException e) {
// TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            mIsBluetoothConnected = false;
            if (mIsUserInitiatedDisconnect) {
                finish();
            }
        }

    }

    private void msg(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        if (mBTSocket != null && mIsBluetoothConnected) {
            new DisConnectBT().execute();
        }
        Log.d(TAG, "Paused");
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (mBTSocket == null || !mIsBluetoothConnected) {
            new ConnectBT().execute();
        }
        Log.d(TAG, "Resumed");
        super.onResume();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "Stopped");
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
// TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void> {
        private boolean mConnectSuccessful = true;

        @Override
        protected void onPreExecute() {

            progressDialog = ProgressDialog.show(Controlling.this, "Hold on", "Connecting");
        }

        @Override
        protected Void doInBackground(Void... devices) {

            try {
                if (mBTSocket == null || !mIsBluetoothConnected) {
                    mBTSocket = mDevice.createInsecureRfcommSocketToServiceRecord(mDeviceUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    mBTSocket.connect();
                }
            } catch (IOException e) {
                mConnectSuccessful = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (!mConnectSuccessful) {
                Toast.makeText(getApplicationContext(), "Could not connect to device.Please turn on your Hardware", Toast.LENGTH_LONG).show();
                finish();
            } else {
                msg("Connected to device");
                mIsBluetoothConnected = true;
                mReadThread = new ReadInput();
            }

            progressDialog.dismiss();
        }

    }
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }
}