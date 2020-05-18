package com.amy.stockwatch;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;


public class StockDownloader extends AsyncTask<String, Void, String> {
    private static final String TAG = "StockLoaderTask";
    @SuppressLint("StaticFieldLeak")
    private MainActivity mainActivity;
    private String name, symbol;
    private static final String stockURL = "https://cloud.iexapis.com/stable/stock";
    private static final String yourAPIKey = "sk_3109762cd50344c1a53841da2d3f0454";//secret token


    //Constructor
    StockDownloader(MainActivity ma) {
        mainActivity = ma;
    }


    //Build URL for Tesla: https://cloud.iexapis.com/stable/stock/TSLA/quote?token=sk_3109762cd50344c1a53841da2d3f0454
    @Override
    protected String doInBackground(String... params) {
        // 0 == symbol, 1 == name, passed from MainActivity
        this.symbol = params[0];
        this.name = params[1];
        String query = String.format( "/%s/quote?token=%s", params[0], yourAPIKey );
        Uri.Builder buildURL = Uri.parse( stockURL.concat( query ) ).buildUpon();
        String urlToUse = buildURL.build().toString();
        Log.d( TAG, "doInBackground: " + urlToUse );

        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL( urlToUse );

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod( "GET" );
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader( (new InputStreamReader( is )) );

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append( line ).append( '\n' );
            }

            Log.d( TAG, "doInBackground: " + sb.toString() );

        } catch (Exception e) {
            Log.e( TAG, "doInBackground: ", e );
            return null;
        }

        return sb.toString();
    }

    @Override
    protected void onPostExecute(String s) {//Pass Stock Object with completed info to Main
        ArrayList<Double> financeInfo = parseJSON(s);
        Stock myStock = new Stock (this.symbol, this.name,financeInfo.get(0),financeInfo.get(1),financeInfo.get(2));
        Log.d( TAG, "onPostExecute: to pass stock: " + myStock.toString() + "  back to main" );
        mainActivity.updateData(myStock);
    }


    private  ArrayList<Double>  parseJSON(String s) {//get financial info
        Log.d( TAG, "parseJSON: to get financial info");
        ArrayList<Double> financeInfo = new ArrayList<>(  );
        try {
            JSONObject jStockData = new JSONObject( s );

            String priceStr = jStockData.getString( "latestPrice" );
            double price = 0.0;
            if (priceStr != null && !priceStr.trim().isEmpty())
                price = Double.parseDouble( priceStr );

            String priceChangeStr = jStockData.getString( "change" );
            double priceChange = 0.0;
            if (priceChangeStr != null && !priceChangeStr.trim().isEmpty())
                priceChange = Double.parseDouble( priceChangeStr );

            String percentChangeStr = jStockData.getString( "changePercent" );
            double percentChange = 0.0;
            if (percentChangeStr != null && !priceStr.trim().isEmpty())
                percentChange = Double.parseDouble( percentChangeStr );

            financeInfo.add( price );
            financeInfo.add(priceChange);
            financeInfo.add( percentChange );
            return financeInfo;

        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        return null;
    }

}




