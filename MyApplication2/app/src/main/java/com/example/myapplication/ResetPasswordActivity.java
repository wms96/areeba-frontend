package com.example.myapplication;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.RequestBody;
public class ResetPasswordActivity extends AppCompatActivity {
    private EditText phoneET, passwordET;
    private Button resetBTN;
    // i know this isn't the correct way to do this
    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        StrictMode.setThreadPolicy(policy);

    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void reset(View view) {
        phoneET = findViewById(R.id.phoneET);
        passwordET = findViewById(R.id.passwordET);
        resetBTN = findViewById(R.id.resetBTN);
        if (phoneET.getText().toString().matches("")) {
            phoneET.setError(getResources().getString(R.string.value_required));
            return;
        }
        resetBTN.setClickable(false);
        RequestBody formBody = new FormBody.Builder()
                .add("phone_number", phoneET.getText().toString().trim())
                .build();
        try {
            JSONObject response =  JSON.run("/users/resetPassword", formBody);
            if(response.getInt("status") == 0){
                resetBTN.setClickable(true);
                Toast.makeText(getApplicationContext(), response.getString("message"),  Toast.LENGTH_LONG).show();
                return;
            }
            Toast.makeText(getApplicationContext(), "Reset Password Sent",  Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            resetBTN.setClickable(true);
            Log.e("Check String", e.toString() );
            e.printStackTrace();
        }
    }
    public void goToRegisterPage(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

}
