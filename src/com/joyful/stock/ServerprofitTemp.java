package com.joyful.stock;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

public class ServerprofitTemp extends Activity {

	ListView mainList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// String addr = "http://nofateman.wix.com/joyful-idea/";
		String addr = "http://suah.iptime.org:9001/showhistory";

		setContentView(R.layout.activity_server_profit);

		mainList = (ListView) findViewById(R.id.list_view);

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
		Button btnserver = (Button) findViewById(R.id.server);
		btnserver.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(getApplicationContext(), ServerJongmokActivity.class);
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
				overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
			}
		});

		Button profitBtn = (Button) findViewById(R.id.totaljongmok);
		profitBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(getApplicationContext(), Serverprofit.class);
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
			}
		});

		getData data = new getData();
		data.execute();

	}

	class getData extends AsyncTask<String, String, String> {

		HttpURLConnection urlConnection;

		@Override
		protected String doInBackground(String... args) {

			StringBuilder result = new StringBuilder();

			try {
				URL url = new URL("http://suah.iptime.org:9001/getprofit");
				urlConnection = (HttpURLConnection) url.openConnection();
				InputStream in = new BufferedInputStream(urlConnection.getInputStream());

				BufferedReader reader = new BufferedReader(new InputStreamReader(in));

				String line;
				while ((line = reader.readLine()) != null) {
					result.append(line);
				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				urlConnection.disconnect();
			}

			return result.toString();
		}

		@Override
		protected void onPostExecute(String result) {

			// Do something with the JSON string
			Log.e("test", "test from server : " + result);
			try {
				JSONArray JArry = new JSONArray(result.toString());
				PtofitDTO dto = new PtofitDTO(JArry);
				ServerProfitAdapter adapter = new ServerProfitAdapter(ServerprofitTemp.this, dto.getTenArray());
				mainList.setAdapter(adapter);

				// mHandler.sendEmptyMessage(1);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
