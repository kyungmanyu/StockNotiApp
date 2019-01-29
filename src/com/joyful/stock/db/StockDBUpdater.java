package com.joyful.stock.db;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

/**
 * Created by kyungman.yu on 2014-09-26.
 */

public class StockDBUpdater {

	private static final String TAG = "StockDBUpdater";

	public static void insertJongMok(ContentResolver mContentResolver, HashMap<String, String> productList) {

		Set<String> keyset = productList.keySet();
		synchronized (productList) {
			for (String key : keyset) {

				ContentValues values = new ContentValues();

				values.put(StockDBConstant.NAME, key);
				values.put(StockDBConstant.CODE, productList.get(key));

				mContentResolver.insert(StockDBConstant.JONGMOK_URI, values);

				values.clear();
			}
		}

	}

	public static void updateJongMok(ContentResolver mContentResolver, String name, String code) {

		ContentValues values = new ContentValues();

		values.put(StockDBConstant.NAME, name);
		values.put(StockDBConstant.CODE, code);
		if (!checkJongMok(mContentResolver, code)) {
			mContentResolver.insert(StockDBConstant.JONGMOK_URI, values);
		}

	}

	public static boolean checkJongMok(ContentResolver mContentResolver, String code) {

		String name = null;
		String selection = StockDBConstant.CODE + " = " + "\"" + code + "\"";

		String[] queryField;
		Cursor cursor;

		queryField = new String[] { StockDBConstant.NAME };
		cursor = mContentResolver.query(StockDBConstant.JONGMOK_URI, queryField, selection, null, null);
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				name = cursor.getString(cursor.getColumnIndex(StockDBConstant.NAME));
				return true;
			}
			cursor.close();
		}
		return false;
	}

	public static void insertJongMokServer(ContentResolver mContentResolver, String name, String price, String lowprice,
			String highprice, String sellday, String sellstep) {

		ContentValues values = new ContentValues();

		values.put(StockDBConstant.NAMESERVER, name);
		values.put(StockDBConstant.PRICE, price);
		values.put(StockDBConstant.LOWPRICE, lowprice);
		values.put(StockDBConstant.HIGHPRICE, highprice);
		values.put(StockDBConstant.SELLDAY, sellday);
		values.put(StockDBConstant.SELLSTEP, sellstep);

		mContentResolver.insert(StockDBConstant.JONGMOK_SERVER_URI, values);

	}

	public static ArrayList<String> getJongMokServer(ContentResolver mContentResolver) {

		ArrayList<String> jongmoklist = new ArrayList<String>();

		String[] queryField = new String[] { StockDBConstant.NAMESERVER };
		Cursor cursor = mContentResolver.query(StockDBConstant.JONGMOK_SERVER_URI, queryField, null, null, null);

		if (cursor != null) {
			if (cursor.moveToFirst()) {
				do {

					jongmoklist.add(cursor.getString(cursor.getColumnIndex(StockDBConstant.NAMESERVER)));
				} while (cursor.moveToNext());
			}
			cursor.close();
		}

		return jongmoklist;
	}

	public static String getbuyPriceServer(ContentResolver mContentResolver, String jongmok) {

		String price = null;
		String selection = StockDBConstant.NAMESERVER + " = " + "\"" + jongmok + "\"";

		String[] queryField;
		Cursor cursor;

		queryField = new String[] { StockDBConstant.PRICE };
		cursor = mContentResolver.query(StockDBConstant.JONGMOK_SERVER_URI, queryField, selection, null, null);
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				price = cursor.getString(cursor.getColumnIndex(StockDBConstant.PRICE));
			}
			cursor.close();
		}

		return price;
	}

	public static String gethighPriceServer(ContentResolver mContentResolver, String jongmok) {

		String price = null;
		String selection = StockDBConstant.NAMESERVER + " = " + "\"" + jongmok + "\"";

		String[] queryField;
		Cursor cursor;

		queryField = new String[] { StockDBConstant.HIGHPRICE };
		cursor = mContentResolver.query(StockDBConstant.JONGMOK_SERVER_URI, queryField, selection, null, null);
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				price = cursor.getString(cursor.getColumnIndex(StockDBConstant.HIGHPRICE));
			}
			cursor.close();
		}

		return price;
	}

	public static String getlowPriceServer(ContentResolver mContentResolver, String jongmok) {

		String price = null;
		String selection = StockDBConstant.NAMESERVER + " = " + "\"" + jongmok + "\"";

		String[] queryField;
		Cursor cursor;

		queryField = new String[] { StockDBConstant.LOWPRICE };
		cursor = mContentResolver.query(StockDBConstant.JONGMOK_SERVER_URI, queryField, selection, null, null);
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				price = cursor.getString(cursor.getColumnIndex(StockDBConstant.LOWPRICE));
			}
			cursor.close();
		}

		return price;
	}

	public static String getdayServer(ContentResolver mContentResolver, String jongmok) {

		String price = null;
		String selection = StockDBConstant.NAMESERVER + " = " + "\"" + jongmok + "\"";

		String[] queryField;
		Cursor cursor;

		queryField = new String[] { StockDBConstant.SELLDAY };
		cursor = mContentResolver.query(StockDBConstant.JONGMOK_SERVER_URI, queryField, selection, null, null);
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				price = cursor.getString(cursor.getColumnIndex(StockDBConstant.SELLDAY));
			}
			cursor.close();
		}

		return price;
	}

	public static String getstepServer(ContentResolver mContentResolver, String jongmok) {

		String price = null;
		String selection = StockDBConstant.NAMESERVER + " = " + "\"" + jongmok + "\"";

		String[] queryField;
		Cursor cursor;

		queryField = new String[] { StockDBConstant.SELLSTEP };
		cursor = mContentResolver.query(StockDBConstant.JONGMOK_SERVER_URI, queryField, selection, null, null);
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				price = cursor.getString(cursor.getColumnIndex(StockDBConstant.SELLSTEP));
			}
			cursor.close();
		}

		return price;
	}

	public static void removeDBtable(Context context, String jongmok) {
		// TODO Auto-generated method stub
		ContentResolver Resolver = context.getContentResolver();

		String selection = StockDBConstant.NAMESERVER + " = " + "\"" + jongmok + "\"";
		Resolver.delete(StockDBConstant.JONGMOK_SERVER_URI, selection, null);

	}

	//
	//
	// public static void removeDBtable(Context context, String mPackageName) {
	// // TODO Auto-generated method stub
	// ContentResolver triggerResolver = context.getContentResolver();
	//
	// String selection = StockDBConstant.PACKAGE + " = " + "\"" + mPackageName
	// + "\"";
	// triggerResolver.delete(StockDBConstant.TRIGGER_URI, selection, null);
	// triggerResolver.delete(StockDBConstant.INGREDIANTS_URI, selection, null);
	// triggerResolver.delete(StockDBConstant.TRIGGERFIELD_URI, selection,
	// null);
	// triggerResolver.delete(StockDBConstant.TEXTFIELD_URI, selection, null);
	// triggerResolver.delete(StockDBConstant.DROPDOWNFIELD_URI, selection,
	// null);
	// triggerResolver.delete(StockDBConstant.LOCATIONFIELD_URI, selection,
	// null);
	//
	// }
}
