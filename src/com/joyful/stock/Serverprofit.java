package com.joyful.stock;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

public class Serverprofit extends Activity {
	private WebView mWebview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// String addr = "http://nofateman.wix.com/joyful-idea/";
		String addr = "http://suah.iptime.org:9001/showhistory";

		getWindow().requestFeature(getWindow().FEATURE_PROGRESS);
		setContentView(R.layout.activity_profit);

		Button btnlocal = (Button)findViewById(R.id.local);
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
		  Button btnserver = (Button)findViewById(R.id.server);
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
		
		mWebview = (WebView) findViewById(R.id.detail_webview);
		mWebview.getSettings().setJavaScriptEnabled(true);

		mWebview.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int progress) {
				// Activities and WebViews measure progress with different
				// scales.
				// The progress meter will automatically disappear when we reach
				// 100%
				Serverprofit.this.setProgress(progress * 1000);
			}
		});
		mWebview.setWebViewClient(new WebViewClient() {
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				Toast.makeText(Serverprofit.this, "error " + description, Toast.LENGTH_SHORT).show();
			}
		});
		mWebview.getSettings().setLoadWithOverviewMode(true);
		mWebview.getSettings().setUseWideViewPort(true);
		// mWebview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
		// mWebview.setScrollbarFadingEnabled(false);
		mWebview.loadUrl(addr);
		mWebview.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebview.canGoBack()) {
					mWebview.goBack();
					return true;
				}
				return false;
			}
		});

	}

}
