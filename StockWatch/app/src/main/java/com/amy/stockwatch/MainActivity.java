package com.amy.stockwatch;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
//import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;


public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, View.OnLongClickListener {

    private final String TAG = "MAIN_ACTIVITY";
    private ArrayList<Stock> stockList = new ArrayList<>();
    private RecyclerView recyclerView;
    private StockAdapter stockAdapter;
    private NameDownloader nameDownloader;
    private StockDownloader stockDownloader;
    private String symbolToAdded;
    private String name;
    private SwipeRefreshLayout swiper; // The SwipeRefreshLayout


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        recyclerView = findViewById( R.id.recycler );
        stockAdapter = new StockAdapter( stockList, this );
        recyclerView.setAdapter( stockAdapter );
        recyclerView.setLayoutManager( new LinearLayoutManager( this ) );
        nameDownloader = new NameDownloader( this );
        nameDownloader.execute();
        Log.d(TAG, "onCreate: Read Json File");
        ArrayList <Stock> tempList = doRead();

        Log.d(TAG, "onCreate: Check Network Connection");
        boolean isConnect = doNetCheck();
        if (!isConnect){
            for (Stock s: tempList){
                s.setPrice( 0.0 );
                s.setPriceChange( 0.0 );
                s.setPercentChange( 0.0 );
                stockList.add(s);
            }
            Collections.sort(stockList);
            stockAdapter.notifyDataSetChanged();
        }
        else{
            for (Stock s: tempList){
                getFinanceInfo( String.format( "%s - %s", s.getSymbol(), s.getName() )); }
        }

        Log.d(TAG, "onCreate: Swiper refreshing");
        swiper = findViewById(R.id.swiper);
        swiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                doRefresh();
            }
        });
    }

    private void doRefresh() {
        if (doNetCheck() == true) {
            Toast.makeText(this, "REFRESH: redownload data", Toast.LENGTH_SHORT).show();
            stockList.clear();
            ArrayList <Stock> tempList = doRead();
            for (Stock s: tempList){
                getFinanceInfo( String.format( "%s - %s", s.getSymbol(), s.getName() )); }
        }
        stockAdapter.notifyDataSetChanged();
        swiper.setRefreshing(false);

    }
    @Override
    protected void onPause() {
        super.onPause();
        doWrite();

    }
    @Override
    public void onClick(View view) {
        Log.d( TAG, "onClick: to go to website" );
        int pos = recyclerView.getChildLayoutPosition( view );
        Stock selection = stockList.get( pos );
        Toast.makeText( this, "Selected onClick: " + selection.getName(), Toast.LENGTH_LONG ).show();
        String symbol = selection.getSymbol().trim();
        Intent i = new Intent(Intent.ACTION_VIEW);
        String stockURL = "https://www.marketwatch.com/investing/stock/" + symbol;
        i.setData(Uri.parse(stockURL));
        startActivity(i);
    }



    @Override
    public boolean onLongClick(View view) {
        final int pos = recyclerView.getChildLayoutPosition( view );
        Log.d( TAG, "onLongClick: to delete the selected stock" );
        AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(this);
        deleteBuilder.setIcon(R.drawable.delete);
        String deletedSymbol = stockList.get(pos).getSymbol();

        deleteBuilder.setNegativeButton( "CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText( MainActivity.this, "You cancel to delete stock", Toast.LENGTH_SHORT ).show();
            }
        } );

        deleteBuilder.setPositiveButton( "DELETE", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                stockList.remove( pos );
                doWrite();//add
                stockAdapter.notifyDataSetChanged();
            }

        } );
        deleteBuilder.setMessage(String.format( "Delete Stock Symbol %s ?" , deletedSymbol));
        deleteBuilder.setTitle("Delete Stock");
        AlertDialog dialog = deleteBuilder.create();
        dialog.show();
        return true;
    }

    //For Add Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d( TAG, "onCreateOptionsMenu: to inflate ADD Menu" );
        getMenuInflater().inflate( R.menu.action_menu, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d( TAG, "onCreateOptionsMenu: to execute ADD Menu" );
        switch (item.getItemId()) {
            case R.id.addMenu:
                if (doNetCheck() == true){

                    // Single input value dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder( this );
                    final EditText et = new EditText( this );
                    et.setFilters( new InputFilter[] {new InputFilter.AllCaps(  )});
                    et.setInputType( InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS );
                    et.setGravity( Gravity.CENTER_HORIZONTAL );
                    builder.setView( et );

                    builder.setNegativeButton( "CANCEL", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Toast.makeText( MainActivity.this, "You cancel to add stock", Toast.LENGTH_SHORT ).show();
                        }
                    } );
                    builder.setPositiveButton( "OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            String stockToBeAdded = et.getText().toString().toUpperCase();
                            checkInputStock( stockToBeAdded );
                        }
                    } );
                    builder.setMessage( "Please enter Stock Symbol:" );
                    builder.setTitle( "Stock Selection" );
                    AlertDialog dialog = builder.create();//create the dialog with these above feature
                    dialog.show();
                }
                return true;//for menu
            default:
                return super.onOptionsItemSelected( item );
        }
    }

    public void getFinanceInfo(String selectedSymbolName){
        Log.d( TAG, "getFinanceInfo: to start ASYN to get finance info from StockDownloader" );
        String[] symbolNameArr = selectedSymbolName.split("-");
        symbolToAdded = symbolNameArr[0].trim();
        name = symbolNameArr[1].trim();
        stockDownloader = (StockDownloader) new StockDownloader( this ).execute( symbolToAdded, name );
    }

    public void updateData (Stock myStock){
        Log.d( TAG, "updateData: to add the stock to list and update" );
        boolean isDuplicate = false;
        for (Stock s : stockList) {
            //case 1: duplicate stock in the current list
            if (myStock.getSymbol().equals( s.getSymbol() )) {
                isDuplicate = true;
                AlertDialog.Builder duplicateBuilder = new AlertDialog.Builder( this );
                duplicateBuilder.setIcon( R.drawable.warning2 );
                duplicateBuilder.setMessage( String.format( "Stock Symbol %s is already displayed", myStock.getSymbol() ) );
                duplicateBuilder.setTitle( "Duplicate Stock" );
                AlertDialog dialog = duplicateBuilder.create();
                dialog.show();
                break;
            }
        }
        if (isDuplicate == false) {
            stockList.add( myStock );
            Collections.sort(stockList);
            doWrite();//add
        }
        stockAdapter.notifyDataSetChanged();
    }


    private void checkInputStock(String symbol) {
        final CharSequence[] matchArray = nameDownloader.getMatchingList( symbol );
        if (matchArray.length == 0) {
            AlertDialog.Builder notFoundBuilder = new AlertDialog.Builder( this );
            notFoundBuilder.setMessage( "Data for stock symbol" );
            notFoundBuilder.setTitle( "Symbol Not Found: " + symbol );
            AlertDialog dialog = notFoundBuilder.create();
            dialog.show();
        }

        else if (matchArray.length == 1) {
            String selectedSymbolName = (String) matchArray[0];
            getFinanceInfo(selectedSymbolName);
        }

        else {//multiple matches, Partly Matched
            AlertDialog.Builder matchBuilder = new AlertDialog.Builder( this );
            matchBuilder.setTitle( "Make a selection" );
            matchBuilder.setItems( matchArray, new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    String symbolName = (String) matchArray[which];
                    getFinanceInfo(symbolName);
                }

            } );
            matchBuilder.setNegativeButton( "Nevermind", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            } );
            AlertDialog dialog = matchBuilder.create();
            dialog.show();
        }
    }

    public  ArrayList<Stock> doRead() {
        stockList.clear();
        ArrayList<Stock> readTempList = new ArrayList<>();
        try{
            InputStream inFD = getApplicationContext().openFileInput("stockData.json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inFD, StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder();
            String readInStr;
            while((readInStr = reader.readLine()) != null){ builder.append(readInStr); }
            reader.close();

            JSONArray jArray = new JSONArray(builder.toString());
            int arrLen = jArray.length();
            for(int i = 0; i < arrLen; i++){
                JSONObject jsonObject = (JSONObject)jArray.get(i);
                String symbolJ = jsonObject.getString("Symbol");
                String nameJ = jsonObject.getString("Name");
                double priceJ = jsonObject.getDouble("Price");
                double priceChangeJ = jsonObject.getDouble("PriceChange");
                double percentChangeJ = jsonObject.getDouble("PercentChange");
                Stock stockItem = new Stock(symbolJ, nameJ, priceJ,priceChangeJ, percentChangeJ);
                readTempList.add(stockItem);
                Collections.sort(readTempList);
            }
        }
        catch (Exception err) { err.printStackTrace(); }
        return readTempList;
    }

    public void doWrite() {
        Log.d( TAG, "doWrite: writeJson" );
            try{
                FileOutputStream file = getApplicationContext().openFileOutput("stockData.json", Context.MODE_PRIVATE);
                JSONArray jsonArray = new JSONArray();
                for (Stock s : stockList) {
                    try {
                        JSONObject stockObj= new JSONObject();
                        stockObj.put("Symbol", s.getSymbol());
                        stockObj.put("Name", s.getName());
                        stockObj.put("Price", s.getPrice());
                        stockObj.put( "PriceChange", s.getPriceChange() );
                        stockObj.put("PercentChange", s.getPercentChange());
                        jsonArray.put(stockObj);
                    }
                    catch (JSONException err){ err.printStackTrace(); }
                }
                String jContent = jsonArray.toString();
                file.write(jContent.getBytes());
                file.close();
            }
            catch(IOException err) { err.printStackTrace();}
        }

    private boolean doNetCheck() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            noNetwork();
            return false;
        }

        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            //connect
            return true; }
        else {
            noNetwork();
            return false;
        }
    }
    private void noNetwork(){
        AlertDialog.Builder networkBuilder = new AlertDialog.Builder( this );
        networkBuilder.setMessage( "Stock cannot be updated without\nnetwork connection" );
        networkBuilder.setTitle( "No Network Connection" );
        AlertDialog dialog = networkBuilder.create();
        dialog.show();
    }
}

