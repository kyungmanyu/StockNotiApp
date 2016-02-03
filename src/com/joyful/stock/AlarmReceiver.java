package com.joyful.stock;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class AlarmReceiver extends BroadcastReceiver implements GetCurrentPriceAsyncTask.Callback {

    public static final String ACTION_REFRESH = "com.lge.email.intent.action.ACTION_REFRESH";

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent == null) {
            return;
        }

        String actionName = intent.getAction();

        if (actionName.equals(ACTION_REFRESH)) {

            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.KOREA);
            Calendar cal = Calendar.getInstance(Locale.KOREA);
            Log.e("test",
                    "cal test dateFormat.format(cal.getTime()) : "
                            + dateFormat.format(cal.getTime()));

            try {
                Date currentDate = dateFormat.parse(dateFormat.format(cal.getTime()));
                Date befor = dateFormat.parse("09:00");
                Date after = dateFormat.parse("15:00");
                Date finishtime = dateFormat.parse("14:00");
                Log.e("test",
                        "cal test currentDate.after(befor) : "
                                + currentDate.after(befor));

                Log.e("test",
                        "cal test currentDate.before(after) : "
                                + currentDate.before(after));
                if (currentDate.after(befor) && currentDate.before(after)) {
                    new GetCurrentPriceAsyncTask(context, null, this)
                            .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    Util.setFlag(context, true);
                }

            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

    }

    @Override
    public void onCompleted(Context context, Map<String, ArrayList<String>> result) {

        ArrayList<String> itemIDs = null;
        Set<String> tmp = Util.getStockCodes(context);
        if (tmp != null) {
            itemIDs = new ArrayList<>();
            Iterator<String> ir = tmp.iterator();
            while (ir.hasNext()) {
                itemIDs.add(ir.next());
            }
        }

        Log.d("", "[Test][Receiver] onCompleted.. ");
        if (itemIDs == null) {
            return;
        }

        for (String curId : itemIDs) {

            ArrayList<String> price = result.get(curId);
            int compareV = 0;
            try {
                compareV = Integer.parseInt(price.get(0));
            } catch (Exception e) {
            }

            Log.d("", "[Test][Receiver] curId = " + curId + " / curPrice = " + compareV);

            if (compareV != 0) {
                Log.e("stock",
                        "Util.getProfit(compareV,Util.getInt(context, Util.PURCHASE_POINT, curId, 0)) : "
                                + Util.getProfit(compareV,
                                        Util.getInt(context, Util.PURCHASE_POINT, curId, 0)));
                Util.setStockProfit(
                        context,
                        curId,
                        Util.getProfit(compareV,
                                Util.getInt(context, Util.PURCHASE_POINT, curId, 0)));

                comparePrice(context, curId, compareV);
            }
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.KOREA);
        Calendar cal = Calendar.getInstance(Locale.KOREA);
        Log.e("test",
                "cal test dateFormat.format(cal.getTime()) : "
                        + dateFormat.format(cal.getTime()));

        try {
            Date currentDate = dateFormat.parse(dateFormat.format(cal.getTime()));
            Date befor = dateFormat.parse("09:00");
            Date after = dateFormat.parse("15:00");
            Date finishtime = dateFormat.parse("14:00");
            Log.e("stock",
                    "onCompleted cal test currentDate.after(befor) : "
                            + currentDate.after(befor));

            Log.e("stock",
                    "onCompleted cal test currentDate.before(after) : "
                            + currentDate.before(after));

            if (currentDate.after(finishtime) && currentDate.before(after) && Util.getFlag(context)) {

                Intent intent = new Intent();
                intent.setClassName(context, "com.joyful.stock.ProfileNote");

                PendingIntent pendingIntent = PendingIntent.getActivity(
                        context.getApplicationContext(), 11, intent,
                        PendingIntent.FLAG_ONE_SHOT);

                Util.makeNotification(context.getApplicationContext(), "11", "주식알림",
                        " 오늘의 수익률 "
                                + Util.getTotalProfit(context.getApplicationContext()),
                        pendingIntent);

                SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy.MM.dd",
                        Locale.KOREA);

                String todayDate = mSimpleDateFormat.format(cal.getTime());

                Util.addProfitNote(
                        context,
                        todayDate,
                        "[ " + todayDate + " ]" + "   수익률 : "
                                + Util.getTotalProfit(context.getApplicationContext()));
                Set<String> dateList = new LinkedHashSet<>();
                if (Util.getDateList(context) != null) {
                    dateList = Util.getDateList(context);
                }

                dateList.add(todayDate);
                Log.d("addStockCode", "add codelist : " + dateList.toString());
                Util.addDateList(context, dateList);

                Util.setFlag(context, false);
            }

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private void comparePrice(Context context, String itemId, int currentP) {
        Log.d("", "[Test][Recevier] comparePrice :: itemId = " + itemId
                + " / currentP = " + currentP);

        if (context != null && itemId != null && currentP > 0) {

            int tPoint = Util.getInt(context, Util.TARGET_POINT, itemId, 0);
            int mPoint = Util.getInt(context, Util.MINOR_POINT, itemId, 0);

            String sName = jongmokList.getStockName(itemId);

            Log.d("", "[Test][Recevier] comparePrice :: itemId = " + itemId
                    + " / sName = " + sName
                    + " / targetPrice = " + tPoint
                    + " / minorPrice = " + mPoint);

            if (tPoint > 0 && mPoint > 0) {
                String profit = Util.getProfit(currentP,
                        Util.getInt(context, Util.PURCHASE_POINT, itemId, 0));

                Intent editIntent = new Intent("com.joyful.stock.action.EDIT_STOCK_ITEM_ACTIVITY");
                editIntent.putExtra(EditStockItemActivity.EXTRA_STOCK_ITEM_NUM, itemId);

                PendingIntent pendingIntent = PendingIntent.getActivity(
                        context.getApplicationContext(), Integer.parseInt(itemId), editIntent,
                        PendingIntent.FLAG_ONE_SHOT);

                if (currentP >= tPoint) {
                    Util.makeNotification(context, itemId, "주식알림", "목표가 초과!! " + sName + "("
                            + itemId + ") "
                            + " 수익률 = " + profit, pendingIntent);
                } else if (currentP <= mPoint) {
                    Util.makeNotification(context, itemId, "주식알림", "손절가 미만!! " + sName + "("
                            + itemId + ") "
                            + " 수익률 = " + profit, pendingIntent);
                } else {
                    //                    Util.makeNotification(context, itemId, "주식알림", "현재현황.. " + sName + "("
                    //                            + itemId + ") "
                    //                            + " 현재가 = " + currentP, null);
                }
            }
        }
    }

}
