package com.joyful.stock;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class GetCurrentPriceAsyncTask extends
        AsyncTask<Void, Void, Map<String, ArrayList<String>>> {

    private Callback mCallback = EmptyCallback.INSTANCE;

    public static final String NAVER_FINANCE = "http://finance.naver.com/item/sise_day.nhn?code=";
    //= "http://finance.naver.com/item/main.nhn?code=";

    private Activity mParentActivity = null;
    private AlertDialog mWaitingDialog;
    private Context mContext;
    //    private ArrayList<String> mItemNumList;

    private List<String> mItemNumList =
            Collections.synchronizedList(new LinkedList());

    public interface Callback {
        public void onCompleted(Context context, Map<String, ArrayList<String>> result);
    }

    private static class EmptyCallback implements Callback {
        public static final Callback INSTANCE = new EmptyCallback();

        @Override
        public void onCompleted(Context context, Map<String, ArrayList<String>> result) {
        }
    }

    public GetCurrentPriceAsyncTask() {

    }

    private void setItemList(Context context) {

        Set<String> temp = Util.getStockCodes(context);
      
        if (temp != null) {
        	Log.e("stock", "setItemList temp size : "+temp.size());
            Log.e("stock", "setItemList temp tostring : "+temp.toString());
            Iterator<String> ir = temp.iterator();
            while (ir.hasNext()) {
                mItemNumList.add(ir.next());
            }
            
        }

    }


    public GetCurrentPriceAsyncTask(Context context, final ArrayList<String> itemNumList,
            Callback callback) {
        Log.w("", "[Test] GetCurrentPriceAsyncTask() - Context");
        mContext = context;
        if (itemNumList == null) {
            setItemList(mContext);
        } else {
            mItemNumList = itemNumList;
        }
        mCallback = (callback == null) ? EmptyCallback.INSTANCE : callback;
    }

    public GetCurrentPriceAsyncTask(Activity activity, final ArrayList<String> itemNumList,
            Callback callback) {
        Log.w("", "[Test] GetCurrentPriceAsyncTask() - Activity!!");
        mParentActivity = activity;
        mContext = activity.getApplicationContext();
        if (itemNumList == null) {
            setItemList(mContext);
        } else {
            mItemNumList = itemNumList;
        }
        mCallback = (callback == null) ? EmptyCallback.INSTANCE : callback;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if (mParentActivity != null) {
            showWaitingDialog("업데이트", "종목 정보 갱신중..");
        }

    }

    @Override
    protected Map<String, ArrayList<String>> doInBackground(Void... arg0) {

        Map<String, ArrayList<String>> pMap = new LinkedHashMap<>();

        synchronized (this) {
            if (mItemNumList != null) {
                for (String itemNum : mItemNumList) {
                    ArrayList<String> price = new ArrayList<>();
                    price = findCurrentPrice(NAVER_FINANCE + itemNum);
                    pMap.put(itemNum, price);
                }
            }
        }
        return pMap;
    }

    @Override
    protected void onPostExecute(Map<String, ArrayList<String>> result) {
        if (mContext != null && mCallback != null) {

            if (result != null) {
                mCallback.onCompleted(mContext, result);
            }
        }

        if (mParentActivity != null) {
            dismissWaitingDialog();
        }
    }

    private void showWaitingDialog(String title, String message) {
        Log.i("", "[Test] showWaitingDialog!! ");
        try {
            ProgressDialog dlgProgress = new ProgressDialog(mParentActivity);
            //            dlgProgress.setTitle(title);
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

    private void dismissWaitingDialog() {
        Log.i("", "[Test] dismissWaitingDialog!! ");
        try {
            if (mParentActivity.isFinishing() == false && mWaitingDialog != null) {
                mWaitingDialog.dismiss();
                mWaitingDialog = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ArrayList<String> findCurrentPrice(final String addr) {

        SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy.MM.dd",
                Locale.KOREA);

        Calendar cal = Calendar.getInstance(Locale.KOREA);

        if (cal.get(Calendar.DAY_OF_WEEK) % 7 == 0) {
            cal.add(Calendar.DATE, -1);
        } else if (cal.get(Calendar.DAY_OF_WEEK) % 7 == 1) {
            cal.add(Calendar.DATE, -2);
        }
        String todayDate = mSimpleDateFormat.format(cal.getTime());

        HttpURLConnection conn = null;
        URL url = null;
        BufferedReader br = null;

        ArrayList<String> retVal = new ArrayList<>();
        try {
            url = new URL(addr);
            conn = (HttpURLConnection)url.openConnection();

            if (conn != null) {
                Log.d("", "[Test] conn..");
                conn.setConnectTimeout(1000);
                conn.setReadTimeout(1000);
                conn.setRequestMethod("GET");
                conn.setAllowUserInteraction(false);
                conn.setRequestProperty("content-type", "text/plain; charset=euc-kr");
                conn.setUseCaches(false);
                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    Log.d("", "[Test] conn.. :: HTTP_OK..");
                    br = new BufferedReader(
                            new InputStreamReader(conn.getInputStream(), "euc-kr"));

                    int nextCheck = 0; // 0 - today , 1 - 현재가, 3 - 상승/하락, 4 - 변동폭
                    String curPrice = null;
                    String arrow = null;
                    String change = null;
                    for (;;) {
                        String line = br.readLine();
                        if (line == null) {
                            break;
                        }

                        if (nextCheck == 0 && line.contains(todayDate.trim())) {
                            System.out.println("today :::: " + line);
                            nextCheck++;
                            continue;
                        }
                        if (nextCheck == 1) {
                            System.out.println(line);
                            curPrice = line;
                            nextCheck++;
                            continue;
                        }
                        if (nextCheck == 2) {
                            nextCheck++;
                            continue;
                        }
                        if (nextCheck == 3) {
                            System.out.println(line);
                            arrow = line;
                            nextCheck++;
                            continue;
                        }
                        if (nextCheck == 4) {
                            System.out.println(line);
                            change = line;
                            nextCheck++;
                            continue;
                        }

                        if (nextCheck == 5) {
                            break;
                        }
                      
                    }
                    String currentPrice = getCurrentPrice(curPrice);
                    String changePercent = getChangePercent(currentPrice, arrow, change);

                    retVal.add(currentPrice);
                    retVal.add(changePercent);
                    br.close();

                    return retVal;

                } else {
                    Log.e("", "[Test] conn.. :: HTTP_ERROR..");
                }

            } else {
                Log.w("", "[Test] conn is null..");
            }
        } catch (Exception ex) {
            Log.e("", "[Test] Exception");
            ex.printStackTrace();
        } finally {
            if (conn != null) {
                Log.e("", "[Test] disconnect..");
                conn.disconnect();
            }
        }

        return null;

    }

   

    public static String getChangePercent(String currentPrice, String arrow, String changeVal) {

        String upS = "상승";
        String downS = "하락";

        if (currentPrice == null || arrow == null || changeVal == null) {
            return null;
        }

        changeVal = changeVal.trim().replace(",", "");

        double todayP = 0;
        double chanP = 0;

        try {
            todayP = Double.parseDouble(currentPrice);
            chanP = Double.parseDouble(changeVal);
        } catch (Exception e) {
        }

        boolean isPlus = true;
        if (arrow.contains(upS) || arrow.contains(downS)) {
            if (arrow.contains(downS)) {
                isPlus = false;
            }
        }

        double yesterdayP = 0;
        if (isPlus) {
            yesterdayP = todayP - chanP;
        } else {
            yesterdayP = todayP + chanP;
        }

        double changePercent = 0;
        if (todayP > 0 && yesterdayP > 0) {
            changePercent = (todayP / yesterdayP - 1.0) * 100;
            double roundVal = Math.round(changePercent * 100d) / 100d;
            return String.valueOf(roundVal);
        }

        return null;
    }

    public static String getCurrentPrice(String input) {
        String comS = "<span";
        String comE = "</span>";
        String comC = "\">";

        if (input == null) {
            return null;
        }

        if (input.contains(comS) && input.contains(comE)) {

            System.out.println("com::: " + input);
            String[] splitString = input.split(comS);

            String temp1 = splitString[1].trim();
            //            System.out.println(temp1);

            splitString = temp1.split(comE);
            String temp2 = splitString[0].trim();
            //            System.out.println(temp2);

            splitString = temp2.split(comC);
            String temp3 = splitString[1].trim();
            //            System.out.println(temp3);

            String temp4 = temp3.replace(",", "");

            return temp4;
        }

        return null;
    }
}