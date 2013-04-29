package com.example.blootothgame;




import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MyDataBase {
	public static final String MYDATABASE_NAME = "BT_DATABASE";
	public static final String MYDATABASE_TABLE = "SCORE";
	public static final String KEY_ID = "_id";
	public static final String OPPONENT_ID = "opponent_id";
	public static final String WINNER ="winner";
	public static final int MYDATABASE_VERSION = 4;
	public static final String CREATED_AT ="CREATED_AT";
	
	public static final String CREATE_SQL_DB = "create table " 
	+ MYDATABASE_TABLE + " (" + KEY_ID + " integer primary key autoincrement, "
	+ OPPONENT_ID + " text not null, " 
	+ WINNER + " int, " 
	+ CREATED_AT + " int);";
	
	private SQLiteHelper DBHelper;
	private SQLiteDatabase db;
	
	private Context context;
	
	public MyDataBase(Context context)
	{
		this.context = context;
	}
	
	public MyDataBase openToRead()
	{
		DBHelper  = new SQLiteHelper(context, MYDATABASE_NAME, null, MYDATABASE_VERSION);
		db = DBHelper.getReadableDatabase();
		return this;
	}
	
	public MyDataBase openToWrite()
	{
		DBHelper  = new SQLiteHelper(context, MYDATABASE_NAME, null, MYDATABASE_VERSION);
		db = DBHelper.getWritableDatabase();
		return this;
	}
	public void clearDB()
	{
		db.delete(MYDATABASE_NAME,null,null);
	}
	
	public void close()
	{
		DBHelper.close();
	}
	
	public void insert(String MAC, int myScore)
	{
		try
		{
			ContentValues cv = new ContentValues();
			cv.put(OPPONENT_ID, MAC);
			cv.put(WINNER, myScore);
			long myTime = new Date().getTime();
			cv.put(CREATED_AT, myTime);
			db.insertOrThrow(MYDATABASE_TABLE, null, cv);
		}
		catch(Exception e)
		{
			Log.d("insert ","insert" + e);
		}
	}
	
	public Cursor getAllRecords()
	{
		String[] columns = new String[] { KEY_ID,OPPONENT_ID, WINNER , CREATED_AT};
		return db.query(MYDATABASE_TABLE, columns, null, null,null,null,null);
	}
	
	public class SQLiteHelper extends SQLiteOpenHelper {

		public SQLiteHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_SQL_DB);
			
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("drop table " + MYDATABASE_TABLE);
			this.onCreate(db);
		}
		
	}
	

}
