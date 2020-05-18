package com.amy.knowyourgovernment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;


public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, View.OnLongClickListener {

    private final String TAG = "MAIN_ACTIVITY";
    private MainActivity mainActivity = this;
    private RecyclerView recyclerView;
    private OfficerAdapter officerAdapter;
    private ArrayList<Officer> officerList = new ArrayList<>();
    private static int MY_LOCATION_REQUEST_CODE_ID = 311;
    private TextView locationView;
    private LocationManager locationManager;
    private Criteria criteria;
    public static String locationHeader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "OnCreate: " + locationHeader );
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        recyclerView = findViewById( R.id.recycler );
        locationView = findViewById( R.id.locationInput );
        officerAdapter = new OfficerAdapter( officerList, this );
        recyclerView.setAdapter( officerAdapter );
        recyclerView.setLayoutManager( new LinearLayoutManager( this ) );
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAccuracy(Criteria.ACCURACY_MEDIUM);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);
        if (doNetCheck() == true) {
            if (checkPermission() == true) {
                locationView.setText( locationHeader );
                  setLocation();
            }
        } else {
            locationView.setText( "No Data For Location" );
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /*******************************  Update Data Begins  *****************************/
    public void updateData(ArrayList<Object> myData){
        officerList.clear();
        if(!myData.isEmpty()){
            locationHeader = myData.get( 0 ).toString();
            Log.d(TAG, "updateData: " + locationHeader );
            locationView.setText(locationHeader);
            ArrayList<Officer> temp = (ArrayList<Officer>) myData.get(1);
            int len = temp.size();
            for(int i = 0; i < len; i++){
                Officer officerItem = temp.get( i );
                officerList.add(officerItem);
            }
        }
        else{ locationView.setText("No Data For Location"); }
        officerAdapter.notifyDataSetChanged();

    }
    /********************************  Update Data Ends  ******************************/




    /********************************  Location Begins  ******************************/
    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION )
                != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    MY_LOCATION_REQUEST_CODE_ID );
            return false;
        }
        else { return true; }
    }

    @SuppressLint("MissingPermission")
    private void setLocation() {

        String bestProvider = locationManager.getBestProvider(criteria, true);
        Location currentLocation = locationManager.getLastKnownLocation(bestProvider);
        Log.d(TAG, "setLocation: " + locationHeader );
        if (currentLocation != null) {
            if (locationHeader == null)
            loadDataFromLocation( currentLocation.getLatitude(), currentLocation.getLongitude());
            else{
                new OfficialDataLoader(this).execute(locationHeader);
            }
        } else {
            locationView.setText("No Data For Location");
        }
    }


    public void loadDataFromLocation(double lat, double longt){
        List<Address> addresses = null;
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(lat, longt, 1);
            Address ad = addresses.get(0);
            String zipcode = ad.getPostalCode();
            locationHeader = zipcode;
            Log.d(TAG, "loadDataFromLocation: " + locationHeader );
            new OfficialDataLoader(this).execute(zipcode);

        } catch (IOException e) { }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull
            String[] permissions, @NonNull
                    int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_LOCATION_REQUEST_CODE_ID) {
            if (permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION) &&
                    grantResults[0] == PERMISSION_GRANTED) {
                setLocation();
                return;
            }
        }
        else{ locationView.setText("No Data For Location"); }

    }

    /********************************  Location Ends ********************************/


    @Override
    public void onClick(View v){
        Log.d(TAG, "onClick : To show the individual officer data");
        int pos = recyclerView.getChildLayoutPosition(v);
        Intent intent = new Intent(MainActivity.this, OfficerActivity.class);
        Officer myOfficer = officerList.get(pos);
        intent.putExtra("location", locationView.getText().toString() );
        Bundle bundle = new Bundle();
        bundle.putSerializable("officer", myOfficer );// need to cast?
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public boolean onLongClick(View v){
        Log.d(TAG, "onLongClick : To show the individual officer data like onClick");
        int pos = recyclerView.getChildLayoutPosition(v);
        onClick(v);
        return false;
    }

    /********************************** Menu Begin **********************************/
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.action_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.searchMenu:
                Log.d(TAG, "onOptionsItemSelected: Search Menu Selected");
                if(doNetCheck() == true){
                    findLocationDialog(); }
                return true;
            case R.id.aboutMenu:
                Log.d(TAG, "onOptionsItemSelected: About Menu Selected");
                Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    /********************************** Menu End **********************************/


    /***********************  Network and Dialog Start ****************************/

    private boolean doNetCheck() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService( Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            noNetworkDialog();
            return false;
        }

        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true; }
        else {
            noNetworkDialog();
            return false;
        }
    }

    private void noNetworkDialog(){
        AlertDialog.Builder networkBuilder = new AlertDialog.Builder( this );
        networkBuilder.setMessage( "Data cannot be updated/loaded without\nan internet connection" );
        networkBuilder.setTitle( "No Network Connection" );
        AlertDialog dialog = networkBuilder.create();
        dialog.show();
    }

    private void findLocationDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder( this );
        final EditText et = new EditText( this );
        et.setInputType( InputType.TYPE_CLASS_TEXT );
        et.setGravity( Gravity.CENTER_HORIZONTAL );
        builder.setView( et );
        builder.setNegativeButton( "CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText( MainActivity.this, "Canceling location search", Toast.LENGTH_SHORT ).show();
            }
        } );
        builder.setPositiveButton( "OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText( MainActivity.this, "Searching location", Toast.LENGTH_SHORT ).show();
                String locationInput = et.getText().toString().trim();
                locationHeader = locationInput;
                new OfficialDataLoader(mainActivity).execute(locationInput);
            }
        } );
        builder.setTitle("Enter a City, State or a Zip Code");
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /***********************  Network and Dialog End ***************************/

}
