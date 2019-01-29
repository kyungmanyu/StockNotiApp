/** @author kyungman.yu  2015. 7. 8. **/
package com.joyful.stock;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import com.joyful.stock.db.StockDBConstant;
import com.joyful.stock.db.StockDBUpdater;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class jongmokList {

	// ArrayList for Listview
	static HashMap<String, String> productList = new HashMap<>();
	static HashMap<String, String> productTitlesMap = new HashMap<>();
	static ArrayList<String> products = new ArrayList<>();
	static ArrayList<String> productItems = new ArrayList<>();;

	public static ArrayList<String> getList(Context context) {

		if (Util.getDeviceImei(context) != null) {

			initialDB(context.getContentResolver());
		} else {
			if (products.size() < 1 && !Util.getDBUpdateFlag(context)) {
				Log.e("jongmoklist", "setList");
				setList(context);
			}

			if (products.size() < 1 && Util.getDBUpdateFlag(context)) {
				Log.e("jongmoklist", "initial DB");
				initialDB(context.getContentResolver());
			}
		}
		return products;
	}

	public static void addStockCode(Context context, String stockItem) {
		Set<String> stockCodes = new LinkedHashSet<>();
		Set<String> newstockCodes = new LinkedHashSet<>();
		if (Util.getStockCodes(context) != null) {
			stockCodes = Util.getStockCodes(context);
		}

		newstockCodes.add(stockItem);
		newstockCodes.addAll(stockCodes);
		Log.d("addStockCode", "add codelist : " + stockCodes.toString());
		Util.setStockCodes(context, newstockCodes);
	}

	public static void removeStockCode(Context context, String stockItem) {
		Set<String> stockCodes = new LinkedHashSet<>();
		if (Util.getStockCodes(context) != null) {
			stockCodes = Util.getStockCodes(context);
		}

		stockCodes.remove(stockItem);
		Log.d("removeStockCode", "remove codelist : " + stockCodes.toString());
		Util.setStockCodes(context, stockCodes);
		Log.d("removeStockCode", "check codelist : " + Util.getStockCodes(context).toString());
	}

	public static String getStockItem(String stockName) {

		return productList.get(stockName);
	}

	public static void setStockItem(Context context, String stockName, String stockCode) {

		synchronized (productList) {
			productList.put(stockName, stockCode);
			products.add(stockName);
			updateList(context, stockName, stockCode);
		}
	}

	public static String getStockName(String stockCode) {

		return productTitlesMap.get(stockCode);
	}

	public static void initialDB(ContentResolver resolve) {

		String[] queryField = new String[] { StockDBConstant.NAME, StockDBConstant.CODE };
		Cursor cursor = resolve.query(StockDBConstant.JONGMOK_URI, queryField, null, null, null);

		products.clear();
		productItems.clear();

		productList.clear();
		productTitlesMap.clear();

		if (cursor != null && cursor.moveToFirst()) {
			do {
				String name = cursor.getString(cursor.getColumnIndex(StockDBConstant.NAME));
				String code = cursor.getString(cursor.getColumnIndex(StockDBConstant.CODE));

				products.add(name);
				productItems.add(code);

				productList.put(name, code);
				productTitlesMap.put(code, name);

			} while (cursor.moveToNext());
			cursor.close();
		}

	}

	public static void updateList(Context context, String name, String code) {
		StockDBUpdater.updateJongMok(context.getContentResolver(), name, code);
		initialDB(context.getContentResolver());
	}

	public static void setList(Context context) {

		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(context.getAssets().open("stock.TXT"), "euc-kr"));

			// do reading, usually loop until end of file reading
			String mLine = reader.readLine();
			String splitToken[];
			Log.e("stock", "mLine : " + mLine);

			while (mLine != null) {
				// process line
				if (mLine != null) {
					splitToken = mLine.split("//");
					products.add(splitToken[1]);
					productItems.add(splitToken[0]);

					productList.put(splitToken[1], splitToken[0]);
					productTitlesMap.put(splitToken[0], splitToken[1]);
				}
				mLine = reader.readLine();

			}

		} catch (IOException e) {
			// log the exception
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					// log the exception
					e.printStackTrace();
				}
			}
		}

		StockDBUpdater.insertJongMok(context.getContentResolver(), productList);
		Util.setDBUpdateFlag(context, true);

	}
}
