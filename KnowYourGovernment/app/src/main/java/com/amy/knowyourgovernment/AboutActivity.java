package com.amy.knowyourgovernment;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {


        private static final String TAG = "About_Activity";

        private TextView apiView;


        @Override
        protected void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_about);

            apiView = (TextView)findViewById(R.id.apiName );
            if (apiView != null) {
                apiView.setMovementMethod( LinkMovementMethod.getInstance());
            }

        }
    }

