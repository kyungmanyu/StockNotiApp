package com.joyful.stock.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class StockDBopenHelpler extends SQLiteOpenHelper {

	private static final String CREATE_TABLE_JONGMOK = "CREATE TABLE " + StockDBConstant.JONGMOK_TABLE_NAME + "("
			+ StockDBConstant.ID + " INTEGER PRIMARY KEY autoincrement," + StockDBConstant.NAME + " TEXT,"
			+ StockDBConstant.CODE + " TEXT" + ")";

	public StockDBopenHelpler(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(CREATE_TABLE_JONGMOK);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
