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
import java.util.Map;
import java.util.Set;

import com.joyful.stock.db.StockDBUpdater;

public class ServerStockItemAdapter extends BaseAdapter {

	private LayoutInflater mLayoutInflater;
	private TextView mStockName;
	private TextView mCurrentPrice;
	private TextView mProfit;
	private TextView mBuyprice;
	private TextView mComparebefore;
	private ArrayList<String> mCodeArray = new ArrayList<>();
	private Map<String, ArrayList<String>> mListMap = new HashMap<>();
	private Context mContext;

	public ServerStockItemAdapter(Context context, Map<String, ArrayList<String>> list) {
		mLayoutInflater = LayoutInflater.from(context);
		mListMap = list;
		mContext = context;
		Set keyset = mListMap.keySet();
		Log.e("StockItemAdapter", "mListMap : " + mListMap);
		Iterator<String> ir = keyset.iterator();
		while (ir.hasNext()) {
			mCodeArray.add(ir.next());
		}

	}

	@Override
	public int getCount() {
		return mListMap.size();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View itemLayout = mLayoutInflater.inflate(R.layout.stock_item, null);
		Log.e("jusik", "jusik mCodeArray :" + mCodeArray.size());
		mStockName = (TextView) itemLayout.findViewById(R.id.Stock_name);
		mCurrentPrice = (TextView) itemLayout.findViewById(R.id.current_value);
		mProfit = (TextView) itemLayout.findViewById(R.id.profit_value);
		mComparebefore = (TextView) itemLayout.findViewById(R.id.comparebefore_value);
		mBuyprice = (TextView) itemLayout.findViewById(R.id.buyprice_value);

		try {
			String codeNum = mCodeArray.get(position);

			mStockName.setText(jongmokList.getStockName(codeNum));

			mBuyprice.setText(
					StockDBUpdater.getbuyPriceServer(mContext.getContentResolver(), jongmokList.getStockName(codeNum)));

			if (mListMap.get(codeNum) != null) {

				double buyPrice = Integer.parseInt(StockDBUpdater.getbuyPriceServer(mContext.getContentResolver(),
						jongmokList.getStockName(codeNum)));
				double currentPrice = Integer.parseInt(mListMap.get(codeNum).get(0));
				double comparevalue = Double.parseDouble(mListMap.get(codeNum).get(1));
				mCurrentPrice.setText(mListMap.get(mCodeArray.get(position)).get(0));
				Log.e("jusik", "jusik (currentPrice -uyPrice) :" + (currentPrice - buyPrice));

				Log.e("jusik",
						"jusik ((currentPrice - buyPrice) / buyPrice) :" + ((currentPrice - buyPrice) / buyPrice));
				double profitValue = ((currentPrice - buyPrice) / buyPrice) * 100;
				Log.e("jusik", "jusik profitValue :" + profitValue);
				mProfit.setText(String.format("%.2f", profitValue));
				mComparebefore.setText(mListMap.get(codeNum).get(1));
				if (comparevalue > 0) {
					mCurrentPrice.setTextColor(mContext.getResources().getColor(R.color.color_red));
					mComparebefore.setTextColor(mContext.getResources().getColor(R.color.color_red));
				} else {
					mCurrentPrice.setTextColor(mContext.getResources().getColor(R.color.color_blue));
					mComparebefore.setTextColor(mContext.getResources().getColor(R.color.color_blue));
				}

				if (profitValue > 0) {
					mBuyprice.setTextColor(mContext.getResources().getColor(R.color.color_red));
					mProfit.setTextColor(mContext.getResources().getColor(R.color.color_red));
				} else {
					mBuyprice.setTextColor(mContext.getResources().getColor(R.color.color_blue));
					mProfit.setTextColor(mContext.getResources().getColor(R.color.color_blue));
				}

			}
		} catch (Exception ex) {
			try {
				throw new IllegalArgumentException();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.e("test", "exception !");
				Toast.makeText(mContext, "주식 시장이 휴장 입니다...", Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}
		}

		return itemLayout;
	}

	@Override
	public String getItem(int position) {
		// TODO Auto-generated method stub

		return mCodeArray.get(position);
	}
}
