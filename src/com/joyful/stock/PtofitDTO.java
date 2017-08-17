package com.joyful.stock;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class PtofitDTO {

	JSONArray mJArry;
	ArrayList<List> mList = new ArrayList<>();
	String mTotalProfit;
	int mTotalJonmok;

	public PtofitDTO(JSONArray jary) {
		mJArry = jary;
		mTotalJonmok = mJArry.length();
		makeTenArray();
	}

	private void makeTenArray() {
		// TODO Auto-generated method stub
		
		JSONObject obj;
		String total;
		for (int i = mJArry.length() - 1; i > (mJArry.length() - 11); i--) {
			try {
				ArrayList<String> list = new ArrayList<>();
				obj = mJArry.getJSONObject(i);
				list.add(obj.getString("name"));

				list.add(obj.getString("buyprice"));
				list.add(obj.getString("sellprice"));
				list.add(obj.getString("profit"));
				list.add(obj.getString("buyday"));
				list.add(obj.getString("sellday"));
				list.add(obj.getString("period"));
				total = obj.getString("totalprofit");
				if (i == mTotalJonmok) {
					mTotalProfit = total;
				}
				list.add(total);
			
				mList.add(list);
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		
			
		}

	}

	public List getTenArray() {

		return mList;
	}

}
