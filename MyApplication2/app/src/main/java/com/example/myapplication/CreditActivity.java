package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import okhttp3.FormBody;
import okhttp3.RequestBody;

//implementing onclicklistener
public class CreditActivity extends AppCompatActivity implements View.OnClickListener {

    //View Objects
    private Button buttonScan;
    private TextView textViewName;
    private SharedPreferences mPreferences;
    private  JSONObject usersData, cardInfo;
    //qr code scanner object
    private IntentIntegrator qrScan;
    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit);
        StrictMode.setThreadPolicy(policy);


        //View objects
        buttonScan = (Button) findViewById(R.id.buttonScan);
        textViewName = (TextView) findViewById(R.id.textViewName);
        mPreferences = getSharedPreferences("areeba", MODE_PRIVATE);

        //initializing scan object
        qrScan = new IntentIntegrator(this);

        //attaching onclick listener
        buttonScan.setOnClickListener(this);
    }

    //Getting the scan usersData
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //if qrcode has nothing in it
            if (result.getContents() == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            } else {
                //if qr contains data
                try {
                    //converting the data to json
                    JSONObject obj = new JSONObject(result.getContents());
                    //setting values to textviews
                    textViewName.setText(obj.getString("name") + "For the Amount Of "+ obj.getString("name"));
                    usersData = new JSONObject( mPreferences.getString("results", "{}"));

                    RequestBody formBody = new FormBody.Builder()
                            .add("client_id", usersData.getString("id"))
                            .add("amount",  obj.getString("amount"))
                            .add("details", obj.getString("details"))
                            .add("transaction", obj.getString("transaction"))
                            .build();

                    try {
                        JSONObject response =  JSON.run("/credit/insert", formBody);
                        if(response.getInt("status") == 0){
                            Toast.makeText(getApplicationContext(), response.getString("message"),  Toast.LENGTH_LONG).show();
                            return;
                        }
                        Toast.makeText(getApplicationContext(), response.getString("message"),  Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Log.e("Check String", e.toString() );
                        e.printStackTrace();
                    }

                    mPreferences = getSharedPreferences("areeba", MODE_PRIVATE);
                    cardInfo = new JSONObject( mPreferences.getString("cardInfo", "{}"));
                    if(cardInfo.length() == 0){
                        Toast.makeText(getApplicationContext(), "please enter card",  Toast.LENGTH_LONG).show();
                        return;
                    }
                    JSONObject auth =  JSON.get("/users/getAuth");

                    RequestBody formBody2 = new FormBody.Builder()
                            .add("token", auth.getString("access_token"))
                            .add("cardNumber", usersData.getString("cardNumber"))
                            .add("amount",  cardInfo.getString("amount"))
                            .add("cardName", cardInfo.getString("cardName"))
                            .add("cardCVV", cardInfo.getString("cardCVV"))
                            .add("expiryMM", cardInfo.getString("expiryMM"))
                            .add("expiryYY", cardInfo.getString("expiryYY"))
                            .build();

                    try {
                        JSONObject response =  JSON.run("/credit/sendTransaction", formBody2);
                        if(response.getInt("status") == 500){
                            Toast.makeText(getApplicationContext(), "an error occurred",  Toast.LENGTH_LONG).show();
                            return;
                        }
                        Toast.makeText(getApplicationContext(), "transaction successful",  Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Log.e("Check String", e.toString() );
                        e.printStackTrace();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    //if control comes here
                    //that means the encoded format not matches
                    //in this case you can display whatever data is available on the qrcode
                    //to a toast
                    Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    @Override
    public void onClick(View view) {
        //initiating the qr code scan
        qrScan.initiateScan();
    }
}