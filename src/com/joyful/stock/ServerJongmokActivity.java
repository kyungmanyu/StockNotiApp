package com.joyful.stock;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.joyful.stock.db.StockDBUpdater;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class ServerJongmokActivity extends Activity implements GetCurrentPriceAsyncTaskServer.Callback {

	private ListView mStockList;
	private ServerStockItemAdapter mStockItemAdapter;
	private BroadcastReceiver mRefreshReceiver;
	private static final int PERMISSIONS_REQUEST_READ_PHONE_STATE = 10;
	private Handler mHandler;
	private AlertDialog mWaitingDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		this.setTitle("시스템 추천주");

		// Thread setList = new Thread(new Runnable() {
		//
		// @Override
		// public void run() {
		// // TODO Auto-generated method stub
		// jongmokList.setList(ServerJongmokActivity.this);
		// }
		// });
		// setList.start();

		Button btnlocal = (Button) findViewById(R.id.local);
		btnlocal.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(getApplicationContext(), MainActivity.class);
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
				overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
			}
		});
		Button btnprofit = (Button) findViewById(R.id.profit);
		btnprofit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(getApplicationContext(), Serverprofit.class);
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
				overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
			}
		});

		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub

				switch (msg.what) {

				case 1:
					updateStockItem();
					break;

				}

				super.handleMessage(msg);
			}
		};

	}

	class RefreshReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			updateStockItem();
		}

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub

		mRefreshReceiver = new RefreshReceiver();
		IntentFilter filter = new IntentFilter("com.lge.email.intent.action.ACTION_REFRESH");
		this.registerReceiver(mRefreshReceiver, filter);

		// final SwipeDetector swipeDetector = new SwipeDetector();
		mStockList = (ListView) findViewById(R.id.list_view);
		mStockList.setLongClickable(true);
		// mStockList.setOnTouchListener(swipeDetector);
		mStockList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub

				Intent editIntent = new Intent(ServerJongmokActivity.this, ServerStockDetailActivity.class);
				editIntent.putExtra("code_num", (String) parent.getAdapter().getItem(position));
				startActivity(editIntent);

			}
		});

//		getData data = new getData();
//		data.execute();

		 updateStockItem();

		super.onResume();

	}

	private void showWaitingDialog(String title, String message) {
		Log.i("", "[Test] showWaitingDialog!! ");
		try {
			ProgressDialog dlgProgress = new ProgressDialog(this);
			// dlgProgress.setTitle(title);
			dlgProgress.setMessage(message);
			dlgProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			dlgProgress.setCancelable(false);
			mWaitingDialog = dlgProgress;
			mWaitingDialog.show();
		} catch (Exception e) {
			e.printStackTrace();
			Log.d("", "[RemoveAccountTask] activity terminated");
		}
	}

	class getData extends AsyncTask<String, String, String> {

		HttpURLConnection urlConnection;

		@Override
		protected void onPreExecute() {

			// TODO Auto-generated method stub
			showWaitingDialog("", "서버 데이타 수신중...");
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... args) {

			// jongmokList.setList(ServerJongmokActivity.this);

			StringBuilder result = new StringBuilder();

			try {
				URL url = new URL("http://suah.iptime.org:9000/getjongmok");
				urlConnection = (HttpURLConnection) url.openConnection();
				InputStream in = new BufferedInputStream(urlConnection.getInputStream());

				BufferedReader reader = new BufferedReader(new InputStreamReader(in));

				String line;
				while ((line = reader.readLine()) != null) {
					result.append(line);
				}

				ArrayList<String> jongmoklist = StockDBUpdater
						.getJongMokServer(ServerJongmokActivity.this.getContentResolver());

				if (jongmoklist != null) {
					for (int i = 0; i < jongmoklist.size(); i++) {

						StockDBUpdater.removeDBtable(getApplicationContext(), jongmoklist.get(i));

					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				urlConnection.disconnect();
			}

			try {
				JSONArray JArry = new JSONArray(result.toString());
				for (int i = 0; i < JArry.length(); i++) {
					JSONObject jo = JArry.getJSONObject(i);
					Log.e("test", "JsonObject from server : " + jo.getString("jongmok"));
					String codenum = jongmokList.getStockItem(jo.getString("jongmok"));
					Log.e("test", "JsonObject jongmokcode from server : "
							+ jongmokList.getStockItem(jo.getString("jongmok")));
					if (codenum == null || !StockDBUpdater.checkJongMok(getContentResolver(), codenum)) {
						setStockItem(jo.getString("jongmok"));

					}

					StockDBUpdater.insertJongMokServer(getContentResolver(), jo.getString("jongmok"),
							jo.getString("price"), jo.getString("lowprice"), jo.getString("highprice"),
							jo.getString("sellday"), jo.getString("sellstep"));
				}

				mHandler.sendEmptyMessage(1);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return result.toString();
		}

		@Override
		protected void onPostExecute(String result) {

			// Do something with the JSON string
			Log.e("test", "test from server : " + result);

			if (mWaitingDialog.isShowing()) {
				mWaitingDialog.dismiss();
			}
			mHandler.sendEmptyMessage(1);

		}

	}

	protected void setStockItem(String item) {

		String jongmokHeader = "https://m.search.daum.net/search?w=tot&q=";
		String jongmokRear = "&nil_profile=rcmkwd&rtmaxcoll=1CI&pin=stock&DA=11M";
		String compare1 = "<span class=\"f_sub_s\">";
		String compare2 = "<span class=\"txt_sub\">";
		URL url = null;
		String jongmokName = item;
		HttpURLConnection conn = null;
		BufferedReader br = null;

		String stockCode;

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
				jongmokList.setStockItem(this, jongmokName, stockCode);
				Log.e("stock", "searchJongmok name : " + jongmokName);
				Log.e("stock", "searchJongmok stockCode : " + stockCode);

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub

		this.unregisterReceiver(mRefreshReceiver);
		super.onPause();
	}

	private void updateStockItem() {

		if (mStockItemAdapter == null) {
			new GetCurrentPriceAsyncTaskServer(this, null, this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else {
			new GetCurrentPriceAsyncTaskServer(this.getApplicationContext(), null, this)
					.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.servermain, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();
		if (id == R.id.action_settings) {

			Intent editIntent = new Intent("com.joyful.stock.action.ALARM_SETTINGS_ACTIVITY");
			startActivity(editIntent);

			return true;
		} else if (id == R.id.help) {
			Intent i = new Intent(Intent.ACTION_VIEW);

			Uri u = Uri.parse("http://nofateman.wix.com/joyful-idea/");

			i.setData(u);

			startActivity(i);

			return true;
		} else if (id == R.id.action_profit) {
			Intent profit = new Intent(this, ProfitNote.class);
			startActivity(profit);

			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCompleted(Context context, Map<String, ArrayList<String>> result) {
		// TODO Auto-generated method stub

		try {
			mStockItemAdapter = new ServerStockItemAdapter(this, result);
			mStockList.setAdapter(mStockItemAdapter);
		} catch (Exception ex) {
			Toast.makeText(ServerJongmokActivity.this, "주식 시장이 휴일 입니다...", Toast.LENGTH_LONG).show();
		}
	}

}
