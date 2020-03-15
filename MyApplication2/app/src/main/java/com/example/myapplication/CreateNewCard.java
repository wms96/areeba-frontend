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

public class CreateNewCard extends AppCompatActivity {
    private EditText firstNameET, lastNameET;
    private Button registerBTN;
    private SharedPreferences mPreferences;

    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_card);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void register(View view) {
        firstNameET = findViewById(R.id.firstNameET);
        lastNameET = findViewById(R.id.lastNameET);
        registerBTN = findViewById(R.id.registerBTN);

        if (firstNameET.getText().toString().matches("")) {
            firstNameET.setError(getResources().getString(R.string.value_required));
            return;
        }
        if (lastNameET.getText().toString().matches("")) {
            lastNameET.setError(getResources().getString(R.string.value_required));
            return;
        }

//        RequestBody formBody = new FormBody.Builder()
//                .add("first_name", firstNameET.getText().toString().trim())
//                .add("last_name", lastNameET.getText().toString().trim())
//                .build();
        registerBTN.setClickable(false);
        try {
            JSONObject auth =  JSON.get("/users/getAuth");
            RequestBody formBodyAuth = new FormBody.Builder()
                    .add("token", auth.getString("access_token"))
                    .build();

            JSONObject response =  JSON.run("/users/getVirtualCard", formBodyAuth);

            mPreferences = getSharedPreferences("areeba", MODE_PRIVATE);
            JSONObject results;
            String client_id = null;
            try {
                results = new JSONObject( mPreferences.getString("results", "{}"));
                client_id =  results.getString("id");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            RequestBody formBody2 = new FormBody.Builder()
                    .add("clientId", client_id)
                    .add("cardNumber", response.getString("cardNumber").trim())
                    .add("expiryDate", response.getString("expiryDate").trim())
                    .add("securityCode", response.getString("securityCode").trim())
                    .add("cardholderName", response.getString("cardholderName").trim())
                    .add("cardId", response.getString("cardId").trim())
                    .add("contractId", response.getString("contractId").trim())
                    .add("contractNumber", response.getString("contractNumber").trim())
                    .add("bankId", response.getString("bankId").trim())
                    .build();
            JSONObject response2 =  JSON.run("/users/saveVirtualCard", formBody2);
            if(response2.getInt("status") == 0){
                registerBTN.setClickable(true);
                Toast.makeText(getApplicationContext(), response.getString("message"),  Toast.LENGTH_LONG).show();
                return;
            }
            Toast.makeText(getApplicationContext(), "Registration Done",  Toast.LENGTH_LONG).show();
            firstNameET.setText("");
            lastNameET.setText("");

        } catch (Exception e) {
            registerBTN.setClickable(true);
            Log.e("Check String", e.toString() );
            e.printStackTrace();
        }
    }

}
