package com.joyful.stock;

import java.util.ArrayList;
import java.util.Map;

import com.joyful.stock.gcm.MyInstanceIDListenerService;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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

public class MainActivity extends Activity implements GetCurrentPriceAsyncTask.Callback {

    private ListView mStockList;
    private StockItemAdapter mStockItemAdapter;
    private BroadcastReceiver mRefreshReceiver;
    private static final int PERMISSIONS_REQUEST_READ_PHONE_STATE = 10;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.setTitle("주식 관리앱");
        
//        if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
//			requestPermissions(new String[] { Manifest.permission.READ_PHONE_STATE },
//					PERMISSIONS_REQUEST_READ_PHONE_STATE);
//		} else {
//			Util.setDeviceImei(this);
//		}
        

       
        
        Thread setList = new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                jongmokList.setList(MainActivity.this);
            }
        });
        setList.start();
        
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
        Button btnprofit = (Button)findViewById(R.id.profit);
        btnprofit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(getApplicationContext(), ServerprofitTemp.class);
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
				overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
			}
		});
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//    	if (requestCode == PERMISSIONS_REQUEST_READ_PHONE_STATE
//				&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//    		Util.setDeviceImei(this);
//		} else {
//			finish();
//		}
//    };
  
    
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

//        final SwipeDetector swipeDetector = new SwipeDetector();
        mStockList = (ListView)findViewById(R.id.list_view);
        mStockList.setLongClickable(true);
        //        mStockList.setOnTouchListener(swipeDetector);
        mStockList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub

                Intent editIntent = new Intent(MainActivity.this, StockDetailActivity.class);
                editIntent.putExtra("code_num", (String)parent
                        .getAdapter()
                        .getItem(position));
                startActivity(editIntent);

            }
        });

        super.onResume();

        updateStockItem();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub

        this.unregisterReceiver(mRefreshReceiver);
        super.onPause();
    }

    private void updateStockItem() {


        if (mStockItemAdapter == null) {
            new GetCurrentPriceAsyncTask(this, null, this)
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new GetCurrentPriceAsyncTask(this.getApplicationContext(), null, this)
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_settings) {

            Intent editIntent = new Intent("com.joyful.stock.action.ALARM_SETTINGS_ACTIVITY");
            startActivity(editIntent);

            return true;
        } else if (id == R.id.search) {
            Intent search = new Intent(this, SearchJongmok.class);
            startActivity(search);

            return true;
        } else if (id == R.id.help) {
            Intent i = new Intent(Intent.ACTION_VIEW);

            Uri u = Uri.parse("http://nofateman.wix.com/joyful-idea/");

            i.setData(u);

            startActivity(i);

            return true;
        }else if (id == R.id.action_profit) {
            Intent profit = new Intent(this, ProfitNote.class);
            startActivity(profit);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCompleted(Context context, Map<String, ArrayList<String>> result) {
        // TODO Auto-generated method stub

    	Log.e("stock", "MainActivity async complete result : "+result);
        try {
            mStockItemAdapter = new StockItemAdapter(this, result);
            mStockList.setAdapter(mStockItemAdapter);
        } catch (Exception ex) {
            Toast.makeText(MainActivity.this, "주식 시장이 휴일 입니다...", Toast.LENGTH_LONG).show();
        }
    }

}
