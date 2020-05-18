package com.amy.stockwatch;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class NameDownloader  extends AsyncTask<String, Void, String> {

    private static final String TAG = "NameLoader_Task";
    @SuppressLint("StaticFieldLeak")
    private MainActivity mainActivity;
    private static final String DATA_URL = "https://api.iextrading.com/1.0/ref-data/symbols";
    HashMap symbolNameMap = new HashMap<String, String>();
    Map matchingMap = new HashMap<String, String>();
    NameDownloader(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    protected String doInBackground(String... params) {
        Uri dataUri = Uri.parse( DATA_URL );
        String urlToUse = dataUri.toString();
        Log.d( TAG, "doInBackground: " + urlToUse );

        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL( urlToUse );
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            Log.d( TAG, "doInBackground: ResponseCode: " + conn.getResponseCode() );//aa.check if its HTTP OK
            conn.setRequestMethod( "GET" );
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader( (new InputStreamReader( is )) );
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append( line ).append( '\n' );//take the large JSON data to string
            }
            Log.d( TAG, "doInBackground: " + sb.toString() );//printed out the String
        } catch (Exception e) {
            Log.e( TAG, "doInBackground: ", e );
            return null;
        }
        return sb.toString();
    }

    @Override
    protected void onPostExecute(String s) {
        symbolNameMap = parseJSON( s );
        if (symbolNameMap != null)
            Log.d( TAG, "onCreateOptionsMenu: to get symbol & name to HashMap -> Total Firms " + symbolNameMap.size() );
    }

    private HashMap<String, String> parseJSON(String s) {

        HashMap<String, String> symbolNameMap = new HashMap<String, String>();
        try {
            JSONArray jObjMain = new JSONArray( s );
            for (int i = 0; i < jObjMain.length(); i++) {
                JSONObject JFirm = (JSONObject) jObjMain.get( i );
                String symbol = JFirm.getString( "symbol" );
                String name = JFirm.getString( "name" );
                symbolNameMap.put( symbol, name );
            }
            return symbolNameMap;
        } catch (Exception e) {
            Log.d( TAG, "parseJSON: " + e.getMessage() );
            e.printStackTrace();
        }
        return symbolNameMap;
    }

    public String[] getMatchingList(final String symbol) {
        ArrayList<String> matchList = new ArrayList<>();
        ArrayList<String> matchKeyList = new ArrayList<>();
        matchingMap.clear();
        filterMap( symbolNameMap, symbol );
        int numItems = matchingMap.size();
        String[] sArray = new String[numItems];
        String[] kArray = new String[numItems];
        Set<Map.Entry<String, String>> set = matchingMap.entrySet();
        for (Map.Entry<String, String> item : set) {
            String key = item.getKey();
            matchKeyList.add(key); }

        for (int i = 0; i < numItems; i++) {
            kArray[i] = matchKeyList.get( i ); }

       Arrays.sort( kArray, new Comparator<String>() {//sort by Key (symbol)
            @Override
            public int compare(String o1, String o2) {
                boolean firstMatches = o1.startsWith(symbol);
                boolean secondMatches = o2.startsWith(symbol);
                if (firstMatches != secondMatches) {
                    return firstMatches ? -1 : 1;
                }
                return o1.length() - o2.length();
            }} );

        for (String key: kArray) {
            matchList.add( String.format( "%s - %s", key, symbolNameMap.get( key ) ) ); }
        for (int i = 0; i < numItems; i++) {
            sArray[i] = matchList.get( i ); }
        Log.d( TAG, "getMatchingList: to get and return Matching List [size " + sArray.length +"] to main" );
        return sArray;
    }


    public void filterMap(Map<String, String> inputMap, String symbol) {
        for (String key : inputMap.keySet()) {
            if (key.contains(symbol)) {
                matchingMap.put( key, inputMap.get( key ) );
            }
        }
    }
}

