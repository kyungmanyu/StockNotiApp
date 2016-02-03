/** @author kyungman.yu  2015. 7. 8. **/
package com.joyful.stock;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

public class jongmokList {

    // ArrayList for Listview
    static HashMap<String, String> productList = new HashMap<>();
    static HashMap<String, String> productTitlesMap = new HashMap<>();
    static ArrayList<String> products = new ArrayList<>();;
    static ArrayList<String> productItems = new ArrayList<>();;
    static Set<String> stockCodes = new LinkedHashSet<>();

    public static ArrayList<String> getList(Context context) {
        if (products.size() < 1) {
            setList(context);
        }
        return products;
    }

    public static void addStockCode(Context context, String stockItem) {

        if (Util.getStockCodes(context) != null) {
            stockCodes = Util.getStockCodes(context);
        }

        stockCodes.add(stockItem);
        Log.d("addStockCode", "add codelist : " + stockCodes.toString());
        Util.setStockCodes(context, stockCodes);
    }

    public static void removeStockCode(Context context, String stockItem) {

        if (Util.getStockCodes(context) != null) {
            stockCodes = Util.getStockCodes(context);
        }

        stockCodes.remove(stockItem);
        Log.d("removeStockCode", "remove codelist : " + stockCodes.toString());
        Util.setStockCodes(context, stockCodes);
        Log.d("removeStockCode", "check codelist : " + Util.getStockCodes(context).toString());
    }

    public static String getStockItem(String stockName) {

        return productList.get(stockName);
    }

    public static String getStockName(String stockCode) {

        return productTitlesMap.get(stockCode);
    }

    public static void setList(Context context) {

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(context.getAssets().open("stock.TXT"), "euc-kr"));

            // do reading, usually loop until end of file reading 
            String mLine = reader.readLine();
            String splitToken[];
            Log.e("stock", "mLine : " + mLine);
            products = new ArrayList<>();
            productItems = new ArrayList<>();
            productList = new HashMap<>();
            while (mLine != null) {
                //process line
                if (mLine != null) {
                    splitToken = mLine.split("//");
                    products.add(splitToken[1]);
                    productItems.add(splitToken[0]);

                    productList.put(splitToken[1], splitToken[0]);
                    productTitlesMap.put(splitToken[0], splitToken[1]);
                }
                mLine = reader.readLine();

            }

        } catch (IOException e) {
            //log the exception
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //log the exception
                    e.printStackTrace();
                }
            }
        }

    }
}
