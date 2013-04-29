package com.example.blootothgame;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

public class BTClient extends Thread {
	private final BluetoothAdapter mAdapter;
	private final BluetoothSocket mSocket;
	private final static String TAG = "BTCLient";
	private InputStream inputStream;
	private OutputStream outputStream;
	private final Handler mHandler;
	BTClient( BluetoothDevice device, UUID mUUID, BluetoothAdapter mAdapter, Handler handler)
	{
		Log.d("BtClient", "constructor");
		BluetoothSocket tmp = null;
		mHandler = handler;
		this.mAdapter = mAdapter;
		
			try {
				tmp = device.createInsecureRfcommSocketToServiceRecord(mUUID);
			} catch (IOException e) {
				Log.d(TAG, "error while trying connection");
				e.printStackTrace();
			}
		mSocket = tmp;
		try
		{
			inputStream = mSocket.getInputStream();
			outputStream = mSocket.getOutputStream();
		}catch(IOException ex)
		{
			Log.d(TAG,"geting input string error" + ex);
		}
		mAdapter.cancelDiscovery();
		try
		{
			mSocket.connect();
			
		}catch(IOException ex)
		{
			try {
				Log.d(TAG, " run exeption " + ex);
				mSocket.close();
			} catch (IOException e) {
				Log.d(TAG,"during closing mSocket");
				e.printStackTrace();
			}
		}
		
	}
	
	public void run()
	{
		Log.d("BtClient", "run method");
	
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
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void write(byte[] bytes)
	{
		Log.d(TAG,"Write");
		try {
			outputStream.write(bytes);
            
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.d(TAG,"problem with write");
		}
	}
	
	
	
	public void cancel()
	{
		try {
			mSocket.close();
		} catch (IOException e) {
			Log.d(TAG,"closing error");
			e.printStackTrace();
		}
	}
}
