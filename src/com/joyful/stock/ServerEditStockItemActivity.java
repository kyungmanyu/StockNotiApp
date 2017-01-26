package com.joyful.stock;

import java.util.ArrayList;
import java.util.Map;

import com.joyful.stock.db.StockDBUpdater;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ServerEditStockItemActivity extends Activity
		implements OnClickListener, GetCurrentPriceAsyncTask.Callback {

	private String mCurrentItemNumber;

	private TextView mItemNum;
	private TextView mItemName;

	private TextView mCPoint;
	private TextView mTpoint;
	private TextView mMpoint;
	private TextView mPpoint;
	private TextView mDay;
	private TextView mStep;

	public static final String EXTRA_STOCK_ITEM_NUM = "stockItemNumber";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.server_edit_stock_item_activity2);

		mItemNum = (TextView) findViewById(R.id.itemNum);
		mItemName = (TextView) findViewById(R.id.itemName);

		mCPoint = (TextView) findViewById(R.id.cPoint);
		mTpoint = (TextView) findViewById(R.id.tPoint);
		mMpoint = (TextView) findViewById(R.id.mPoint);
		mPpoint = (TextView) findViewById(R.id.pPoint);
		mDay = (TextView) findViewById(R.id.day);
		mStep = (TextView) findViewById(R.id.sellstep);

		findViewById(R.id.cancelBtn).setOnClickListener(this);
		findViewById(R.id.saveBtn).setOnClickListener(this);

		mCurrentItemNumber = getIntent().getStringExtra(EXTRA_STOCK_ITEM_NUM);

		// load current Price
		Log.i("", "[Test] mCurrentItemNumber = " + mCurrentItemNumber);
		if (mCurrentItemNumber != null) {
			mItemNum.setText(mCurrentItemNumber);

			ArrayList<String> itemNumList = new ArrayList<>();
			itemNumList.add(mCurrentItemNumber);
			new GetCurrentPriceAsyncTask(this, itemNumList, this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

			String sName = jongmokList.getStockName(mCurrentItemNumber);
			mItemName.setText(sName);


			mPpoint.setText("" + StockDBUpdater.getbuyPriceServer(this.getContentResolver(),
					jongmokList.getStockName(mCurrentItemNumber)));


			mMpoint.setText("" + StockDBUpdater.getlowPriceServer(this.getContentResolver(),
					jongmokList.getStockName(mCurrentItemNumber)));

			mTpoint.setText("" + StockDBUpdater.gethighPriceServer(this.getContentResolver(),
					jongmokList.getStockName(mCurrentItemNumber)));
			mDay.setText("" + StockDBUpdater.getdayServer(this.getContentResolver(),
					jongmokList.getStockName(mCurrentItemNumber)));
			mStep.setText("" + StockDBUpdater.getstepServer(this.getContentResolver(),
					jongmokList.getStockName(mCurrentItemNumber)));
		}
	}

	@Override
	public void onClick(View arg0) {

		switch (arg0.getId()) {

		case R.id.cancelBtn:
			finish();
			break;
		case R.id.saveBtn:
//			if (saveData()) {
				finish();
//			}
			break;
		default:
			break;

		}

	}

	@Override
	public void onCompleted(Context context, Map<String, ArrayList<String>> result) {
		Log.d("", "[Test][Activity] onCompleted.. ");
		if (mCurrentItemNumber == null) {
			return;
		}
		ArrayList<String> price = result.get(mCurrentItemNumber);
		if (mCPoint != null && price != null && price.size() == 2) {
			mCPoint.setText("" + price.get(0));
		}
	}


}