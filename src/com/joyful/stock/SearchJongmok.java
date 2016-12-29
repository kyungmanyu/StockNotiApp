/** @author kyungman.yu  2015. 7. 7. **/
package com.joyful.stock;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Xml;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class SearchJongmok extends Activity implements SearchCallback {
	// List view
	private ListView lv;

	// Listview Adapter
	ArrayAdapter<String> adapter;

	// Search EditText
	EditText inputSearch;
	public static final String SEARCH_HEADER = "http://api.seibro.or.kr/openapi/service/StockSvc/getStkIsinByNm?secnNm=";
	public static final String SEARCH_REAR = "&numOfRows=5&pageNo=1&ServiceKey=VIvbq5r67gHSKdMzbUHwLcowQq6knUBAhfRqbcRSY4lh0TgbWo9ZZ07DNTDDWT5huIcFVYdyEfWeA7NPeeTF3w%3D%3D";

	private ArrayList<String> searchlist = new ArrayList<>();
	String xmldata;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setContentView(R.layout.searchlist);

		lv = (ListView) findViewById(R.id.list_view);
		inputSearch = (EditText) findViewById(R.id.inputSearch);
		inputSearch.setFocusable(true);
		inputSearch.setFocusableInTouchMode(true);
		inputSearch.setCursorVisible(true);
		inputSearch.requestFocus();
		inputSearch.setImeOptions(EditorInfo.IME_ACTION_DONE);
		inputSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					Log.e("test", "IME_ACTION_DONE : ");

				}
				return true;
			}
		});

		adapterinit();

		/**
		 * Enabling Search Filter
		 */
		inputSearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
				// When user changed the Text

				// searchList(cs);

				SearchJongmok.this.adapter.getFilter().filter(cs);
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
			}
		});

		Button refreshBtn = (Button) this.findViewById(R.id.refreshbtn);
		refreshBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new SearchJongmokAsync(SearchJongmok.this, inputSearch.getText().toString(), SearchJongmok.this);
			}
		});

		super.onCreate(savedInstanceState);

	}

	private void adapterinit() {
		// TODO Auto-generated method stub
		// Adding items to listview
		adapter = new ArrayAdapter<String>(this, R.layout.search_main, R.id.product_name, jongmokList.getList(this));
		lv.setAdapter(adapter);

		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub

				Log.e("jongmok", "jongmok jonmok : " + parent.getAdapter().getItem(position));
				Log.e("jongmok",
						"jongmok code : " + jongmokList.getStockItem((String) parent.getItemAtPosition(position)));

				Intent editIntent = new Intent("com.joyful.stock.action.EDIT_STOCK_ITEM_ACTIVITY");
				editIntent.putExtra(EditStockItemActivity.EXTRA_STOCK_ITEM_NUM,
						(String) jongmokList.getStockItem((String) parent.getItemAtPosition(position)));
				startActivity(editIntent);
				finish();
			}
		});
	}

	public void parseXml(Reader reader) throws XmlPullParserException, IOException {
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(reader);

		parser.nextTag();
		parser.require(XmlPullParser.START_TAG, null, "menu");
		while (parser.nextTag() == XmlPullParser.START_TAG) {
			parser.require(XmlPullParser.START_TAG, null, "item");
			String itemText = parser.nextText();
			parser.nextTag(); // this call shouldn’t be necessary!
			parser.require(XmlPullParser.END_TAG, null, "item");
			Log.e("test", "menu option: " + itemText);
		}
		parser.require(XmlPullParser.END_TAG, null, "menu");
	}

	@Override
	public void updateJongmok(String name) {
		// TODO Auto-generated method stub
		Log.e("search callback", "searchcallback update : " + name);
		runOnUiThread(new updateRunnable(name));

	}

	class updateRunnable implements Runnable {

		String jongmok;

		public updateRunnable(String name) {
			// TODO Auto-generated constructor stub
			jongmok = name;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			adapterinit();
			SearchJongmok.this.adapter.getFilter().filter(jongmok);
		}

	}

}

interface SearchCallback {

	void updateJongmok(String name);
}
