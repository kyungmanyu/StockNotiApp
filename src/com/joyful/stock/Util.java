package com.joyful.stock;

import java.text.SimpleDateFormat;
import java.util.Set;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.util.Log;

public class Util {

    private static final String PREF_NAME = "com.joyful.stock_general_preferences";

    public static final String TARGET_POINT = "targetP";
    public static final String MINOR_POINT = "minorP";
    public static final String PURCHASE_POINT = "purchaseP";
    public static final String STOCK_CODES = "stock_codes";
    public static final String DATE_LIST = "date_list";
    public static final String UPDATE_FLAG = "update_flag";
    public static final String GCM_TOKEN = "gcm_token";

    public static final int ALARM_REQ_ID = 0;
    public static String sDeviceId;

    public static int getInt(Context context, String key, String itemId, int defaultVal) {

        SharedPreferences pref = context.getApplicationContext().getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);

        int val = pref.getInt(itemId + "." + key, defaultVal);
        Log.i("", "[Test][getInt] key = " + key + " / itemId = " + itemId + " / val = " + val
                + " / defaultVal = "
                + defaultVal);
        return val;
    }

    public static void putInt(Context context, String key, String itemId, int inputVal) {
        SharedPreferences pref = context.getApplicationContext().getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        pref.edit().putInt(itemId + "." + key, inputVal).commit();

        Log.i("", "[Test][putInt] key = " + key + " / itemId = " + itemId + " / inputVal = "
                + inputVal);
    }
    
    public static void removeInt(Context context, String key, String itemId, int inputVal) {
        SharedPreferences pref = context.getApplicationContext().getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);        
        pref.edit().remove(itemId + "." + key).commit();
        Log.i("", "[Test][putInt] key = " + key + " / itemId = " + itemId + " / inputVal = "
                + inputVal);
    }
    
    

    public static String getStockProfit(Context context, String key) {
        SharedPreferences pref = context.getApplicationContext().getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        return pref.getString(key, null);

    }

    public static void setStockProfit(Context context, String key, String profit) {
        SharedPreferences pref = context.getApplicationContext().getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        pref.edit().clear();
        pref.edit().putString(key, profit).commit();
    }
    
    public static String getGcmToken(Context context) {
        SharedPreferences pref = context.getApplicationContext().getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        return pref.getString(GCM_TOKEN, null);

    }
    
    public static void setGcmToken(Context context, String token) {
        SharedPreferences pref = context.getApplicationContext().getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        pref.edit().clear();
        pref.edit().putString(GCM_TOKEN, token).commit();
    }

    public static boolean getFlag(Context context) {
        SharedPreferences pref = context.getApplicationContext().getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        return pref.getBoolean(UPDATE_FLAG, true);

    }

    public static void setFlag(Context context, boolean flag) {
        SharedPreferences pref = context.getApplicationContext().getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        pref.edit().clear();
        pref.edit().putBoolean(UPDATE_FLAG, flag).commit();
    }

    public static Set<String> getStockCodes(Context context) {
        SharedPreferences pref = context.getApplicationContext().getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        return pref.getStringSet(STOCK_CODES, null);

    }

    public static void setStockCodes(Context context, Set<String> list) {
        SharedPreferences pref = context.getApplicationContext().getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        pref.edit().clear();
        pref.edit().putStringSet(STOCK_CODES, list).commit();
    }

    public static String getProfit(int curP, int puchP) {
        double currentP = curP;
        double puchaseP = puchP;
        double profitValue = ((currentP - puchaseP) / puchaseP) * 100;

        Log.e("stock", "profitValue" + profitValue);
        Log.e("stock", "format profitValue" + String.format("%.2f", profitValue));
        return String.format("%.2f", profitValue);
    }

    public static String getTotalProfit(Context context) {

        double profitValue = 0;
        for (String code : Util.getStockCodes(context.getApplicationContext())) {
            double eachProfit = Double.parseDouble(getStockProfit(context.getApplicationContext(),
                    code));
            Log.e("stock", "getTotal eachProfit :" + eachProfit);
            profitValue = profitValue + eachProfit;
            Log.e("stock", "getTotal profitValue : " + profitValue);
        }

        return String.format("%.2f", profitValue);
    }

    public synchronized static void makeNotification(Context context, String itemId, String title,
            String summary,
            PendingIntent pIntent) {

        Notification.Builder builder = new Notification.Builder(context)
                .setContentTitle(title)
                .setContentText(summary).setSmallIcon(R.drawable.noti)
                .setContentIntent(pIntent)
                .setAutoCancel(true).setWhen(System.currentTimeMillis());

        Notification notification = builder.build();
        //        notification.contentView.setImageViewResource(android.R.id.icon, R.drawable.ic_launcher);
        NotificationManager notificationManager = (NotificationManager)context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        int notiID = 0;
        try {
            notiID = Integer.parseInt(itemId);
        } catch (Exception e) {
        }

        notificationManager.notify(notiID, notification);
    }

    public static PendingIntent getPendingIntent(Context context, int pendingFlags) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.setAction(AlarmReceiver.ACTION_REFRESH);

        PendingIntent operation = PendingIntent.getBroadcast(context,
                ALARM_REQ_ID, intent, pendingFlags);

        return operation;
    }

    public static void cancelRepeatAlarm(Context context) {

        PendingIntent pending = getPendingIntent(context, PendingIntent.FLAG_NO_CREATE);

        Log.i("", "[Test][cancelRepeatAlarm] pendingObj = " + pending);

        if (pending != null) {
            AlarmManager am = (AlarmManager)context.getSystemService(Activity.ALARM_SERVICE);
            am.cancel(pending);
            pending.cancel();
            Log.d("", "[Test][cancelRepeatAlarm] Alarm canceled");
        }
    }

    public static void startRepeatAlarm(Context context, long period) {

        Log.i("", "[Test][startRepeatAlarm]:: input value = " + period);

        period = period * 1000 * 60;

        AlarmManager am = (AlarmManager)context.getSystemService(Activity.ALARM_SERVICE);
        PendingIntent pending = getPendingIntent(context, PendingIntent.FLAG_CANCEL_CURRENT);

        long now = System.currentTimeMillis();
        long firstTriggerTime = now + period;

        Log.i("", "[Test][startRepeatAlarm]:: period (millisecond) = " + period + " / Time now : "
                + (new SimpleDateFormat("hh:mm:ss")).format(now) + " / Trigger Time = "
                + (new SimpleDateFormat("hh:mm:ss")).format(firstTriggerTime));

        am.setRepeating(AlarmManager.RTC_WAKEUP, firstTriggerTime, period, pending);

    }

    /** @author kyungman.yu  2015. 7. 14. **/
    public static void addProfitNote(Context context, String todayDate, String profitData) {
        // TODO Auto-generated method stub

        SharedPreferences pref = context.getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        pref.edit().clear();
        pref.edit().putString(todayDate, profitData).commit();
    }

    public static String getProfitNote(Context context, String todayDate) {
        // TODO Auto-generated method stub

        SharedPreferences pref = context.getApplicationContext().getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        return pref.getString(todayDate, null);
    }

    public static void addDateList(Context context, Set<String> dateList) {
        // TODO Auto-generated method stub

        SharedPreferences pref = context.getApplicationContext().getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        pref.edit().clear();
        pref.edit().putStringSet(DATE_LIST, dateList).commit();
    }

    public static Set<String> getDateList(Context context) {
        // TODO Auto-generated method stub

        SharedPreferences pref = context.getApplicationContext().getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        return pref.getStringSet(DATE_LIST, null);
    }

    
	public static void setDeviceImei(Context context) {
		// TODO Auto-generated method stub
		TelephonyManager mngr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		sDeviceId = mngr.getDeviceId();
		Log.e("kyungman", "device id = "+sDeviceId);
	}

}
