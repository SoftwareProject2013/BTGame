package com.example.blootothgame;

import java.io.IOException;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

public class MainActivity extends Activity implements SensorEventListener{

	private SensorManager sensorManager;
	double sum, oldsum;
	int sensivity;
	private static final String TAG = "Main Activity";
	BluetoothAdapter mBluetoothAdapter;
	BluetoothServerSocket mBluetoothServerSocket;
	private static UUID mUUID;
	private static final String UUIDString = "38400000-8cf0-11bd-b23e-10b96e4ef00d";
	private final static String mMAC = "5C:B5:24:BD:5B:C4" ;//"18:87:96:03:68:D1"; //"5C:B5:24:BD:5B:C4";
	protected static final int MESSAGE_WRITE = 0;
	protected static final int MESSAGE_READ = 1;
	protected static final int MESSAGE_START = 3;
	private int player1Progress;
	private int player2Progress;
	SeekBar player1SeekBar;
	SeekBar player2SeekBar;
	byte i = 0;
	BTServer btServer;
	BTClient btClient;
	boolean isServer;
	Button btStart;
	boolean start;
	boolean opponentRedy;
	boolean connected;
	boolean endGame;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		player1Progress=0;
		player2Progress=0;
		player1SeekBar = (SeekBar)findViewById(R.id.seekBar1);
		player2SeekBar = (SeekBar)findViewById(R.id.seekBar2);
		try
		{
			sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
			sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
			sensivity = 12;
			mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
			if(mBluetoothAdapter == null)
			{
				throw(new Exception("bluetooth not supported"));				
			}
			if(!mBluetoothAdapter.isEnabled())
			{
				throw(new Exception("bluetooth is ont enabled"));
			}
			if(mBluetoothAdapter.isEnabled())
			{
				Toast.makeText(getApplicationContext(), "Address " + mBluetoothAdapter.getAddress() + "Name: " + mBluetoothAdapter.getName() , Toast.LENGTH_SHORT).show();
			}
			else
			{
				Toast.makeText(getApplicationContext(), "Bluetooth not enabled you can't play", Toast.LENGTH_LONG).show();
			}
			mUUID = UUID.fromString(UUIDString);
			btStart = (Button)findViewById(R.id.button1);
			if(mMAC.equals(mBluetoothAdapter.getAddress().toString()))
			{
				isServer = true;
			}
			else
			{
				isServer = false;
			}
		
		}
		catch(Exception ex)
		{
			Toast.makeText(getApplicationContext(), ex.toString() , Toast.LENGTH_SHORT).show();
			Log.d(TAG, ex.toString());
			finish();
		}
		btStart.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				try
				{
					if(start == false)
					{
						if(endGame == true)
						{
							player1Progress = 0;
							player2Progress = 0;
							player1SeekBar.setProgress(player1Progress);
			            	player2SeekBar.setProgress(player2Progress);
						}
						start = true;
						if(isServer == true && ( btServer != null ) )
						{
							btServer.write((""+1000).getBytes());
						}
						if(isServer == false && btClient != null)
						{
							btClient.write((""+1000).getBytes());
						}
					}
				}catch(Exception e)
				{
					Log.d(TAG,"start button " + e);
				}
			}
			
			
		});
		((Button)findViewById(R.id.button2)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) 
			{
				Toast.makeText(getBaseContext(), "wait for connection", Toast.LENGTH_LONG);
				try
				{
					if(isServer == true)
					{
						if(btServer == null)
						{
							
						
							isServer = true;
							btServer = new BTServer(mUUID, mBluetoothAdapter, mHandler);
							btServer.start();
						}
						
					}
					else
					{
						if(btClient == null)
						{
							BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(mMAC);
							btClient = new BTClient(device, mUUID, mBluetoothAdapter,mHandler);
							isServer = false;
							btClient.start();
						}
						
					}
					
				}catch(Exception e)
				{
					Toast.makeText(getApplicationContext(), "cannot set connection " + e, Toast.LENGTH_LONG);
				}
				
				Toast.makeText(getApplicationContext(), "connected", Toast.LENGTH_SHORT);
				
			}
		});
	}

	@Override
	protected void onPause() {
		if (btClient != null) { btClient.cancel(); btClient = null; }
		if (btServer != null) { btServer.cancel(); btServer = null; }
		
		super.onPause();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
	

	@Override
	protected void onDestroy() {
		if (btClient != null) { btClient.cancel(); btClient = null; }
		if (btServer != null) { btServer.cancel(); btServer = null; }
		super.onDestroy();
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
		{
			if(start == true && opponentRedy == true)
			{
				double ax = event.values[0];
				double bx = event.values[1];
				double cx = event.values[2];
				
				sum = ax+bx+cx;
				int diff = (int)Math.abs(sum-oldsum);
				
				if(diff>sensivity)
				{
					player1Progress += (int)(sum/5);
					player1SeekBar.setProgress(player1Progress);
					oldsum = sum;
					if(isServer == true)
					{
						btServer.write((""+player1Progress).getBytes());
					}
					else
					{
						btClient.write((""+player1Progress).getBytes());
					}
					checkResults();
				}
			}
		}
		
	}

	private void checkResults() {
		if(player1Progress >= 100)
		{
			Toast.makeText(getApplicationContext(), "You win", Toast.LENGTH_LONG);
			start = false;
		}
		if(player2Progress >= 100)
		{
			Toast.makeText(getApplicationContext(), "You win", Toast.LENGTH_LONG);
			start = false;
		}			
	}
	private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MESSAGE_READ:
                byte[] readBuf = (byte[]) msg.obj;
                // construct a string from the valid bytes in the buffer
                String readMessage = new String(readBuf, 0, msg.arg1);
                Log.d("Handler ", "Message " + readMessage);
                if(Integer.parseInt(readMessage) == 1000)
                {
                	opponentRedy = true;
                }
                if(Integer.parseInt(readMessage) == 1001)
                {
                	opponentRedy = false;
                	start = false;

                }
                else if(Integer.parseInt(readMessage) < 200)
                {
	                player2Progress = Integer.parseInt(readMessage);
	                player2SeekBar.setProgress(player2Progress);
	                checkResults();
	                break;
                }
        
            }
        }

		private void checkResults() {
			
			if(player1Progress > 95 || player2Progress > 95)
			{
				endGame = true;
				start = false;
				if(isServer == true)
				{
					//btServer.write("1001".getBytes());
					btServer.write("0".getBytes());
				}
				else
				{
					//btClient.write("1001".getBytes());
					btClient.write("0".getBytes());
				}
				opponentRedy = false;
				Log.d(TAG,"end game");
			}
			if(player1Progress >= 90)
			{
				Toast.makeText(getApplicationContext(), "You win", Toast.LENGTH_LONG);
				Log.d(TAG,"player 1 win");
			}
			if(player2Progress >= 90)
			{
				Toast.makeText(getApplicationContext(), "You win", Toast.LENGTH_LONG);
				Log.d(TAG,"player 2 win");
				
			}			
		}
    };


}
