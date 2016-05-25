package com.joyful.stock.gcm;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.joyful.stock.Util;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;

public class MyInstanceIDListenerService extends IntentService {

	private static final String TAG = "RegIntentService";
	private static final String[] TOPICS = { "global" };
	private static final String SENDER_ID = "662614584412";
	private String mDeviceID;

	public MyInstanceIDListenerService() {
		super(TAG);
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		Log.i(TAG, "GCM Registration oncreate");

		// if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
		// != PackageManager.PERMISSION_GRANTED) {
		// requestPermissions(new
		// String[]{Manifest.permission.READ_PHONE_STATE},
		// PERMISSIONS_REQUEST_READ_PHONE_STATE);
		// } else {
		// setDeviceImei();
		// }
		setDeviceImei();
		super.onCreate();
	}

	private void setDeviceImei() {
		// TODO Auto-generated method stub
		TelephonyManager mngr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		// mDeviceID = mngr.getDeviceId();

		mDeviceID = "1";
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		Log.i(TAG, "GCM Registration Token: " + intent);
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

		try {
			// [START register_for_gcm]
			// Initially this call goes out to the network to retrieve the
			// token, subsequent calls
			// are local.
			// R.string.gcm_defaultSenderId (the Sender ID) is typically derived
			// from google-services.json.
			// See https://developers.google.com/cloud-messaging/android/start
			// for details on this file.
			// [START get_token]
			InstanceID instanceID = InstanceID.getInstance(this);
			if (Util.getGcmToken(this) == null) {
				String token = instanceID.getToken(SENDER_ID, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
				// [END get_token]
				Log.i(TAG, "GCM Registration Token: " + token);
				Util.setGcmToken(this, token);

				// TODO: Implement this method to send any registration to your
				// app's servers.
				sendRegistrationToServer(token);

				// Subscribe to topic channels
				subscribeTopics(token);
			}
			// You should store a boolean that indicates whether the generated
			// token has been
			// sent to your server. If the boolean is false, send the token to
			// your server,
			// otherwise your server should have already received the token.
			// sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER,
			// true).apply();
			// [END register_for_gcm]
		} catch (Exception e) {
			Log.d(TAG, "Failed to complete token refresh", e);
			// If an exception happens while fetching the new token or updating
			// our registration data
			// on a third-party server, this ensures that we'll attempt the
			// update at a later time.
			// sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER,
			// false).apply();
		}
		// Notify UI that registration has completed, so the progress indicator
		// can be hidden.
		// Intent registrationComplete = new
		// Intent(QuickstartPreferences.REGISTRATION_COMPLETE);
		// LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
	}

	/**
	 * Persist registration to third-party servers.
	 *
	 * Modify this method to associate the user's GCM registration token with
	 * any server-side account maintained by your application.
	 *
	 * @param token
	 *            The new token.
	 */
	private void sendRegistrationToServer(String token) {
		// Add custom implementation, as needed.
		Log.e("kyungman", "kyungman client token : " + token);
		URL url = null;
		OutputStream os = null;
		mDeviceID = "1";
		try {
			// url = new URL("http://suah.iptime.org:9000/savegcm");
			String urltoken = "http://192.168.129.133:9000/savegcm?" + "id=" + mDeviceID + "&token=" + token;
			url = new URL(urltoken);
			Log.e("kyungman", "kyungman url : " + url.getPath());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			Log.e("kyungman", "MalformedURLException: " + e.getMessage());
			e.printStackTrace();
		}
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) url.openConnection();
			Log.e("kyungman", "kyungman conn : " + conn.getContentEncoding());
			conn.setDoOutput(true);
			// conn.setDoInput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Cache-Control", "no-cache");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Accept", "application/json");

			JSONObject insertToken = new JSONObject();
			try {
				insertToken.put("id", mDeviceID);
				insertToken.put("token", token);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				Log.e("kyungman", "JSONException: " + e.getMessage());
				e.printStackTrace();
			}
			Log.e("kyungman", "kyungman insertToken.toString() : " + insertToken.toString());
			os = conn.getOutputStream();
			os.write(insertToken.toString().getBytes());
			os.flush();
			Log.e("kyungman", "kyungman conngetURLgetPath : " + conn.getURL().getPath());
			os.close();
			conn.connect();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e("kyungman", "IOException: " + e.getMessage());
			e.printStackTrace();
		}

		Log.e("kyungman", "kyungman url : " + conn.getURL());
		Log.e("kyungman", "kyungman url toString : " + conn.toString());

	}

	private void sendGcmData() {
		// Add custom implementation, as needed.

		URL url = null;
		OutputStream os = null;

		try {
			// url = new URL("http://suah.iptime.org:9000/savegcm");
			url = new URL("http://192.168.129.133:9000/sendgcm");
			Log.e("kyungman", "kyungman url : " + url.getPath());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			Log.e("kyungman", "MalformedURLException: " + e.getMessage());
			e.printStackTrace();
		}
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) url.openConnection();
			Log.e("kyungman", "kyungman conn : " + conn.getContentEncoding());
			conn.setDoOutput(true);
			// conn.setDoInput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Cache-Control", "no-cache");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Accept", "application/json");

			os.close();
			conn.connect();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e("kyungman", "IOException: " + e.getMessage());
			e.printStackTrace();
		}

		Log.e("kyungman", "kyungman url : " + conn.getURL());
		Log.e("kyungman", "kyungman url toString : " + conn.toString());
	}

	/**
	 * Subscribe to any GCM topics of interest, as defined by the TOPICS
	 * constant.
	 *
	 * @param token
	 *            GCM token
	 * @throws IOException
	 *             if unable to reach the GCM PubSub service
	 */
	// [START subscribe_topics]
	private void subscribeTopics(String token) throws IOException {
		GcmPubSub pubSub = GcmPubSub.getInstance(this);
		for (String topic : TOPICS) {
			pubSub.subscribe(token, "/topics/" + topic, null);
		}
	}
	// [END subscribe_topics]

}