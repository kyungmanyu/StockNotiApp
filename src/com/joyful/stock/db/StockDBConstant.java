package com.joyful.stock.db;

import android.net.Uri;

public class StockDBConstant {

	public static final String AUTHORITY = "com.joyful.stock.provider";

	public static final String JONGMOK_TABLE_NAME = "jongmoktable";
	public static final String ID = "_id";
	public static final String NAME = "name";
	public static final String CODE = "code";
	
	public static final String JONGMOK_TABLE_NAME_SERVER = "jongmoktableserver";
	
	public static final String NAMESERVER = "nameserver";
	public static final String PRICE = "price";
	public static final String LOWPRICE = "lowprice";
	public static final String HIGHPRICE = "highprice";
	public static final String SELLDAY = "sellday";
	public static final String SELLSTEP = "sellstep";

	public static final Uri JONGMOK_URI = Uri.parse("content://" + AUTHORITY + "/" + JONGMOK_TABLE_NAME);
	public static final Uri JONGMOK_SERVER_URI = Uri.parse("content://" + AUTHORITY + "/" + JONGMOK_TABLE_NAME_SERVER);
}
