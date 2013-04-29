package com.example.blootothgame;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.text.style.EasyEditSpan;
import android.util.Log;

public class BTServer extends Thread {
	private final static String TAG = "BTServer";
	private BluetoothServerSocket mServerSocket;
	private BluetoothSocket mSocket;
	private final BluetoothAdapter mBluetoothAdapter;
	private InputStream inputStream;
	private OutputStream outputStream;
	private final Handler mHandler;
	private boolean connected;
	public BTServer(UUID mUUID, BluetoothAdapter mBluetoothAdapter, Handler handler) throws Exception {
		Log.d(TAG,"btServer constructior");
		mHandler = handler;
		this.mBluetoothAdapter = mBluetoothAdapter;
		BluetoothServerSocket tmp = null;;
		try {
			mServerSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("BluetoothGame", mUUID);
		} catch (IOException e) {
			Log.d(TAG,"creating socket problem");
			e.printStackTrace();
		}
	}
	
	public void run()
	{
		try
		{
			Log.d(TAG, "run method");
			mSocket = mServerSocket.accept();		
		
				inputStream = mSocket.getInputStream();
				outputStream = mSocket.getOutputStream();
			connected = true;
			Log.d(TAG,"run method");
			byte[] buffer = new byte[1024];
			int bytes;
			while(true)
			{
				try
				{
					bytes = inputStream.read(buffer);
					
	                mHandler.obtainMessage(MainActivity.MESSAGE_READ, bytes, -1, buffer)
	                .sendToTarget();
					
				}catch(IOException ex)
				{
					Log.d(TAG, " problem with reading input buffer" + ex);
					break;
				}
				
			}
			Log.d(TAG,"connected");
			try {
				mSocket.close();
				connected = false;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}catch(Exception e)
		{
			Log.d(TAG,"problem with run" + e);
		}
	}
	
	public void write(byte[] bytes)
	{
		Log.d(TAG,"Write");
		try {
			outputStream.write(bytes);
		} catch (IOException e) {
			Log.d(TAG,"problem with write");
		}
	}
	
	public void cancel()
	{
		try
		{
			mServerSocket.close();
			connected = false;
		}catch(Exception ex)
		{
			Log.d(TAG, "problem with closing socket");
		}
	}
	public boolean getConnected()
	{
		return connected;
	}

}
