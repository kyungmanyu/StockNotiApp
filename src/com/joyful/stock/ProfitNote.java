/** @author kyungman.yu  2015. 7. 7. **/
package com.joyful.stock;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

public class ProfitNote extends Activity {
    // List view
    private ListView lv;

    // Listview Adapter
    ArrayAdapter<String> adapter;

    // Search EditText
    EditText inputSearch;
    ArrayList<String> profitdate = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        setContentView(R.layout.profitlist);

        lv = (ListView)findViewById(R.id.list_view);
        inputSearch = (EditText)findViewById(R.id.inputSearch);

        Set<String> dateList = new LinkedHashSet<>();
        if (Util.getDateList(this) != null) {
            dateList = Util.getDateList(this);
            for (String date : dateList) {
                profitdate.add(Util.getProfitNote(this, date));
            }
        }

        // Adding items to listview
        adapter = new ArrayAdapter<String>(this, R.layout.profit_main, R.id.product_name,
                profitdate);
        lv.setAdapter(adapter);

        
        /**
         * Enabling Search Filter
         * */
        inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                ProfitNote.this.adapter.getFilter().filter(cs);
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
