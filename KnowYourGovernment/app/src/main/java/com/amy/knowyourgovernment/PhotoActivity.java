package com.amy.knowyourgovernment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.squareup.picasso.Picasso;

public class PhotoActivity extends AppCompatActivity {

    private static final String TAG = "PHOTO_ACTIVITY";
    String myParty;
    TextView myLocation,myRole, myName;
    ImageView myPhoto, myLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_photo );
        myLocation = findViewById( R.id.locationOutput );
        myRole = findViewById( R.id.roleOutput );
        myName = findViewById( R.id.nameOutput );
        myPhoto = findViewById( R.id.photoOutput );
        myLogo = findViewById( R.id.logoPhotoView );

        Intent intent = this.getIntent();
        String locationInput = intent.getStringExtra( "location" );
        myLocation.setText(locationInput);
        myRole.setText( intent.getStringExtra( "role" ) );
        myName.setText( intent.getStringExtra( "name" ) );
        myParty = intent.getStringExtra( "party" );

        setPartyLogoBackGround();

        if (doNetCheck() == true) { loadandSetPhoto(intent); }
        else{ myPhoto.setImageResource(R.drawable.brokenimage); }
    }

    private boolean doNetCheck() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService( Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        }
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true; }
        else {
            return false;
        }
    }

    public void logoPhotoClicked(View v){
        Log.d(TAG, "logoClicked: to link to party website");
        String demoURL = "https://democrats.org";
        String repURL = "https://www.gop.com";
        Intent i = new Intent(Intent.ACTION_VIEW);
        if ( myParty.equals("Democrat"))
            i.setData(Uri.parse(demoURL));
        else if ( myParty.equals("Republican"))
            i.setData(Uri.parse(repURL));
        startActivity(i);
    }

    private void setPartyLogoBackGround(){
        Log.d(TAG, "setPartyLogoBackGround: to set logo and background party");
        if (myParty.equals( "Democrat" )) {
            myLogo.setImageResource( R.drawable.dem_logo );
            getWindow().getDecorView().setBackgroundColor( getResources().getColor( R.color.colorDemocratic ) );
        }
        else if (myParty.equals( "Republican" )) {
            myLogo.setImageResource( R.drawable.rep_logo );
            getWindow().getDecorView().setBackgroundColor( getResources().getColor( R.color.colorRepublican ) );
        }
        else if (myParty.equals( "Unknown" )) {
            myLogo.setVisibility( View.GONE );
            getWindow().getDecorView().setBackgroundColor( getResources().getColor(R.color.colorBlack) );
        }
        else{
            myLogo.setVisibility( View.GONE );
            getWindow().getDecorView().setBackgroundColor( getResources().getColor(R.color.colorBlack) );
        }
    }

    private void loadandSetPhoto(Intent intent){
        Log.d(TAG, "loadandSetPhoto: to load photo from URL");
        final String myPhotoUrl = intent.getStringExtra("urlPhoto");
        Picasso picasso = new Picasso.Builder(this).listener( new Picasso.Listener() {
            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                final String changedUrl = myPhotoUrl.replace("http:", "https:");
                picasso.load(changedUrl)
                        .error(R.drawable.brokenimage)
                        .placeholder(R.drawable.placeholder)
                        .into(myPhoto);

            }
        }).build();
        picasso.load(myPhotoUrl)
                .error(R.drawable.brokenimage)
                .placeholder(R.drawable.placeholder)
                .into(myPhoto);
    }
}
