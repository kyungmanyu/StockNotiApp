/** @author kyungman.yu  2015. 7. 7. **/
package com.joyful.stock;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.xml.sax.XMLReader;
import org.xmlpull.v1.XmlPullParser;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

public class SearchJongmok extends Activity {
    // List view
    private ListView lv;

    // Listview Adapter
    ArrayAdapter<String> adapter;

    // Search EditText
    EditText inputSearch;
    public static final String SEARCH_HEADER = "http://api.seibro.or.kr/openapi/service/StockSvc/getStkIsinByNm?secnNm=";
    public static final String SEARCH_REAR = "&numOfRows=5&pageNo=1&ServiceKey=VIvbq5r67gHSKdMzbUHwLcowQq6knUBAhfRqbcRSY4lh0TgbWo9ZZ07DNTDDWT5huIcFVYdyEfWeA7NPeeTF3w%3D%3D";
    
    private ArrayList<String> searchlist = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        setContentView(R.layout.searchlist);
        
        lv = (ListView)findViewById(R.id.list_view);
        inputSearch = (EditText)findViewById(R.id.inputSearch);

        // Adding items to listview
        adapter = new ArrayAdapter<String>(this, R.layout.search_main, R.id.product_name,
                jongmokList.getList(this));
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub

                Log.e("jongmok", "jongmok jonmok : " + parent.getAdapter().getItem(position));
                Log.e("jongmok",
                        "jongmok code : "
                                + jongmokList.getStockItem((String)parent
                                        .getItemAtPosition(position)));



                Intent editIntent = new Intent("com.joyful.stock.action.EDIT_STOCK_ITEM_ACTIVITY");
                editIntent.putExtra(EditStockItemActivity.EXTRA_STOCK_ITEM_NUM,
                        (String)jongmokList.getStockItem((String)parent
                                .getItemAtPosition(position)));
                startActivity(editIntent);
                finish();
            }
        });
        /**
         * Enabling Search Filter
         * */
        inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                SearchJongmok.this.adapter.getFilter().filter(cs);
//                searchList(cs);
//                HttpURLConnection conn = null;
//                URL url = null;
//                BufferedReader br = null;
//                try {
//                    url = new URL(SEARCH_HEADER+cs+SEARCH_REAR);
//                    conn = (HttpURLConnection)url.openConnection();
//                    InputStream in = new BufferedInputStream(conn.getInputStream());
//                    XMLReader parser = new X
//                }catch(Exception ex){
//                    
//                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                    int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub                          
            }
        });
        super.onCreate(savedInstanceState);

    }

}
