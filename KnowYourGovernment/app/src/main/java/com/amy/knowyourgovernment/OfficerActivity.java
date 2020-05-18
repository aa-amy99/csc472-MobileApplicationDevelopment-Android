package com.amy.knowyourgovernment;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.squareup.picasso.Picasso;


public class OfficerActivity extends AppCompatActivity {

    TextView myAddressHeader, myPhoneHeader, myEmailHeader, myWebHeader;
    TextView myLocation, myName, myRole, myParty , myAddress, myPhoneNumber, myEmail, myWeb;
    ImageView myPhoto, myYoutube, myGoogle, myTwitter, myFB, myLogo;
    Officer myOfficer;
    private static final String TAG = "OFFICER_ACTIVITY";
    private  static final String REPUBLICAN = "Republican";
    private  static final String REPUBLICAN_PARTY = "Republican Party";
    private static final String DEMOCRAT = "Democratic";
    private  static final String DEMOCRATIC_PARTY = "Democratic Party";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_officer );
        getWindow().getDecorView().setBackgroundColor(getResources().getColor(  R.color.colorBlack));
        myLocation = findViewById(R.id.locationOutput );
        myName = findViewById(R.id.nameOutput );
        myRole = findViewById(R.id.roleOutput );
        myParty = findViewById(R.id.partyOutput );
        myAddressHeader = findViewById(R.id.addressHeader);
        myAddressHeader.setText( "Address: " );
        myAddress = findViewById(R.id.addressesOutput );
        myPhoneHeader = findViewById(R.id.phoneHeader );
        myPhoneHeader.setText( ("Phone: ") );
        myPhoneNumber = findViewById(R.id.phoneOutput );
        myEmailHeader = findViewById(R.id.emailHeader );
        myEmailHeader.setText( "Email: " );
        myEmail = findViewById(R.id.emailOutput );
        myWebHeader = findViewById(R.id.webHeader );
        myWebHeader.setText( "Website: " );
        myWeb = findViewById(R.id.webOutput );
        myPhoto = findViewById(R.id.photoOutput );
        myLogo = findViewById( R.id.logoOutput );
        myFB = findViewById(R.id.fbIcon );
        myTwitter = findViewById(R.id.twitterIcon );
        myYoutube = findViewById(R.id.youtubeIcon );
        myGoogle = findViewById(R.id.googleIcon );

        setIntent();
        setOfficerHeader();
        if(doNetCheck() == true) {
            setPhoto();
        } else {
            Toast.makeText( OfficerActivity.this, "No Network Connection", Toast.LENGTH_LONG ).show();
            myPhoto.setImageResource(R.drawable.brokenimage);
        }
        setOfficerInfo();
        setSocialMedia();
        doLinkify();
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


    private void setIntent(){
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        myOfficer = (Officer) bundle.getSerializable("officer");
        myLocation.setText(intent.getStringExtra("location"));
    }

    private void setOfficerHeader() {
        Log.d(TAG, "setOfficerHeader: to set officer header");
        //Role
        if (myOfficer.getRole().equals( "No role data" )) myRole.setVisibility(View.GONE);
        else { myRole.setText( myOfficer.getRole() );}
        //Name
        if (myOfficer.getName().equals( "No name data" ))  myName.setVisibility(View.GONE);
        else { myName.setText( myOfficer.getName() ); }
        //Party
        if (myOfficer.getParty().equals( "Unknown" )) myParty.setVisibility(View.GONE);
        else {
            myParty.setText(String.format( "( %s )" ,myOfficer.getParty()));
            setPartyLogoBackground();
        }
    }

    private void setOfficerInfo(){
        Log.d(TAG, "setOfficerInfo: to set officer info");
        if (myOfficer.getAddress().equals( "No address provided" )) {
            myAddress.setVisibility(View.GONE);
            myAddressHeader.setVisibility(View.GONE);
        }
        else {
            myAddress.setText( myOfficer.getAddress());
            myAddress.setTextColor( Color.WHITE );
            myAddressHeader.setTextColor( Color.WHITE );
        }

        if (myOfficer.getPhone().equals( "No phone provided" )){
            myPhoneNumber.setVisibility(View.GONE);
            myPhoneHeader.setVisibility(View.GONE);
        }
        else {
            myPhoneNumber.setText( myOfficer.getPhone());
            myPhoneNumber.setTextColor( Color.WHITE );
            myPhoneHeader.setTextColor( Color.WHITE );
        }

        if (myOfficer.getEmail().equals( "No email provided" )) {
            myEmail.setVisibility(View.GONE);
            myEmailHeader.setVisibility(View.GONE);
        }
        else{
            myEmail.setText( myOfficer.getEmail() );
            myEmail.setTextColor( Color.WHITE );
            myEmailHeader.setTextColor( Color.WHITE );
        }

        if (myOfficer.getUrl().equals( "No url provided" )) {
            myWeb.setVisibility(View.GONE);
            myWebHeader.setVisibility(View.GONE);
        }
        else{
            myWeb.setText( myOfficer.getUrl() );
            myWeb.setTextColor( Color.WHITE );
            myWebHeader.setTextColor( Color.WHITE );
        }
    }

    private void setPartyLogoBackground(){
        Log.d(TAG, "setPartyLogoBackground: to set party logo and background");
        if(myOfficer.getParty().equals( REPUBLICAN ) || myOfficer.getParty().equals( REPUBLICAN_PARTY )){
            myLogo.setImageResource( R.drawable.rep_logo );
            getWindow().getDecorView().setBackgroundColor( getResources().getColor( R.color.colorRepublican));
        }
        else if(myOfficer.getParty().equals( DEMOCRAT ) || myOfficer.getParty().equals( DEMOCRATIC_PARTY )){
            myLogo.setImageResource( R.drawable.dem_logo );
            getWindow().getDecorView().setBackgroundColor(getResources().getColor( R.color.colorDemocratic));
        }
        else {
            myLogo.setVisibility(View.GONE);
            getWindow().getDecorView().setBackgroundColor( getResources().getColor( R.color.colorBlack));
            }
    }

    private void setPhoto(){
        Log.d(TAG, "setPhoto: to set photo view");
        myPhoto.setImageResource(R.drawable.placeholder);
        if (myOfficer.getPhoto().equals("No photoUrl provided") || myOfficer.getPhoto().equals("") )
            myPhoto.setImageResource(R.drawable.missing);
        else {
            final String myPhotoUrl = myOfficer.getPhoto();
            Picasso picasso = new Picasso.Builder(this).listener( new Picasso.Listener() {
                @Override
                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                    final String urlToUse = myPhotoUrl.replace("http:", "https:");
                    picasso.load(urlToUse)
                            .error(R.drawable.brokenimage)
                            .placeholder(R.drawable.placeholder)
                            .into(myPhoto);
                }
            }).build();

            picasso.setLoggingEnabled(true);
            picasso.load(myPhotoUrl)
                    .error(R.drawable.brokenimage)
                    .placeholder(R.drawable.placeholder)
                    .into(myPhoto);
        }
    }

    private void setSocialMedia(){
        Log.d(TAG, "setSocialMedia: to set social media view");
        myFB.setImageResource( R.drawable.facebook );
        myTwitter.setImageResource( R.drawable.twitter);
        myYoutube.setImageResource( R.drawable.youtube );
        myGoogle.setImageResource(R.drawable.googleplus);

        if(myOfficer.getFb().equals("") || myOfficer.getFb().equals("no Facebook Data"))
            myFB.setVisibility( View.GONE );

        if(myOfficer.getTwitter().equals("") || myOfficer.getTwitter().equals("no Twitter Data"))
            myTwitter.setVisibility(View.GONE);

        if(myOfficer.getYoutube().equals("") || myOfficer.getYoutube().equals("no Youtube Data") )
            myYoutube.setVisibility(View.GONE);

        if(myOfficer.getGoogle().equals("") || myOfficer.getGoogle().equals("no goolePlus Data"))
            myGoogle.setVisibility(View.GONE);

    }

    private void doLinkify(){
        Linkify.addLinks( myAddress, Linkify.ALL );
        Linkify.addLinks( myPhoneNumber,Linkify.PHONE_NUMBERS);
        Linkify.addLinks( myEmail,Linkify.EMAIL_ADDRESSES);
        Linkify.addLinks( myWeb,Linkify.WEB_URLS);
    }

    public void logoClicked(View v){
        Log.d(TAG, "logoClicked: To link to party's website");
        String demoURL = "https://democrats.org";
        String repURL = "https://www.gop.com";
        String myParty = myOfficer.getParty();
        Intent i = new Intent(Intent.ACTION_VIEW);
        if ( myParty.equals( DEMOCRATIC_PARTY )|| myParty.equals( DEMOCRAT ))
        i.setData(Uri.parse(demoURL));
        else if (myParty.equals( REPUBLICAN )|| myParty.equals( REPUBLICAN_PARTY ))
            i.setData(Uri.parse(repURL));
        startActivity(i);
    }


    public void twitterClicked(View v){
        Log.d(TAG, "twitterClicked: to link to twitter");

        Intent intent = null;
        String id = myOfficer.getTwitter();
        try {
            getPackageManager().getPackageInfo("com.twitter.android",0);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + id));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }catch (Exception e){
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://twitter.com/" + id));
        }
        startActivity(intent);
    }


    public void facebookClicked(View v){
        Log.d(TAG, "facebookClicked: to link to FB");

        String FACEBOOK_URL = "https://www.facebook.com/" + myOfficer.getFb();
        String urlToUse;
        PackageManager packageManager = getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) {
                urlToUse = "fb://facewebmodal/f?href=" + FACEBOOK_URL;
            } else {
                urlToUse = "fb://page/" + myOfficer.getFb();
            }
        } catch (PackageManager.NameNotFoundException e) {
            urlToUse = FACEBOOK_URL;
        }
        Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
        facebookIntent.setData(Uri.parse(urlToUse));
        startActivity(facebookIntent);
    }

    public void youtubeClicked(View v){
        Log.d(TAG, "youtubeClicked: to link to Youtube");
        String name = myOfficer.getYoutube();
        Intent intent = null;
        try {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage("com.google.android.youtube");
            intent.setData(Uri.parse("https://www.youtube.com/" + name));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/" + name)));
        }
    }

    public void googleplusClicked(View v){
        Log.d(TAG, "googleplusClicked: to link to GooglePlus");
        String name = myOfficer.getGoogle();
        Intent intent = null;
        try {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setClassName("com.google.android.apps.plus",
                    "com.google.android.apps.plus.phone.UrlGatewayActivity");
            intent.putExtra("customAppUri", name);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://plus.google.com/" + name)));
        }
    }

    public void photoActivityLink(View v){
        Log.d(TAG, "photoActivityLink: to direct to PhotoActivity ");
        if(myOfficer.getPhoto().equals("") || myOfficer.getPhoto().equals("No photoUrl provided") ){
            return;
        }
        else {
            Intent intent = new Intent( OfficerActivity.this, PhotoActivity.class );
            intent.putExtra( "location", myLocation.getText().toString() );
            intent.putExtra( "name", myOfficer.getName() );
            intent.putExtra( "role", myOfficer.getRole() );
            intent.putExtra( "urlPhoto", myOfficer.getPhoto() );

            if (myOfficer.getParty().equals( DEMOCRAT ) || myOfficer.getParty().equals( DEMOCRATIC_PARTY ))
                intent.putExtra( "party", "Democrat" );
            else if (myOfficer.getParty().equals( REPUBLICAN ) || myOfficer.getParty().equals( REPUBLICAN_PARTY ))
                intent.putExtra( "party", "Republican" );
            else {
                intent.putExtra( "party", "Unknown" );
            }
            startActivity( intent );
        }
    }


}
