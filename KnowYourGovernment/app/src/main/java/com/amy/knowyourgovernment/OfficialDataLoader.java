package com.amy.knowyourgovernment;

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
import java.util.ArrayList;

public class OfficialDataLoader extends AsyncTask<String, Void, String> {

        private static final String TAG = "OFFICIAL_DATA_LOADER";
         @SuppressLint("StaticFieldLeak")
        private MainActivity mainActivity;
        private String city, state, zipcode;
        private static final String myURL = "https://www.googleapis.com/civicinfo/v2/representatives?key=";
        private static final String yourAPIKey = ;//Google API key has been taken out for security purpose


        //Constructor
         OfficialDataLoader(MainActivity ma) {
             mainActivity = ma;
        }


        @Override
        protected String doInBackground(String... para){

            //0 = city,IL or zipcode , passed from MainActivity
            String dataURL = myURL + yourAPIKey + "&address=" ;
            String input = para[0];

            String query = dataURL.concat(input);
            Uri.Builder buildURL = Uri.parse( query ).buildUpon();
            String urlToUse = buildURL.build().toString();
            Log.d( TAG, "doInBackground: " + urlToUse );

            StringBuilder sb = new StringBuilder();
            try {
                URL url = new URL(urlToUse);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                InputStream is = conn.getInputStream();
                BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append('\n');
                }
                Log.d(TAG, "doInBackground: " + sb.toString());

            }
            catch (Exception e) { Log.e(TAG, "doInBackground: ", e);
                return null;
            }

            return sb.toString();
        }

    @Override
    protected void onPostExecute(String s){
        Log.d(TAG, "onPostExecute: To pass data back to Main");
        ArrayList<Object> myData = parseJSON(s);
        mainActivity.updateData(myData);

    }

    private ArrayList<Object> parseJSON(String s){//get officer info
            Log.d( TAG, "parseJSON: to get all officers data");
            ArrayList<Officer> jList = new ArrayList<>();
            ArrayList<Object> loadedDataList = new ArrayList<>( );
            try{
                //First Part: Normalized Location Input
                JSONObject allData = new JSONObject(s);
                JSONObject locationInput = allData.getJSONObject("normalizedInput");
                city = locationInput.getString("city");
                state = locationInput.getString("state");
                zipcode = locationInput.getString("zip");
                String location = String.format( "%s, %s %s",city,state,zipcode );
                loadedDataList.add( 0,location );

                //Second Part: Officers List
                JSONArray myOffices = allData.getJSONArray("offices");
                JSONArray myOfficials = allData.getJSONArray("officials");
                for(int i = 0;i < myOffices.length(); i++){
                    JSONObject officeJObj = myOffices.getJSONObject(i);
                    String nameRole = officeJObj.getString("name");
                    if (nameRole == null) nameRole = "No role data";
                    String myOfficialIndices = officeJObj.getString("officialIndices");
                    String [] indexStrArray = myOfficialIndices.substring(1,myOfficialIndices.length()-1).split(",");
                    int totalIndices = indexStrArray.length;
                    int [] indexIntArray = new int [totalIndices];
                    int count = 0;
                    while(count < totalIndices){
                        indexIntArray[count] = Integer.parseInt(indexStrArray[count]);
                        count ++;
                    }

                    for(int ii = 0; ii < totalIndices; ii++ ){
                        JSONObject jObj = myOfficials.getJSONObject(indexIntArray[ii]);
                        //Name
                        String name = jObj.getString("name");
                        if (name == null) name = "No name data";
                        String party, phone, url, email, photo = "";
                        String address = "";
                        String google = "";
                        String fb = "";
                        String twitter = "";
                        String youtube = "";
                        //Address
                        if(jObj.has("address")){
                            JSONObject jAddress = jObj.getJSONArray("address").getJSONObject(0);
                            if (jAddress.has("line1")) { address = address.concat(jAddress.getString( "line1" )) +"\n" ; }
                            if (jAddress.has("line2")) { address = address.concat(jAddress.getString( "line2" )) + "\n"; }
                            if (jAddress.has("line3")) {
                                if (!jAddress.getString( "line3" ).equals( "" ))
                                address = address.concat(jAddress.getString( "line3" )) + "\n";
                            }
                            if (jAddress.has("city")) { address = address.concat(jAddress.getString( "city" )) + " "; }
                            if (jAddress.has("state")) { address = address.concat(jAddress.getString( "state")) + ", "; }
                            if (jAddress.has("zip")) { address = address.concat(jAddress.getString( "zip")); }
                        }
                        else { address = "No address provided";
                        }

                        //Party
                        if (jObj.has("party")) { party = jObj.getString("party");}
                        else party = "Unknown";

                        //Phone Array
                        if (jObj.has("phones")) {
                            JSONArray jPhone = jObj.getJSONArray( "phones" );
                            phone = jPhone.get( 0 ).toString();
                        }
                        else phone = "No phone provided";

                        //Url Array
                        if (jObj.has("urls")) {
                            JSONArray jUrls = jObj.getJSONArray( "urls" );
                            url = jUrls.get( 0 ).toString();
                        }
                        else url = "No url provided";

                        //Email Array
                        if (jObj.has("emails")) {
                            JSONArray jEmails = jObj.getJSONArray( "emails" );
                            email = jEmails.get( 0 ).toString();
                        }
                        else email = "No email provided";

                        //Photo Url
                        if (jObj.has("photoUrl")) photo = jObj.getString("photoUrl");
                        else photo = "No photoUrl provided";

                        //Channel Array
                        if (jObj.has("channels")){
                            JSONArray channels = jObj.getJSONArray("channels");
                            int len = channels.length();
                            int numItem = 0;
                            while (numItem < len ){
                                String socialMedia = channels.getJSONObject(numItem).getString("type");
                                if (socialMedia.equals( "GooglePlus" )) google = channels.getJSONObject(numItem).getString("id");
                                if (socialMedia.equals( "Facebook" )) fb = channels.getJSONObject(numItem).getString("id");
                                if (socialMedia.equals( "Twitter" )) twitter = channels.getJSONObject(numItem).getString("id");
                                if (socialMedia.equals( "YouTube" )) youtube = channels.getJSONObject(numItem).getString("id");
                                numItem ++;
                            }
                        }
                        else {
                            google = "no goolePlus Data";
                            fb = "no Facebook Data";
                            twitter = "no Twitter Data";
                            youtube = "no Youtube Data";
                        }

                        Officer myOfficer = new Officer(nameRole, name, party, address, phone, url, email, photo, google, fb, twitter, youtube);
                        jList.add(myOfficer);
                    }
                }
                loadedDataList.add( 1,jList );
            return loadedDataList;

        } catch ( JSONException ex) { ex.printStackTrace(); }
            return null;
        }
}
