package com.joyful.stock;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.joyful.stock.db.StockDBUpdater;
import com.joyful.stock.gcm.MyInstanceIDListenerService;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class LoginPage extends Activity {
	private static final int PERMISSIONS_REQUEST_GETACCOUNTS = 10;
	private boolean isGranted = false;
	ArrayList<String> gUsernameList = new ArrayList<String>();
	EditText emailEdit;
	LinearLayout mEditView;
	TextView mLoading;
	Handler mHandler;
	Button mAccount;
	Button mAccountPersonal;
	ProgressBar mProgress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
			// only for gingerbread and newer versions

			if (checkSelfPermission(Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
				Log.d("jusik", "checkSelfPermission ");
				requestPermissions(new String[] { Manifest.permission.GET_ACCOUNTS }, PERMISSIONS_REQUEST_GETACCOUNTS);
			} else {
				isGranted = true;
			}
		} else {
			isGranted = true;
		}
		mEditView = (LinearLayout) findViewById(R.id.writeemail);
		emailEdit = (EditText) findViewById(R.id.emailedit);
		mProgress = (ProgressBar) findViewById(R.id.progress);
		mLoading = (TextView) findViewById(R.id.loading);
		mLoading.setVisibility(View.VISIBLE);
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch (msg.what) {

				case 1:
					mProgress.setProgress(50);
					break;
				case 2:
					mProgress.setProgress(100);
					break;

				case 3:
					if (Util.getDeviceImei(LoginPage.this) != null) {
						mLoading.setVisibility(View.GONE);
						mProgress.setVisibility(View.GONE);
						Toast.makeText(getApplicationContext(), Util.getDeviceImei(LoginPage.this) + " 로 로그인 합니다",
								Toast.LENGTH_LONG).show();
						Intent i = new Intent(LoginPage.this, MainActivity.class);
						startActivity(i);
					} else {
						mLoading.setVisibility(View.GONE);
						mProgress.setVisibility(View.GONE);
						mAccount.setVisibility(View.VISIBLE);
						mAccountPersonal.setVisibility(View.VISIBLE);
					}

					break;
				default:

				}
				super.handleMessage(msg);
			}
		};
		initLoading();

		mAccount = (Button) findViewById(R.id.account);

		mAccount.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.e("stock", "LoginPage isGranted"+isGranted);
				if (isGranted) {
					// mEditView.setVisibility(View.VISIBLE);
					gUsernameList.clear();
					Account[] accounts = AccountManager.get(getApplicationContext()).getAccounts();

					Account account = null;

					for (int i = 0; i < accounts.length; i++) {

						account = accounts[i];

						if (account.type.equals("com.google")) { // 이러면 구글
																	// 계정 구분
							gUsernameList.add(account.name);

						}
					}

					if (gUsernameList.size() == 0) {
						Toast.makeText(LoginPage.this, "google 계정이 없습니다 별도 계정을 이용해주세요", Toast.LENGTH_SHORT).show();
					} else if (gUsernameList.size() > 1) {
						AlertDialog.Builder builder = new AlertDialog.Builder(LoginPage.this);
						builder.setTitle("Choose you gmail-account");

						ListView lv = new ListView(LoginPage.this);

						ArrayAdapter<String> adapter = new ArrayAdapter<String>(LoginPage.this,
								android.R.layout.simple_list_item_1, android.R.id.text1, gUsernameList);

						lv.setAdapter(adapter);
						lv.setOnItemClickListener(new OnItemClickListener() {

							public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
								Util.setDeviceImei(LoginPage.this, gUsernameList.get(position));
								// emailEdit.setText(gUsernameList.get(position));
								startSystem();
							}
						});

						builder.setView(lv);
						builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
								dialog.dismiss();
							}
						});

						final Dialog dialog = builder.create();
						dialog.show();

					} else {
						Util.setDeviceImei(LoginPage.this, gUsernameList.get(0));
						// emailEdit.setText(gUsernameList.get(0));

						startSystem();
					}

//					Intent intent = new Intent(LoginPage.this, MyInstanceIDListenerService.class);
//					startService(intent);

				} else {
					Toast.makeText(LoginPage.this, "권한 획득 전  입니다!!", Toast.LENGTH_SHORT).show();
				}
			}
		});

		mAccountPersonal = (Button) findViewById(R.id.accountpersonal);

		mAccountPersonal.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				mEditView.setVisibility(View.VISIBLE);

			}

		});

		Button mComplete = (Button) findViewById(R.id.complete);
		mComplete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Util.setDeviceImei(LoginPage.this, emailEdit.getText().toString());
				startSystem();
			}
		});
	}

	private void startSystem() {
		Intent intent = new Intent(LoginPage.this, MyInstanceIDListenerService.class);
		startService(intent);
		Toast.makeText(getApplicationContext(), Util.getDeviceImei(LoginPage.this) + " 로 로그인 합니다", Toast.LENGTH_LONG)
				.show();
		Intent i = new Intent(LoginPage.this, MainActivity.class);
		startActivity(i);
	}

	private void initLoading() {
		// TODO Auto-generated method stub
		
		Thread load = new Thread(new Runnable() {
//			int index = 1;

			@Override
			public void run() {
				// TODO Auto-generated method stub
				HttpURLConnection urlConnection = null;
				
				StringBuilder result = new StringBuilder();

				try {
					URL url = new URL("http://suah.iptime.org:9001/getjongmok");
					urlConnection = (HttpURLConnection) url.openConnection();
					InputStream in = new BufferedInputStream(urlConnection.getInputStream());

					BufferedReader reader = new BufferedReader(new InputStreamReader(in));

					String line;
					while ((line = reader.readLine()) != null) {
						result.append(line);
					}

					ArrayList<String> jongmoklist = StockDBUpdater
							.getJongMokServer(getContentResolver());

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
//							setStockItem(jo.getString("jongmok"));
							new SearchJongmokAsync(LoginPage.this, jo.getString("jongmok"), null);
						}

						StockDBUpdater.insertJongMokServer(getContentResolver(), jo.getString("jongmok"),
								jo.getString("price"), jo.getString("lowprice"), jo.getString("highprice"),
								jo.getString("sellday"), jo.getString("sellstep"));
					}

					mHandler.sendEmptyMessage(3);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				
				
				
				
//				while (index < 4) {
//					Log.e("thread", "thread index : " + index);
//					mHandler.sendEmptyMessage(index);
//					try {
//						Thread.sleep(1000);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//
//					index++;
//				}
			}
		});
		load.start();
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		if (requestCode == PERMISSIONS_REQUEST_GETACCOUNTS && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
			Log.d("jusik", "onRequestPermissionsResult ");
			isGranted = true;
		} else {
			// finish();
		}
	};

}
