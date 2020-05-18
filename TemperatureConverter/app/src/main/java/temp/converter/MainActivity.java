package temp.converter;


import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences myRadioPrefs;
    private RadioButton cBtn, fBtn;
    private EditText inputTemp;
    private TextView outputTemp, outputHistory, inputHead,outputHead;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cBtn = (RadioButton) findViewById(R.id.cToFbutton);
        fBtn = (RadioButton) findViewById(R.id.ftoCbutton);
        outputTemp = findViewById(R.id.tempOutput);
        outputHistory = findViewById(R.id.history);//temp output to screen
        outputHistory.setMovementMethod(new ScrollingMovementMethod());
        inputHead = findViewById(R.id.tempInputHeader);
        outputHead =  findViewById(R.id.tempOutputHeader);

        myRadioPrefs = getSharedPreferences("MY_RADIO_PREFS", Context.MODE_PRIVATE);//default mode
        cBtn.setChecked(myRadioPrefs.getBoolean("RadioC", false));
        fBtn.setChecked(myRadioPrefs.getBoolean("RadioF", true));

        if (cBtn.isChecked() == true) {
            inputHead.setText("Celsius Degrees:");
            outputHead.setText("Fahrenheit Degrees:");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putString("History", outputHistory.getText().toString());
        outState.putString("OutputTemp", outputTemp.getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {

        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey("OutputTemp")) {
            outputTemp.setText(savedInstanceState.getString("OutputTemp"));
        }
        if (savedInstanceState.containsKey("History")) {
            outputHistory.setText(savedInstanceState.getString("History"));
        }
    }

    public void groupClick1(View v) {

        SharedPreferences.Editor prefsEditor = myRadioPrefs.edit();
        if (fBtn.isChecked()) {//F is selected
            inputHead.setText("Fahrenheit Degrees:");
            outputHead.setText("Celsius Degrees:");
            prefsEditor.putBoolean("RadioF", true);
            prefsEditor.putBoolean("RadioC", false);
        }
        else {//C is selected
            inputHead.setText("Celsius Degrees:");
            outputHead.setText("Fahrenheit Degrees:");
            prefsEditor.putBoolean("RadioC", true);
            prefsEditor.putBoolean("RadioF", false);
        }
        prefsEditor.apply();
    }

    public void convertTemp(View v) {
        inputTemp = findViewById(R.id.temInput);//temp input from user
        if (inputTemp.getText().length() != 0) {
            double resTemp;
            double numTemp = Double.parseDouble(inputTemp.getText().toString().trim());
            String currOutput;

            if (fBtn.isChecked()) {
                resTemp = (numTemp - 32.0) / 1.8;
                currOutput = String.format(" %.1f  F  ==>  %.1f C%n", numTemp, resTemp);
            } else {
                resTemp = (numTemp * 1.8) + 32.0;
                currOutput = String.format(" %.1f  C  ==>  %.1f F%n", numTemp, resTemp);
            }
            String prevOutput = outputHistory.getText().toString();
            String s = currOutput + prevOutput;
            outputHistory.setText(s);
            outputTemp.setText(String.format("%,.1f", resTemp).trim());

        }
    }

    public void clearOutput(View v) {
        outputHistory.setText("");
    }
}