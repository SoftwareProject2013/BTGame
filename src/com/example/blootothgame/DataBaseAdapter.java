package com.example.blootothgame;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.TextView;

import com.example.blootothgame.R.string;

public class DataBaseAdapter extends SimpleCursorAdapter {

	public final static String [] FROM = {MyDataBase.OPPONENT_ID, MyDataBase.CREATED_AT , MyDataBase.WINNER };
	public final static int [] TO = { R.id.winner , R.id.textCreatedAt, };
	public DataBaseAdapter(Context context, Cursor c) {
		super(context,R.layout.row, c,FROM,TO);
		// TODO Auto-generated constructor stub
	}
	@Override
	public void bindView(View row, Context context, Cursor cursor) {
		// TODO Auto-generated method stub
		super.bindView(row, context, cursor);
		
		long timestamp = cursor.getLong(cursor.getColumnIndex(MyDataBase.CREATED_AT));
		TextView textCreatedAt = (TextView) row.findViewById(R.id.textCreatedAt);
		textCreatedAt.setText(DateUtils.getRelativeTimeSpanString(timestamp));
		TextView scoreText = (TextView) row.findViewById(R.id.winner);
		int winner = cursor.getInt(cursor.getColumnIndex(MyDataBase.WINNER));
		String MAC = cursor.getString(cursor.getColumnIndex(MyDataBase.OPPONENT_ID));
		if(winner == 1)
		{
			scoreText.setText("You won against: " + MAC);
		}
		else
		{
			scoreText.setText("You loose against: " + MAC);
		}

	}

}
