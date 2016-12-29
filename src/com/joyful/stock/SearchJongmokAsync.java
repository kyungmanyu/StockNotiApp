package com.joyful.stock;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import android.content.Context;
import android.util.Log;

public class SearchJongmokAsync {

	private String jongmokHeader = "https://m.search.daum.net/search?w=tot&q=";
	private String jongmokRear = "&nil_profile=rcmkwd&rtmaxcoll=1CI&pin=stock&DA=11M";
	String compare1 = "<span class=\"f_sub_s\">";
	String compare2 = "<span class=\"txt_sub\">";
	URL url = null;
	String jongmokName;
	HttpURLConnection conn = null;
	BufferedReader br = null;
	String result;
	String stockCode;
	SearchCallback sCallback;
	Context contxt;

	public SearchJongmokAsync(Context context, String name, SearchCallback callback) {
		// TODO Auto-generated constructor stub
		contxt = context;
		jongmokName = name;
		sCallback = callback;
		Thread getJongmokCode = new Thread(new getJongmok());
		getJongmokCode.start();
	}

	class getJongmok implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub

			Log.e("test", "url = " + url);

			try {
				url = new URL(jongmokHeader + URLEncoder.encode(jongmokName, "UTF-8") + jongmokRear);

				conn = (HttpURLConnection) url.openConnection();

				conn.setConnectTimeout(1000);
				conn.setReadTimeout(1000);
				conn.setRequestMethod("GET");
				conn.setAllowUserInteraction(false);
				conn.setRequestProperty("content-type", "text/plain; charset=UTF-8");
				conn.setUseCaches(false);

				InputStream in = new BufferedInputStream(conn.getInputStream());
				Log.e("test", "dom in.toString : " + conn.getInputStream());

				if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
					// Log.d("", "[Test] conn.. :: HTTP_OK..");
					br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
					for (;;) {
						String line = br.readLine();
						Log.d("", "line : " + line);
						if (line.contains("f_sub_s")) {
							String[] comparemain = line.split(compare1);

							String[] comparesub = comparemain[1].split(compare2);
							stockCode = comparesub[0];
							break;
						}
					}
					jongmokList.setStockItem(contxt, jongmokName, stockCode);
					Log.e("stock", "searchJongmok name : " + jongmokName);
					Log.e("stock", "searchJongmok stockCode : " + stockCode);
					if (sCallback != null) {
						sCallback.updateJongmok(jongmokName);
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

}
