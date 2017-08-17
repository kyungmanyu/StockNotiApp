/* 
 * Application Laboratory, Mobile Communication Company, LG ELECTRONICS INC., SEOUL, KOREA 
 * Copyright(c) 2015 by LG Electronics Inc. 
 * 
 * All rights reserved. No part of this work may be reproduced, stored in a 
 * retrieval system, or transmitted by any means without prior written 
 * Permission of LG Electronics Inc. 
 */

package com.joyful.stock;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import com.joyful.stock.db.StockDBUpdater;

public class ServerProfitAdapter extends BaseAdapter {

	private LayoutInflater mLayoutInflater;
	private TextView mStockName;
	private TextView buyprice;
	private TextView sellprice;
	private TextView profit;
	private TextView buyday;
	private TextView sellday;
	private TextView period;
	private TextView totalprofit;

	private ArrayList<String> mCodeArray = new ArrayList<>();
	private Map<String, ArrayList<String>> mListMap = new HashMap<>();
	private ArrayList<List> mJArray;
	private Context mContext;

	public ServerProfitAdapter(Context context, List list) {
		mLayoutInflater = LayoutInflater.from(context);
		mJArray = (ArrayList<List>) list;
		mContext = context;
		// Set keyset = mListMap.keySet();
		// Log.e("StockItemAdapter", "mListMap : " + mListMap);
		// Iterator<String> ir = keyset.iterator();
		// while (ir.hasNext()) {
		// mCodeArray.add(ir.next());
		// }

		Log.e("kyungman", "list = " + mJArray.size());
	}

	@Override
	public int getCount() {
		return mJArray.size();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View itemLayout = mLayoutInflater.inflate(R.layout.server_profit_item, null);

		mStockName = (TextView) itemLayout.findViewById(R.id.Stock_name);
		buyprice = (TextView) itemLayout.findViewById(R.id.buyprice_value);
		sellprice = (TextView) itemLayout.findViewById(R.id.sellprice_value);
		profit = (TextView) itemLayout.findViewById(R.id.profit_value);
		buyday = (TextView) itemLayout.findViewById(R.id.buyday_value);
		sellday = (TextView) itemLayout.findViewById(R.id.sellday_value);
		period = (TextView) itemLayout.findViewById(R.id.preiod_value);
		// totalprofit = (TextView) itemLayout.findViewById(R.id.total_value);

		try {
			ArrayList<String> obj = new ArrayList<>();
			obj = (ArrayList<String>) mJArray.get(position);
			//
			// String codeNum = mCodeArray.get(position);
			//
			mStockName.setText(obj.get(0));

			buyprice.setText(obj.get(1));
			sellprice.setText(obj.get(2));
			profit.setText(obj.get(3));
			buyday.setText(obj.get(4));
			sellday.setText(obj.get(5));
			period.setText(obj.get(6));
			// totalprofit.setText(obj.get(7));
			// if (mListMap.get(codeNum) != null) {
			//
			// double buyPrice =
			// Integer.parseInt(StockDBUpdater.getbuyPriceServer(mContext.getContentResolver(),
			// jongmokList.getStockName(codeNum)));
			// double currentPrice =
			// Integer.parseInt(mListMap.get(codeNum).get(0));
			// double comparevalue =
			// Double.parseDouble(mListMap.get(codeNum).get(1));
			// mCurrentPrice.setText(mListMap.get(mCodeArray.get(position)).get(0));
			// Log.e("jusik", "jusik (currentPrice -uyPrice) :" + (currentPrice
			// - buyPrice));
			//
			// Log.e("jusik",
			// "jusik ((currentPrice - buyPrice) / buyPrice) :" + ((currentPrice
			// - buyPrice) / buyPrice));
			// double profitValue = ((currentPrice - buyPrice) / buyPrice) *
			// 100;
			// Log.e("jusik", "jusik profitValue :" + profitValue);
			// mProfit.setText(String.format("%.2f", profitValue));
			// mComparebefore.setText(mListMap.get(codeNum).get(1));
			// if (comparevalue > 0) {
			// mCurrentPrice.setTextColor(mContext.getResources().getColor(R.color.color_red));
			// mComparebefore.setTextColor(mContext.getResources().getColor(R.color.color_red));
			// } else {
			// mCurrentPrice.setTextColor(mContext.getResources().getColor(R.color.color_blue));
			// mComparebefore.setTextColor(mContext.getResources().getColor(R.color.color_blue));
			// }
			//
			// if (profitValue > 0) {
			// mBuyprice.setTextColor(mContext.getResources().getColor(R.color.color_red));
			// mProfit.setTextColor(mContext.getResources().getColor(R.color.color_red));
			// } else {
			// mBuyprice.setTextColor(mContext.getResources().getColor(R.color.color_blue));
			// mProfit.setTextColor(mContext.getResources().getColor(R.color.color_blue));
			// }
			//
			// }
		} catch (Exception ex) {

			// TODO Auto-generated catch block
			Log.e("test", "exception !");

			ex.printStackTrace();

		}

		return itemLayout;
	}

	@Override
	public String getItem(int position) {
		// TODO Auto-generated method stub

		return mCodeArray.get(position);
	}
}
