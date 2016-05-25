/** @author kyungman.yu  2015. 7. 7. **/
package com.joyful.stock;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

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
                    searchList(inputSearch.getText());
                }
                return true;
            }
        });
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

                
//                searchList(cs);
               
                
                
                SearchJongmok.this.adapter.getFilter().filter(cs);
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
    
    private void searchList(final CharSequence cs){
        Log.e("test", "onTextChanged : "+cs);
        Thread searchjob = new Thread(new Runnable() {
            
            @Override
            public void run() {
                // TODO Auto-generated method stub
                HttpURLConnection conn = null;
                URL url = null;
                BufferedReader br = null;
                try {
                    url = new URL(SEARCH_HEADER+cs+SEARCH_REAR);
                    conn = (HttpURLConnection)url.openConnection();
                    conn.setConnectTimeout(1000);
                    conn.setReadTimeout(1000);
                    conn.setRequestMethod("GET");
                    conn.setAllowUserInteraction(false);
                    conn.setRequestProperty("content-type", "text/plain; charset=euc-kr");
                    conn.setUseCaches(false);
                    
                    InputStream in = new BufferedInputStream(conn.getInputStream());
                    Log.e("test", "dom in.toString : "+ conn.getInputStream());
                    
//                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
//                        Log.d("", "[Test] conn.. :: HTTP_OK..");
//                        br = new BufferedReader(
//                                new InputStreamReader(conn.getInputStream(), "euc-kr"));
//                        for (;;) {
//                            String line = br.readLine();
//                            Log.d("", "line : "+line);
//                            if (line == null) {
//                                break;
//                            }
//                        }
//                    }
                    
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder db = factory.newDocumentBuilder();
                    Document dom = db.parse(conn.getInputStream());
                  
                    
                    dom.getDocumentElement().normalize();
                                        
                    Element order = dom.getElementById("items");
                    Log.e("test", "dom order : "+ order.getNodeName());
                    NodeList nList = order.getElementsByTagName("item");

                    Log.e("test", "dom nList : "+ nList);
                    Log.e("test", "dom nListgetLength : "+ nList.getLength());
                    for(int temp = 0 ; temp < nList.getLength() ; temp++){
                        Node nNode = nList.item(temp);
                        
                        Log.e("test", "dom getElementsByTagName korSecnNm: "+dom.getElementsByTagName("korSecnNm").item(0).getTextContent());
                        
                        if(nNode.getNodeType() == Node.ELEMENT_NODE){
                            Element mElement = (Element) nNode;
                            Log.e("test", "dom getElementsByTagName korSecnNm: "+mElement.getElementsByTagName("korSecnNm").item(0).getTextContent());
                            Log.e("test", "dom getElementsByTagName secnKacdNm: "+mElement.getElementsByTagName("secnKacdNm").item(0).getTextContent());
                        }
                    }
//                    jongmokList.setStockItem(stockName, stockCode);
                }catch(Exception ex){
                    Log.e("test", "Exception ex : "+ ex);
                }
                
//                SearchJongmok.this.adapter.getFilter().filter(cs);
            }
        });
        searchjob.start();
    
    }

}
