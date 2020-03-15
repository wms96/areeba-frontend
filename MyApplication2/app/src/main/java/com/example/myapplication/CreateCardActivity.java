package com.example.myapplication;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class CreateCardActivity extends AppCompatActivity {
    private EditText cardNumberET, cardNameET, cardCVVET, cardExpiryMonthET, cardExpiryYearET;
    private SharedPreferences mPreferences;

    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_card);
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void register(View view) throws JSONException {
        cardNumberET = findViewById(R.id.cardNumberET);
        cardNameET = findViewById(R.id.cardNameET);
        cardCVVET = findViewById(R.id.cardCVVET);
        cardExpiryMonthET = findViewById(R.id.cardExpiryMonthET);
        cardExpiryYearET = findViewById(R.id.cardExpiryYearET);

        if (cardNumberET.getText().toString().matches("")) {
            cardNumberET.setError(getResources().getString(R.string.value_required));
            return;
        }
        if (cardNameET.getText().toString().matches("")) {
            cardNameET.setError(getResources().getString(R.string.value_required));
            return;
        }
        if (cardCVVET.getText().toString().matches("")) {
            cardCVVET.setError(getResources().getString(R.string.value_required));
            return;
        }
        if (cardExpiryMonthET.getText().toString().matches("")) {
            cardExpiryYearET.setError(getResources().getString(R.string.value_required));
            return;
        }
        if (cardExpiryYearET.getText().toString().matches("")) {
            cardExpiryYearET.setError(getResources().getString(R.string.value_required));
            return;
        }
        JSONObject cardInfo = new JSONObject();
        cardInfo.put("card_number", cardNumberET.getText().toString().trim());
        cardInfo.put("card_name", cardNameET.getText().toString().trim());
        cardInfo.put("card_cvv", cardCVVET.getText().toString().trim());
        cardInfo.put("card_expiryMonth", cardExpiryMonthET.getText().toString().trim());
        cardInfo.put("card_expiryYear", cardExpiryYearET.getText().toString().trim());

        mPreferences = getSharedPreferences("areeba", MODE_PRIVATE);
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString("cardInfo",  cardInfo.toString()); // Storing string
        editor.commit(); // commit changes


        try {
            Toast.makeText(getApplicationContext(), "Registration Done",  Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            Log.e("Check String", e.toString() );
            e.printStackTrace();
        }
    }
}
