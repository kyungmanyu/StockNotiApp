package com.joyful.stock;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class StockDetailActivity extends Activity {

    private String code;
    private WebView mWebview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        String addr = "http://m.stock.naver.com/item/main.nhn#/stocks/";
        code = getIntent().getStringExtra("code_num");
        getWindow().requestFeature(getWindow().FEATURE_PROGRESS);
       
        setContentView(R.layout.stock_detail);
        
        mWebview = (WebView)findViewById(R.id.detail_webview);
        mWebview.getSettings().setJavaScriptEnabled(true);

        mWebview.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                // Activities and WebViews measure progress with different scales.
                // The progress meter will automatically disappear when we reach 100%
                StockDetailActivity.this.setProgress(progress * 1000);
            }
        });
        mWebview.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description,
                    String failingUrl) {
                Toast.makeText(StockDetailActivity.this, "error " + description, Toast.LENGTH_SHORT)
                        .show();
            }
        });

        mWebview.loadUrl(addr + code + "/total");
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
        
        Intent editIntent = new Intent("com.joyful.stock.action.EDIT_STOCK_ITEM_ACTIVITY");
        editIntent.putExtra(EditStockItemActivity.EXTRA_STOCK_ITEM_NUM, code);
        startActivity(editIntent);

        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail, menu);

        return true;
    }

  
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.delete) {
            if (code != null) {

                new AlertDialog.Builder(this)
                        .setTitle("종목 삭제")
                        .setMessage("선택한 종목을 삭제 하시겠습니까?")
                        .setPositiveButton(android.R.string.yes,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // continue with delete
                                        jongmokList.removeStockCode(StockDetailActivity.this, code);
                                        Util.removeInt(StockDetailActivity.this, Util.TARGET_POINT, code, 0);
                                        Util.removeInt(StockDetailActivity.this, Util.MINOR_POINT, code, 0);
                                        Util.removeInt(StockDetailActivity.this, Util.PURCHASE_POINT, code, 0);
                                        finish();
                                    }
                                })
                        .setNegativeButton(android.R.string.no,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // do nothing
                                    }
                                })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }

            return true;
        } else if (id == R.id.action_detail) {
            
            Intent editIntent = new Intent("com.joyful.stock.action.EDIT_STOCK_ITEM_ACTIVITY");
            editIntent.putExtra(EditStockItemActivity.EXTRA_STOCK_ITEM_NUM, code);
            startActivity(editIntent);
            
            return true;

        }

        return super.onOptionsItemSelected(item);
    }

    
}
