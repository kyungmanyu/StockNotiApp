package com.joyful.stock;

import java.util.ArrayList;
import java.util.Map;

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

public class EditStockItemActivity extends Activity implements OnClickListener,
        GetCurrentPriceAsyncTask.Callback {

    private String mCurrentItemNumber;

    private TextView mItemNum;
    private TextView mItemName;

    private TextView mCPoint;
    private EditText mTpoint;
    private EditText mMpoint;
    private EditText mPpoint;

    public static final String EXTRA_STOCK_ITEM_NUM = "stockItemNumber";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_stock_item_activity);
        
        mItemNum = (TextView)findViewById(R.id.itemNum);
        mItemName = (TextView)findViewById(R.id.itemName);


        mCPoint = (TextView)findViewById(R.id.cPoint);
        mTpoint = (EditText)findViewById(R.id.tPoint);
        mMpoint = (EditText)findViewById(R.id.mPoint);
        mPpoint = (EditText)findViewById(R.id.pPoint);

        findViewById(R.id.cancelBtn).setOnClickListener(this);
        findViewById(R.id.saveBtn).setOnClickListener(this);

        mCurrentItemNumber = getIntent().getStringExtra(EXTRA_STOCK_ITEM_NUM);

        // load current Price
        Log.i("", "[Test] mCurrentItemNumber = " + mCurrentItemNumber);
        if (mCurrentItemNumber != null) {
            mItemNum.setText(mCurrentItemNumber);

            ArrayList<String> itemNumList = new ArrayList<>();
            itemNumList.add(mCurrentItemNumber);
            new GetCurrentPriceAsyncTask(this, itemNumList, this)
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            String sName = jongmokList.getStockName(mCurrentItemNumber);
            mItemName.setText(sName);

            int pPrice = Util.getInt(this, Util.PURCHASE_POINT, mCurrentItemNumber, 0);
            Log.w("", "[Test] pPrice = " + pPrice);
            if (pPrice > 0) {
                mPpoint.setText("" + pPrice);
            }
            int mPrice = Util.getInt(this, Util.MINOR_POINT, mCurrentItemNumber, 0);
            Log.w("", "[Test] mPrice = " + mPrice);
            if (mPrice > 0) {
                mMpoint.setText("" + mPrice);
            }
            int tPrice = Util.getInt(this, Util.TARGET_POINT, mCurrentItemNumber, 0);
            Log.w("", "[Test] tPrice = " + tPrice);
            if (tPrice > 0) {
                mTpoint.setText("" + tPrice);
            }
        }
    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.detail, menu);
//
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        int id = item.getItemId();
//      if (id == R.id.delete) {
//          if (mCurrentItemNumber != null) {
//              jongmokList.removeStockCode(this, mCurrentItemNumber);
//              finish();
//          }
//
//
//            return true;
//        }else if(id == R.id.action_detail){
//            Intent editIntent = new Intent(this, StockDetailActivity.class);
//            editIntent.putExtra("code_num", mCurrentItemNumber);
//            startActivity(editIntent);
//
//            
//            return true;
//        }
//      
//        return super.onOptionsItemSelected(item);
//    }
    
    @Override
    public void onClick(View arg0) {

        switch (arg0.getId()) {
       
        case R.id.cancelBtn:
            finish();
            break;
        case R.id.saveBtn:
            if (saveData()) {
                finish();
            }
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

    private boolean saveData() {

        if (mCurrentItemNumber == null) {
            return false;
        }

        boolean retVal = true;
        // Target price
        int tPoint = 0;
        try {
            tPoint = Integer.parseInt(mTpoint.getText().toString());
        } catch (Exception e) {
            Log.e("", "[Test][onClick] saveTargetP.");
            Toast.makeText(this, "목표가 저장 실패!!", Toast.LENGTH_SHORT).show();
            retVal = false;
        }
        if (tPoint > 0) {
            Util.putInt(this, Util.TARGET_POINT, mCurrentItemNumber, tPoint);
        }
        // minor price
        int mPoint = 0;
        try {
            mPoint = Integer.parseInt(mMpoint.getText().toString());
        } catch (Exception e) {
            Log.e("", "[Test][onClick] saveTargetP.");
            Toast.makeText(this, "손절가 저장 실패!!", Toast.LENGTH_SHORT).show();
            retVal = false;
        }
        if (mPoint > 0) {
            Util.putInt(this, Util.MINOR_POINT, mCurrentItemNumber, mPoint);
        }
        // purchase price
        int pPoint = 0;
        try {
            pPoint = Integer.parseInt(mPpoint.getText().toString());
        } catch (Exception e) {
            Log.e("", "[Test][onClick] saveTargetP.");
            Toast.makeText(this, "매수가 저장 실패!!", Toast.LENGTH_SHORT).show();
            retVal = false;
        }
        if (pPoint > 0) {
            Util.putInt(this, Util.PURCHASE_POINT, mCurrentItemNumber, pPoint);
        }

        if (retVal) {
            jongmokList.addStockCode(this, mCurrentItemNumber);
        }

        return retVal;

    }

}