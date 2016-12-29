package com.joyful.stock.db;

import java.util.HashMap;
import java.util.Set;

import android.content.ContentResolver;
import android.content.ContentValues;

/**
 * Created by kyungman.yu on 2014-09-26.
 */

public class StockDBUpdater {

	private static final String TAG = "StockDBUpdater";

	public static void insertJongMok(ContentResolver mContentResolver, HashMap<String, String> productList) {

		Set<String> keyset = productList.keySet();
		for (String key : keyset) {

			ContentValues values = new ContentValues();

			values.put(StockDBConstant.NAME, key);
			values.put(StockDBConstant.CODE, productList.get(key));

			mContentResolver.insert(StockDBConstant.JONGMOK_URI, values);

			values.clear();
		}

	}

	public static void updateJongMok(ContentResolver mContentResolver, String name, String code) {

		ContentValues values = new ContentValues();

		values.put(StockDBConstant.NAME, name);
		values.put(StockDBConstant.CODE, code);

		mContentResolver.insert(StockDBConstant.JONGMOK_URI, values);

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
